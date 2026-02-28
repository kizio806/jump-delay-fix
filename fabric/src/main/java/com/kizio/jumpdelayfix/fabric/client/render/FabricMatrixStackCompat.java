package com.kizio.jumpdelayfix.fabric.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Compatibility bridge for DrawContext matrix stack access across Minecraft patch versions.
 * <p>
 * Fabric 1.21.5 exposes {@code MatrixStack}, while 1.21.6+ switched DrawContext matrices to
 * {@code org.joml.Matrix3x2fStack}. This adapter resolves and caches the supported methods once
 * per runtime type and provides a tiny scoped API for push/translate/scale/pop.
 */
@Environment(EnvType.CLIENT)
public final class FabricMatrixStackCompat {

    private static final Logger LOGGER = LoggerFactory.getLogger(FabricMatrixStackCompat.class);
    private static final String[] GET_MATRICES_METHOD_NAMES = {"getMatrices", "method_51448"};
    private static final Method DRAW_CONTEXT_GET_MATRICES = findMethod(
            DrawContext.class,
            GET_MATRICES_METHOD_NAMES
    );
    private static final AtomicBoolean WARNED_INCOMPATIBLE = new AtomicBoolean(false);

    private static final ConcurrentMap<Class<?>, MatrixAccess> MATRIX_ACCESS_CACHE = new ConcurrentHashMap<>();
    private static volatile boolean matrixCompatDisabled;

    private FabricMatrixStackCompat() {
    }

    /**
     * Pushes the current DrawContext matrices, applies translation + scale, and returns a scope
     * that will pop on {@link ScopedMatrixTransform#close()}.
     *
     * @param drawContext draw context from HUD/screen render callbacks
     * @param offsetX     x translation in pixels
     * @param offsetY     y translation in pixels
     * @param scale       uniform scale factor
     * @return a closeable scope that restores previous matrix state
     */
    public static ScopedMatrixTransform pushTranslateScale(DrawContext drawContext, double offsetX, double offsetY, double scale) {
        Objects.requireNonNull(drawContext, "drawContext");

        if (matrixCompatDisabled) {
            return ScopedMatrixTransform.disabled();
        }
        if (DRAW_CONTEXT_GET_MATRICES == null) {
            disableCompat("Could not resolve DrawContext matrix accessor. Disabling HUD matrix compatibility.", null);
            return ScopedMatrixTransform.disabled();
        }

        try {
            Object matrices = invokeNoArg(DRAW_CONTEXT_GET_MATRICES, drawContext);
            MatrixAccess matrixAccess = MATRIX_ACCESS_CACHE.computeIfAbsent(matrices.getClass(), FabricMatrixStackCompat::resolveMatrixAccess);

            matrixAccess.push(matrices);
            matrixAccess.translate(matrices, offsetX, offsetY);
            matrixAccess.scale(matrices, scale);

            return ScopedMatrixTransform.active(matrices, matrixAccess);
        } catch (RuntimeException exception) {
            disableCompat("Matrix compatibility bridge failed. HUD overlay matrix transform is disabled.", exception);
            return ScopedMatrixTransform.disabled();
        }
    }

    private static MatrixAccess resolveMatrixAccess(Class<?> matrixType) {
        Method push = findRequiredMethod(matrixType, "push", "method_22903", "pushMatrix");
        Method pop = findRequiredMethod(matrixType, "pop", "method_22909", "popMatrix");

        MatrixMethod translate = findTranslateMethod(matrixType);
        MatrixMethod scale = findScaleMethod(matrixType);

        return new MatrixAccess(push, pop, translate, scale);
    }

    private static MatrixMethod findTranslateMethod(Class<?> matrixType) {
        Method method = findMethod(matrixType, new String[]{"translate", "method_22904"}, double.class, double.class, double.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.THREE_DOUBLE);
        }

