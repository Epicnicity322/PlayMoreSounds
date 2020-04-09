package com.epicnicity322.playmoresounds.bukkit.listener;

import com.epicnicity322.playmoresounds.bukkit.sound.RichSound;
import com.epicnicity322.playmoresounds.bukkit.util.PMSHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class OnPlayerBedEnter implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBedEnter(PlayerBedEnterEvent event)
    {
        ConfigurationSection section = PMSHelper.getConfig("sounds").getConfigurationSection("Bed Enter");
        RichSound sound = new RichSound(section);

        if (!event.isCancelled() || !sound.isCancellable()) {
            sound.play(event.getPlayer());
        }
    }
}
