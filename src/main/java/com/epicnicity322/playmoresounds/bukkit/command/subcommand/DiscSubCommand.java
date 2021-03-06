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

package com.epicnicity322.playmoresounds.bukkit.command.subcommand;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.lang.MessageSender;
import com.epicnicity322.playmoresounds.bukkit.PlayMoreSounds;
import com.epicnicity322.playmoresounds.bukkit.command.CommandUtils;
import com.epicnicity322.playmoresounds.bukkit.listener.OnPlayerInteract;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public final class DiscSubCommand extends Command implements Helpable
{
    private static final @NotNull MessageSender lang = PlayMoreSounds.getLanguage();

    @Override
    public @NotNull CommandRunnable onHelp()
    {
        return (label, sender, args) -> lang.send(sender, false, lang.get("Help.Disc").replace("<label>", label));
    }

    @Override
    public @NotNull String getName()
    {
        return "disc";
    }

    @Override
    public @Nullable String[] getAliases()
    {
        return new String[]{"musicdisc"};
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable()
    {
        return (label, sender, args) -> lang.send(sender, lang.get("General.No Permission"));
    }

    @Override
    public @Nullable String getPermission()
    {
        return "playmoresounds.disc.give";
    }

    private String getInvalidArgsMessage(String label, CommandSender sender, String[] args)
    {
        return lang.get("General.Invalid Arguments")
                .replace("<label>", label).replace("<label2>", args[0])
                .replace("<args>", "<" + lang.get("General.Id") + "> " +
                        (sender instanceof Player ? "[" + lang.get("General.Target") + "]" : "<" + lang.get("General.Target") + ">"));
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args)
    {
        String invalidArgsMessage = getInvalidArgsMessage(label, sender, args);
        HashSet<Player> targets = CommandUtils.getTargets(sender, args, 2, invalidArgsMessage,
                "playmoresounds.disc.give.others");

        if (targets == null)
            return;

        if (args.length < 2) {
            lang.send(sender, invalidArgsMessage);
            return;
        }

        ItemStack disc = OnPlayerInteract.getCustomDisc(args[1]);

        if (disc == null) {
            lang.send(sender, lang.get("Disc.Error.Not Found").replace("<id>", args[1]));
            return;
        }

        for (Player player : targets)
            player.getInventory().addItem(disc);

        lang.send(sender, lang.get("Disc.Success").replace("<id>", args[1]).replace("<target>", CommandUtils.getWho(targets, sender)));
    }
}
