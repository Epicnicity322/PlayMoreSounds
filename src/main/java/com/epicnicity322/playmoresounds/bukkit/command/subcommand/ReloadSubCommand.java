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
import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import com.epicnicity322.playmoresounds.bukkit.PlayMoreSounds;
import com.epicnicity322.playmoresounds.core.PlayMoreSoundsCore;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class ReloadSubCommand extends Command implements Helpable
{
    private static final @NotNull MessageSender lang = PlayMoreSounds.getLanguage();

    @Override
    public @NotNull CommandRunnable onHelp()
    {
        return (label, sender, args) -> lang.send(sender, false, lang.get("Help.Reload").replace("<label>", label));
    }

    @Override
    public @NotNull String getName()
    {
        return "reload";
    }

    @Override
    public @Nullable String[] getAliases()
    {
        return new String[]{"rl"};
    }

    @Override
    public @Nullable String getPermission()
    {
        return "playmoresounds.reload";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable()
    {
        return (label, sender, args) -> lang.send(sender, lang.get("General.No Permission"));
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args)
    {
        Collection<Exception> exceptions = PlayMoreSounds.reload().values();

        if (exceptions.isEmpty()) {
            lang.send(sender, lang.get("Reload.Success"));
        } else {
            lang.send(sender, lang.get("Reload.Error"));

            if (!(sender instanceof ConsoleCommandSender)) {
                PlayMoreSounds.getConsoleLogger().log(lang.get("Reload.Error"), ConsoleLogger.Level.ERROR);
            }

            for (Exception exception : exceptions) {
                PlayMoreSoundsCore.getErrorHandler().report(exception, "Reload Config Exception:");
            }
        }
    }
}
