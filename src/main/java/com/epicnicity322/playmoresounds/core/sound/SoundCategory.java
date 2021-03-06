/*
 * PlayMoreSounds - A bukkit plugin that manages and plays sounds.
 * Copyright (C) 2021 Christiano Rangel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.epicnicity322.playmoresounds.core.sound;

import com.epicnicity322.playmoresounds.core.PlayMoreSoundsCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.effect.sound.SoundCategories;

public enum SoundCategory
{
    AMBIENT("AMBIENT", "AMBIENT"),
    BLOCK("BLOCK", "BLOCKS"),
    HOSTILE("HOSTILE", "HOSTILE"),
    MASTER("MASTER", "MASTER"),
    MUSIC("MUSIC", "MUSIC"),
    NEUTRAL("NEUTRAL", "NEUTRAL"),
    PLAYER("PLAYER", "PLAYERS"),
    RECORD("RECORD", "RECORDS"),
    VOICE("VOICE", "VOICE"),
    WEATHER("WEATHER", "WEATHER");

    private @Nullable org.spongepowered.api.effect.sound.SoundCategory spongeValue;
    private @Nullable org.bukkit.SoundCategory bukkitValue;

    SoundCategory(@NotNull String spongeValue, @NotNull String bukkitValue)
    {
        if (PlayMoreSoundsCore.getPlatform() == PlayMoreSoundsCore.Platform.SPONGE) {
            try {
                this.spongeValue = (org.spongepowered.api.effect.sound.SoundCategory) SoundCategories.class.getField(spongeValue).get(null);
            } catch (Exception ignored) {
            }
        } else if (PlayMoreSoundsCore.getPlatform() == PlayMoreSoundsCore.Platform.BUKKIT && StaticFields.hasSoundCategory) {
            this.bukkitValue = org.bukkit.SoundCategory.valueOf(bukkitValue);
        }
    }

    public @Nullable org.bukkit.SoundCategory asBukkit()
    {
        return bukkitValue;
    }

    public @Nullable org.spongepowered.api.effect.sound.SoundCategory asSponge()
    {
        return spongeValue;
    }

    private static final class StaticFields
    {
        private static boolean hasSoundCategory = false;

        static {
            try {
                Class.forName("org.bukkit.SoundCategory");
                hasSoundCategory = true;
            } catch (ClassNotFoundException ignored) {
            }
        }
    }
}
