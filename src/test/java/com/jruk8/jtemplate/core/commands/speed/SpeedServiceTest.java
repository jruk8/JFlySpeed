package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.configs.PluginConfig;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.floatThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpeedServiceTest {

    private static SpeedService service() {
        return new SpeedService(new PluginConfig());
    }

    @Nested
    @DisplayName("setSpeed()")
    class SetTests {

        @Test
        @DisplayName("should convert the scale value to Bukkit range for fly speed")
        void shouldSetFlySpeed() {
            Player player = mock(Player.class);

            service().setSpeed(player, SpeedType.FLY, 1.0);

            verify(player).setFlySpeed(floatThat(value -> Math.abs(value - 0.1f) < 1e-6));
        }

        @Test
        @DisplayName("should convert the scale value to Bukkit range for walk speed")
        void shouldSetWalkSpeed() {
            Player player = mock(Player.class);

            service().setSpeed(player, SpeedType.WALK, 10.0);

            verify(player).setWalkSpeed(floatThat(value -> Math.abs(value - 1.0f) < 1e-6));
        }
    }

    @Nested
    @DisplayName("resetSpeed()")
    class ResetTests {

        @Test
        @DisplayName("should restore the configured fly default")
        void shouldResetFlySpeed() {
            Player player = mock(Player.class);

            double applied = service().resetSpeed(player, SpeedType.FLY);

            assertEquals(SpeedValues.DEFAULT_FLY_SCALE, applied);
            verify(player).setFlySpeed(floatThat(value -> Math.abs(value - 0.1f) < 1e-6));
        }

        @Test
        @DisplayName("should clamp a misconfigured walk default into range")
        void shouldClampMisconfiguredDefault() {
            var config = new PluginConfig();
            config.getSpeed().setDefaultWalkSpeed(99.0);
            Player player = mock(Player.class);

            double applied = new SpeedService(config).resetSpeed(player, SpeedType.WALK);

            assertEquals(SpeedValues.MAX_SCALE, applied);
            verify(player).setWalkSpeed(floatThat(value -> Math.abs(value - 1.0f) < 1e-6));
        }
    }

    @Nested
    @DisplayName("currentScale()")
    class CurrentTests {

        @Test
        @DisplayName("should read fly speed back on the user scale")
        void shouldReadFlyScale() {
            Player player = mock(Player.class);
            when(player.getFlySpeed()).thenReturn(0.1f);

            assertEquals(1.0, service().currentScale(player, SpeedType.FLY), 1e-6);
        }

        @Test
        @DisplayName("should clamp an out-of-range Bukkit value into range")
        void shouldClampNegativeBukkitValue() {
            Player player = mock(Player.class);
            when(player.getWalkSpeed()).thenReturn(-0.5f);

            assertEquals(SpeedValues.MIN_SCALE, service().currentScale(player, SpeedType.WALK), 1e-9);
        }
    }
}
