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

package com.epicnicity322.playmoresounds.bukkit.region;

import com.epicnicity322.yamlhandler.Configuration;
import com.epicnicity322.yamlhandler.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class SoundRegion
{
    private static final @NotNull Pattern allowedRegionNameChars = Pattern.compile("^[A-Za-z0-9_]+$");
    private final @NotNull UUID id;
    private final @Nullable UUID creator;
    private final @NotNull ZonedDateTime creationDate;
    private String name;
    private @Nullable String description;
    private @NotNull Location maxDiagonal;
    private Location minDiagonal;
    private Set<Location> border;

    /**
     * Loads a sound region from a configuration file. This configuration file must have the name of an {@link UUID} and
     * contain the keys for Name, Creator, Creation Date, Description, World, Diagonals.First and Diagonals.Second (X, Y and Z).
     * <p>
     * If Creator key is not present, the creator is considered {@link org.bukkit.command.ConsoleCommandSender}.
     *
     * @param data The configuration containing all the region data.
     * @throws IllegalArgumentException If data is invalid or missing any of the required keys.
     * @throws NullPointerException     If the world does not exist anymore or is not loaded.
     */
    public SoundRegion(@NotNull Configuration data)
    {
        Supplier<IllegalArgumentException> invalidRegionData = () -> new IllegalArgumentException("The provided data does not contain valid region data.");

        Path path = data.getFilePath().orElseThrow(() -> new IllegalArgumentException("Data is not stored on a real file.")).getFileName();
        String fileName = path.toString();

        id = UUID.fromString(fileName.substring(0, fileName.indexOf(".")));
        creator = data.getString("Creator").map(UUID::fromString).orElse(null);
        creationDate = data.getString("Creation Date").map(ZonedDateTime::parse).orElseThrow(invalidRegionData);
        setName(data.getString("Name").orElseThrow(invalidRegionData));
        description = data.getString("Description").orElse(null);

        World world = Objects.requireNonNull(Bukkit.getWorld(UUID.fromString(data.getString("World").orElseThrow(invalidRegionData))), "The world this region is in does not exist or is not loaded.");
        ConfigurationSection first = data.getConfigurationSection("Diagonals.First");
        ConfigurationSection second = data.getConfigurationSection("Diagonals.Second");

        if (first == null || second == null) {
            throw invalidRegionData.get();
        }

        maxDiagonal = new Location(world, first.getNumber("X").orElseThrow(invalidRegionData).doubleValue(),
                first.getNumber("Y").orElseThrow(invalidRegionData).doubleValue(), first.getNumber("Z").orElseThrow(invalidRegionData).doubleValue());
        setMinDiagonal(new Location(world, second.getNumber("X").orElseThrow(invalidRegionData).doubleValue(),
                second.getNumber("Y").orElseThrow(invalidRegionData).doubleValue(), second.getNumber("Z").orElseThrow(invalidRegionData).doubleValue()));
    }

    /**
     * Creates a new sound region.
     *
     * @param name        The name of the region.
     * @param maxDiagonal The {@link Location} of the first diagonal of this region.
     * @param minDiagonal The {@link Location} of the second diagonal of this region.
     * @param creator     The {@link UUID} of the player who created this region, null if it was made by console.
     * @param description The description of this
     */
    public SoundRegion(@NotNull String name, @NotNull Location maxDiagonal, @NotNull Location minDiagonal,
                       @Nullable UUID creator, @Nullable String description)
    {
        id = UUID.randomUUID();
        this.creator = creator;
        this.creationDate = ZonedDateTime.now();
        this.description = description;
        this.maxDiagonal = maxDiagonal;
        setName(name);
        setMinDiagonal(minDiagonal);
    }

    /**
     * @return The exact locations of the border blocks of this region.
     */
    private Set<Location> parseBorder()
    {
        HashSet<Location> border = new HashSet<>();

        double startX = minDiagonal.getX();
        double endX = maxDiagonal.getX() + 1d;
        double startY = minDiagonal.getY();
        double endY = maxDiagonal.getY() + 1d;
        double startZ = minDiagonal.getZ();
        double endZ = maxDiagonal.getZ() + 1d;
        World world = minDiagonal.getWorld();

        for (double x = startX; x <= endX; ++x)
            for (double y = startY; y <= endY; ++y)
                for (double z = startZ; z <= endZ; ++z) {
                    boolean edgeX = x == startX || x == endX;
                    boolean edgeY = y == startY || y == endY;
                    boolean edgeZ = z == startZ || z == endZ;

                    if ((edgeX && edgeY) || (edgeZ && edgeY) || (edgeX && edgeZ))
                        border.add(new Location(world, x, y, z));
                }

        return Collections.unmodifiableSet(border);
    }

    /**
     * Checks if this region is inside the specified location.
     *
     * @param location The location to check if this region is inside.
     */
    public boolean isInside(@NotNull Location location)
    {
        return location.getWorld().equals(minDiagonal.getWorld()) &&
                location.getBlockX() >= minDiagonal.getBlockX() && location.getBlockX() <= maxDiagonal.getBlockX() &&
                location.getBlockY() >= minDiagonal.getBlockY() && location.getBlockY() <= maxDiagonal.getBlockY() &&
                location.getBlockZ() >= minDiagonal.getBlockZ() && location.getBlockZ() <= maxDiagonal.getBlockZ();
    }

    /**
     * Gets the id of this region.
     *
     * @return The id of this region.
     */
    public @NotNull UUID getId()
    {
        return id;
    }

    /**
     * Gets the name of this region.
     *
     * @return the name of this region.
     */
    public @NotNull String getName()
    {
        return name;
    }

    /**
     * Sets the name of this region.
     *
     * @param name The name you want this region to have.
     * @throws IllegalArgumentException If the name is not alpha-numeric.
     */
    public void setName(@NotNull String name)
    {
        if (!allowedRegionNameChars.matcher(name).matches())
            throw new IllegalArgumentException("Specified name is not alpha-numeric.");

        this.name = name;
    }

    /**
     * Gets the time this region was created.
     *
     * @return The time this region was created.
     */
    public @NotNull ZonedDateTime getCreationDate()
    {
        return creationDate;
    }

    /**
     * Gets the max diagonal location of this region.
     *
     * @return An immutable location of a diagonal of this region.
     */
    public @NotNull Location getMaxDiagonal()
    {
        return maxDiagonal.clone();
    }

    /**
     * Calculates the min and max coordinates of this location and second position and updates both.
     *
     * @param loc The location of a diagonal of this sound region.
     */
    public void setMaxDiagonal(@NotNull Location loc)
    {
        World world = minDiagonal.getWorld();

        if (loc.getWorld() != world)
            throw new IllegalArgumentException("First position can not be in a different world than second position.");

        int maxX = Math.max(loc.getBlockX(), maxDiagonal.getBlockX());
        int maxY = Math.max(loc.getBlockY(), maxDiagonal.getBlockY());
        int maxZ = Math.max(loc.getBlockZ(), maxDiagonal.getBlockZ());
        int minX = Math.min(loc.getBlockX(), maxDiagonal.getBlockX());
        int minY = Math.min(loc.getBlockY(), maxDiagonal.getBlockY());
        int minZ = Math.min(loc.getBlockZ(), maxDiagonal.getBlockZ());

        maxDiagonal = new Location(world, maxX, maxY, maxZ);
        minDiagonal = new Location(world, minX, minY, minZ);
        border = parseBorder();
    }

    /**
     * Gets the min diagonal location of this region.
     *
     * @return An immutable location of a diagonal of this region.
     */
    public @NotNull Location getMinDiagonal()
    {
        return minDiagonal.clone();
    }

    /**
     * Calculates the min and max coordinates of this location and max diagonal and updates both.
     *
     * @param location The location of a diagonal of this sound region.
     */
    public void setMinDiagonal(@NotNull Location location)
    {
        World world = maxDiagonal.getWorld();

        if (location.getWorld() != world)
            throw new IllegalArgumentException("Second position can not be in a different world than first position.");

        int maxX = Math.max(location.getBlockX(), maxDiagonal.getBlockX());
        int maxY = Math.max(location.getBlockY(), maxDiagonal.getBlockY());
        int maxZ = Math.max(location.getBlockZ(), maxDiagonal.getBlockZ());
        int minX = Math.min(location.getBlockX(), maxDiagonal.getBlockX());
        int minY = Math.min(location.getBlockY(), maxDiagonal.getBlockY());
        int minZ = Math.min(location.getBlockZ(), maxDiagonal.getBlockZ());

        maxDiagonal = new Location(world, maxX, maxY, maxZ);
        minDiagonal = new Location(world, minX, minY, minZ);
        border = parseBorder();
    }

    /**
     * Gets the coordinates of the border blocks of this region.
     *
     * @return The coordinates of the border of this region.
     */
    public @NotNull Set<Location> getBorder()
    {
        return border;
    }

    /**
     * Gets the {@link UUID} of the player who created this region.
     *
     * @return The region's creator {@link UUID} or null if this region was created by console.
     */
    public @Nullable UUID getCreator()
    {
        return creator;
    }

    /**
     * Gets the description of this region.
     *
     * @return The region's description or null if this region has no description.
     */
    public @Nullable String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of this region.
     *
     * @param description The description you want this region to have.
     */
    public void setDescription(@Nullable String description)
    {
        this.description = description;
    }

    /**
     * Checks if the {@link Object} is a {@link SoundRegion} and has the same properties as this one, {@link UUID}s are
     * ignored.
     *
     * @param o The object to compare.
     * @return If the object is similar to this one.
     */
    public final boolean isSimilar(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SoundRegion)) return false;

        SoundRegion that = (SoundRegion) o;

        return Objects.equals(creator, that.creator) &&
                creationDate.equals(that.creationDate) &&
                name.equals(that.name) &&
                Objects.equals(description, that.description) &&
                minDiagonal.equals(that.maxDiagonal) &&
                minDiagonal.equals(that.minDiagonal);
    }

    /**
     * Checks if the {@link Object} is a {@link SoundRegion} and has the same {@link UUID} as this one. If there's two
     * instances with the same {@link UUID} and different properties, this will return true.
     *
     * @see #isSimilar(Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SoundRegion)) return false;

        SoundRegion that = (SoundRegion) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
