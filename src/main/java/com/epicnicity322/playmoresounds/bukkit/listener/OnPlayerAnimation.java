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

package com.epicnicity322.playmoresounds.bukkit.listener;

import com.epicnicity322.playmoresounds.bukkit.PlayMoreSounds;
import com.epicnicity322.playmoresounds.bukkit.sound.PlayableRichSound;
import com.epicnicity322.playmoresounds.core.config.Configurations;
import com.epicnicity322.yamlhandler.Configuration;
import com.epicnicity322.yamlhandler.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class OnPlayerAnimation extends PMSListener
{
    private final @NotNull PlayMoreSounds plugin;
    private final @NotNull HashMap<String, PlayableRichSound> criteriaSounds = new HashMap<>();

    public OnPlayerAnimation(@NotNull PlayMoreSounds plugin)
    {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getName()
    {
        return "Player Swing";
    }

    @Override
    public void load()
    {
        criteriaSounds.clear();

        for (Map.Entry<String, Object> node : Configurations.ITEMS_SWUNG.getConfigurationHolder().getConfiguration().getNodes().entrySet()) {
            if (node.getValue() instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) node.getValue();

                if (section.getBoolean("Enabled").orElse(false) && section.contains("Sounds")) {
                    criteriaSounds.put(node.getKey(), new PlayableRichSound(section));
                }
            }
        }

        Configuration sounds = Configurations.SOUNDS.getConfigurationHolder().getConfiguration();
        boolean defaultEnabled = sounds.getBoolean(getName() + ".Enabled").orElse(false);

        if (!criteriaSounds.isEmpty() || defaultEnabled) {
            if (defaultEnabled)
                setRichSound(new PlayableRichSound(sounds.getConfigurationSection(getName())));

            if (!isLoaded()) {
                Bukkit.getPluginManager().registerEvents(this, plugin);
                setLoaded(true);
            }
        } else {
            if (isLoaded()) {
                HandlerList.unregisterAll(this);
                setLoaded(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        Player player = event.getPlayer();
        PlayableRichSound sound = getRichSound();
        String material = player.getInventory().getItemInMainHand().getType().name();

        for (Map.Entry<String, PlayableRichSound> criterion : criteriaSounds.entrySet()) {
            if (OnEntityDamageByEntity.matchesCriterion(criterion.getKey(), material)) {
                PlayableRichSound criterionSound = criterion.getValue();

                if (!event.isCancelled() || !criterionSound.isCancellable()) {
                    criterionSound.play(player);

                    if (criterionSound.getSection().getBoolean("Prevent Other Sounds.Default Sound").orElse(false))
                        sound = null;
                    if (criterionSound.getSection().getBoolean("Prevent Other Sounds.Other Criteria").orElse(false))
                        break;
                }
            }
        }

        if (sound != null)
            if (!event.isCancelled() || !sound.isCancellable())
                sound.play(player);
    }
}