        method = findMethod(matrixType, new String[]{"translate", "method_46416"}, float.class, float.class, float.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.THREE_FLOAT);
        }

        method = findMethod(matrixType, new String[]{"translate"}, double.class, double.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.TWO_DOUBLE);
        }

        method = findMethod(matrixType, new String[]{"translate"}, float.class, float.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.TWO_FLOAT);
        }

        throw unsupportedMatrixType(matrixType, "translate");
    }

    private static MatrixMethod findScaleMethod(Class<?> matrixType) {
        Method method = findMethod(matrixType, new String[]{"scale", "method_22905"}, float.class, float.class, float.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.THREE_FLOAT);
        }

        method = findMethod(matrixType, new String[]{"scale"}, double.class, double.class, double.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.THREE_DOUBLE);
        }

        method = findMethod(matrixType, new String[]{"scale"}, float.class, float.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.TWO_FLOAT);
        }

        method = findMethod(matrixType, new String[]{"scale"}, double.class, double.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.TWO_DOUBLE);
        }

        method = findMethod(matrixType, new String[]{"scale"}, float.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.ONE_FLOAT);
        }

        method = findMethod(matrixType, new String[]{"scale"}, double.class);
        if (method != null) {
            return new MatrixMethod(method, MatrixInvocation.ONE_DOUBLE);
        }

        throw unsupportedMatrixType(matrixType, "scale");
    }

    private static Method findRequiredMethod(Class<?> type, String... names) {
        Method method = findMethod(type, names);
        if (method == null) {
            throw new IllegalStateException(
                    "Unable to resolve any method " + Arrays.toString(names) + " on " + type.getName()
            );
        }
        return method;
    }

    private static Method findMethod(Class<?> type, String[] names, Class<?>... parameterTypes) {
        for (String name : names) {
            try {
                return type.getMethod(name, parameterTypes);
            } catch (NoSuchMethodException ignored) {
                // Try next candidate.
            }
        }
        return null;
    }

    private static Method findMethod(Class<?> type, String... names) {
        for (String name : names) {
            for (Method method : type.getMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == 0) {
                    return method;
                }
            }
        }
        return null;
    }

    private static IllegalStateException unsupportedMatrixType(Class<?> matrixType, String operation) {
        String availableMethods = Arrays.stream(matrixType.getMethods())
                .map(Method::getName)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

        return new IllegalStateException(
                "Unsupported DrawContext matrix type '" + matrixType.getName() + "' for operation '" + operation
                        + "'. Available methods: " + availableMethods
        );
    }

    private static Object invokeNoArg(Method method, Object target) {
        try {
            return method.invoke(target);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke method '" + method.getName() + "'.", exception);
        }
    }

    private static void disableCompat(String message, Throwable throwable) {
        matrixCompatDisabled = true;
        if (!WARNED_INCOMPATIBLE.compareAndSet(false, true)) {
            return;
        }
        if (throwable == null) {
            LOGGER.warn(message);
        } else {
            LOGGER.warn(message, throwable);
        }
    }

    private static void invokeNoArgVoid(Method method, Object target) {
        try {
            method.invoke(target);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke method '" + method.getName() + "'.", exception);
        }
    }

    private static void invokeTranslate(Method method, MatrixInvocation invocation, Object target, double x, double y) {
        try {
            switch (invocation) {
                case THREE_DOUBLE -> method.invoke(target, x, y, 0.0D);
                case THREE_FLOAT -> method.invoke(target, (float) x, (float) y, 0.0F);
                case TWO_DOUBLE -> method.invoke(target, x, y);
                case TWO_FLOAT -> method.invoke(target, (float) x, (float) y);
                default -> throw new IllegalStateException("Unsupported translate invocation mode: " + invocation);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke method '" + method.getName() + "'.", exception);
        }
    }

    private static void invokeScale(Method method, MatrixInvocation invocation, Object target, double factor) {
        try {
            switch (invocation) {
                case THREE_DOUBLE -> method.invoke(target, factor, factor, 1.0D);
                case THREE_FLOAT -> method.invoke(target, (float) factor, (float) factor, 1.0F);
                case TWO_DOUBLE -> method.invoke(target, factor, factor);
                case TWO_FLOAT -> method.invoke(target, (float) factor, (float) factor);
                case ONE_DOUBLE -> method.invoke(target, factor);
                case ONE_FLOAT -> method.invoke(target, (float) factor);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke method '" + method.getName() + "'.", exception);
        }
    }

    /**
     * Auto-closeable push/pop scope to make matrix operations exception-safe.
     */
    public static final class ScopedMatrixTransform implements AutoCloseable {

        private static final ScopedMatrixTransform DISABLED = new ScopedMatrixTransform(null, null, false);

        private final Object matrices;
        private final MatrixAccess matrixAccess;
        private final boolean active;
        private boolean closed;

        private ScopedMatrixTransform(Object matrices, MatrixAccess matrixAccess, boolean active) {
            this.matrices = matrices;
            this.matrixAccess = matrixAccess;
            this.active = active;
        }

        private static ScopedMatrixTransform active(Object matrices, MatrixAccess matrixAccess) {
            return new ScopedMatrixTransform(matrices, matrixAccess, true);
        }

        private static ScopedMatrixTransform disabled() {
            return DISABLED;
        }

        public boolean isActive() {
            return active;
        }

        @Override
        public void close() {
            if (closed || !active) {
                return;
            }

            try {
                matrixAccess.pop(matrices);
            } catch (RuntimeException exception) {
                disableCompat("Failed to restore DrawContext matrix stack. Disabling matrix compatibility.", exception);
            } finally {
                closed = true;
            }
        }
    }

    private static final class MatrixAccess {

        private final Method pushMethod;
        private final Method popMethod;
        private final MatrixMethod translateMethod;
        private final MatrixMethod scaleMethod;

        private MatrixAccess(Method pushMethod, Method popMethod, MatrixMethod translateMethod, MatrixMethod scaleMethod) {
            this.pushMethod = pushMethod;
            this.popMethod = popMethod;
            this.translateMethod = translateMethod;
            this.scaleMethod = scaleMethod;
        }

        private void push(Object matrices) {
            invokeNoArgVoid(pushMethod, matrices);
        }

        private void pop(Object matrices) {
            invokeNoArgVoid(popMethod, matrices);
        }

        private void translate(Object matrices, double x, double y) {
            invokeTranslate(translateMethod.method(), translateMethod.invocation(), matrices, x, y);
        }

        private void scale(Object matrices, double factor) {
            invokeScale(scaleMethod.method(), scaleMethod.invocation(), matrices, factor);
        }
    }

    private record MatrixMethod(Method method, MatrixInvocation invocation) {
    }

    private enum MatrixInvocation {
        THREE_DOUBLE,
        THREE_FLOAT,
        TWO_DOUBLE,
        TWO_FLOAT,
        ONE_DOUBLE,
        ONE_FLOAT
    }
}
