package com.epicnicity322.playmoresounds.bukkit.sound.events;

import com.epicnicity322.playmoresounds.bukkit.sound.Sound;
import com.epicnicity322.playmoresounds.bukkit.sound.SoundOptions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * When a PlayMoreSounds's sound is about to play but isn't checked yet. This is called before the checks for
 * permission, location and toggle also before the delay is started.
 * You may use this to change the {@link SoundOptions} and check when a sound is about to be played.
 *
 * @see PlaySoundEvent
 */
public class PrePlaySoundEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Player player;
    private Location location;
    private Sound sound;

    public PrePlaySoundEvent(@Nullable Player player, @NotNull Location location, @NotNull Sound sound)
    {
        this.player = player;
        this.location = location;
        this.sound = sound;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean value)
    {
        cancelled = value;
    }

    /**
     * Gets the player who played the sound.
     *
     * @return The player who played the sound. Null if isn't a player-based sound.
     */
    @Nullable
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Gets the location where the sound was requested to play.
     * <p>
     * Be aware that this is not the final location. The final location is calculated later when
     * {@link SoundOptions#isEyeLocation()} and {@link SoundOptions#getRelativeLocation()} are taken into account.
     *
     * @return The requested sound location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Changes the location where the sound was requested to play. If you want to set a precise location, you must
     * remove relativePositions and disable eyeLocation from sound options. To do that just use the method
     * {@link PrePlaySoundEvent#setPreciseLocation(Location)}.
     *
     * @param location The location you want to change to.
     * @see PrePlaySoundEvent#setPreciseLocation(Location)
     */
    public void setLocation(@NotNull Location location)
    {
        this.location = location;
    }

    /**
     * This changes the location where the sound should play precisely by removing Relative Location and Eye Location from
     * SoundOptions.
     *
     * @param preciseLocation The location you want to change to.
     */
    public void setPreciseLocation(@NotNull Location preciseLocation)
    {
        SoundOptions options = getSound().getOptions();

        options.setIfEyeLocation(false);
        options.setRelativeLocation(null);

        location = preciseLocation;
    }

    /**
     * Gets the instance used to play the sound. You may use this to change or check what are the sound properties. You
     * can also play the sound again using this instance, making this event be called over and over... It's your choice.
     *
     * @return The instance of the sound.
     */
    public Sound getSound()
    {
        return sound;
    }
}