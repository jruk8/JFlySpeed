package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import lombok.Getter;

/**
 * The two speed channels a player has: fly and walk.
 */
@Getter
public enum SpeedType {

    /**
     * Flight speed, changed via {@code Player.setFlySpeed}.
     */
    FLY("fly", SpeedPermissions.FLY),

    /**
     * Walking speed, changed via {@code Player.setWalkSpeed}.
     */
    WALK("walk", SpeedPermissions.WALK);

    private final String key;
    private final String permission;

    SpeedType(String key, String permission) {
        this.key = key;
        this.permission = permission;
    }
}
