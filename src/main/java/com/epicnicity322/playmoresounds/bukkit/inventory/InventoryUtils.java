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

package com.epicnicity322.playmoresounds.bukkit.inventory;

import com.epicnicity322.epicpluginlib.core.tools.Version;
import com.epicnicity322.playmoresounds.bukkit.util.VersionUtils;
import com.epicnicity322.playmoresounds.core.PlayMoreSoundsCore;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public final class InventoryUtils
{
    private static final @NotNull Material glassPanel;

    static {
        if (PlayMoreSoundsCore.getServerVersion().compareTo(new Version("1.13")) < 0) {
            glassPanel = Material.valueOf("THIN_GLASS");
        } else {
            glassPanel = Material.GLASS_PANE;
        }
    }

    private InventoryUtils()
    {
    }

    /**
     * Fills a inventory with glass panes, ignoring items that are not air.
     *
     * @param inventory  The inventory to fill.
     * @param from_index The slot to start the filling.
     * @param to_index   The slot to stop the filling.
     * @return A set with all the glass panes that filled the inventory.
     */
    public static @NotNull HashSet<ItemStack> fillWithGlass(@NotNull Inventory inventory, int from_index, int to_index)
    {
        HashSet<ItemStack> glassPanes = new HashSet<>();

        if (from_index < 0 || to_index < 0 || from_index > 53 || to_index > 53) return glassPanes;

        for (int slot = from_index; slot <= to_index; ++slot) {
            if (inventory.getItem(slot) != null) continue;

            ItemStack glassPane = new ItemStack(glassPanel);
            ItemMeta meta = glassPane.getItemMeta();

            meta.setDisplayName(" ");
            if (VersionUtils.hasItemFlags()) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            glassPane.setItemMeta(meta);
            inventory.setItem(slot, glassPane);
            glassPanes.add(glassPane);
        }

        return glassPanes;
    }
}
