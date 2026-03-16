package com.kizio.jumpdelayfix.common.client.hud;

import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import com.kizio.jumpdelayfix.common.model.JumpProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HudPresentationServiceTest {

    @Test
    void shouldBuildModelForAllVisibleSections() {
        JumpRuntimeConfig config = JumpRuntimeConfig.defaults();
        JumpDiagnostics diagnostics = diagnosticsFor("example.server.domain", 0.18D);

        HudPresentationModel model = HudPresentationService.build(diagnostics, config);

        assertEquals(4, model.lines().size());
        assertEquals("Profile: SMART  Ping: 55ms", model.lines().get(0).text());
        assertEquals("Rollback: 18%  Penalty: 2  ReqTicks: 3", model.lines().get(1).text());
        assertEquals("Mode: Active  Auto: true  Quality: Moderate", model.lines().get(2).text());
        assertEquals("Server: example.server.domain", model.lines().get(3).text());
        assertEquals(81, model.panelHeight());
        assertEquals(0xFFCA6D, model.qualityColor());
        assertTrue(model.showQualityBar());
        assertEquals(47, model.qualityBarFilledWidth(260));
    }

    @Test
    void shouldBuildCompactModelWhenSectionsAreHidden() {
        JumpRuntimeConfig config = JumpRuntimeConfig.defaults();
        config.setHudShowProfileAndPing(false);
        config.setHudShowRollbackAndPenalty(false);
        config.setHudShowModeAndQuality(false);
        config.setHudShowServer(false);
        config.setHudShowQualityBar(false);

        HudPresentationModel model = HudPresentationService.build(diagnosticsFor("server", 0.02D), config);

        assertTrue(model.lines().isEmpty());
        assertEquals(26, model.panelHeight());
        assertFalse(model.showQualityBar());
        assertEquals(26, HudPresentationService.panelHeight(config));
    }

    @Test
    void shouldTrimVeryLongServerIdentifier() {
        JumpRuntimeConfig config = JumpRuntimeConfig.defaults();
        JumpDiagnostics diagnostics = diagnosticsFor("very.long.server.identifier.with.port:25565", 0.01D);

        HudPresentationModel model = HudPresentationService.build(diagnostics, config);
        String serverLine = model.lines().get(3).text();

        assertTrue(serverLine.startsWith("Server: "));
        assertTrue(serverLine.endsWith("..."));
        assertEquals(39, serverLine.length());
    }

    private static JumpDiagnostics diagnosticsFor(String serverId, double rollbackRate) {
        return new JumpDiagnostics(
                serverId,
                JumpProfile.SMART,
                true,
                true,
                false,
                true,
                true,
                55,
                2,
                3,
                20,
                4,
                1,
                rollbackRate
        );
    }
}
