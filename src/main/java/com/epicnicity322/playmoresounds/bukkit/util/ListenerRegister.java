package com.epicnicity322.playmoresounds.bukkit.util;

import com.epicnicity322.playmoresounds.bukkit.PlayMoreSounds;
import com.epicnicity322.playmoresounds.bukkit.listener.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ListenerRegister
{
    private static final @NotNull HashSet<PMSListener> listeners = new HashSet<>();

    static {
        PlayMoreSounds.addOnInstanceRunnable(() -> {
            PlayMoreSounds instance = PlayMoreSounds.getInstance();

            listeners.add(new OnAsyncPlayerChat(instance));
            listeners.add(new OnCraftItem(instance));
            listeners.add(new OnEntityDamageByEntity(instance));
            listeners.add(new OnFurnaceExtract(instance));
            listeners.add(new OnInventoryClick(instance));
            listeners.add(new OnInventoryClose(instance));
            listeners.add(new OnPlayerBedEnter(instance));
            listeners.add(new OnPlayerBedLeave(instance));
            listeners.add(new OnPlayerCommandPreprocess(instance));
            listeners.add(new OnPlayerDeath(instance));
            listeners.add(new OnPlayerDropItem(instance));
            listeners.add(new OnPlayerGameModeChange(instance));
            listeners.add(new OnPlayerItemHeld(instance));
            listeners.add(new OnPlayerKick(instance));
            listeners.add(new OnPlayerLevelChange(instance));
            listeners.add(new OnPlayerToggleFlight(instance));
            listeners.add(new OnPlayerToggleSneak(instance));
            //listeners.add(new OnRegionEnterLeave(instance));
        });
    }

    private ListenerRegister()
    {
    }

    /**
     * Adds a listener to the list of listeners to be loaded on {@link #loadListeners()}.
     *
     * @param listener The listener to add to be registered.
     */
    public static void addListener(@NotNull PMSListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the list of listeners to be loaded on {@link #loadListeners()}.
     *
     * @param listener The listener to remove.
     */
    public static void removeListener(@NotNull PMSListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * @return A set with all the listeners that are being loaded on {@link #loadListeners()}.
     */
    public static @NotNull Set<PMSListener> getListeners()
    {
        return Collections.unmodifiableSet(listeners);
    }

    /**
     * Registers all sound listeners.
     *
     * @return The amount of listeners that were loaded.
     */
    public static int loadListeners()
    {
        int loadedListeners = 0;

        for (PMSListener listener : listeners) {
            try {
                listener.load();

                if (listener.isLoaded())
                    ++loadedListeners;
            } catch (Exception ex) {
                PlayMoreSounds.getPMSLogger().log("&cCould not load the listener " + listener.getName() + ".");
                PlayMoreSounds.getErrorLogger().report(ex, listener.getName() + " listener load exception:");
            }
        }

        return loadedListeners;
    }
}