/*
 * Copyright (c) 2020 Christiano Rangel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.epicnicity322.playmoresounds.bukkit.command.subcommand;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.lang.MessageSender;
import com.epicnicity322.playmoresounds.bukkit.PlayMoreSounds;
import com.epicnicity322.playmoresounds.bukkit.command.CommandUtils;
import com.epicnicity322.playmoresounds.bukkit.sound.SoundManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public final class ToggleSubCommand extends Command implements Helpable
{
    private static final @NotNull MessageSender lang = PlayMoreSounds.getMessageSender();

    @Override
    public @NotNull CommandRunnable onHelp()
    {
        return (label, sender, args) -> lang.send(sender, false, lang.get("Help.Toggle").replace("<label>", label));
    }

    @Override
    public @NotNull String getName()
    {
        return "toggle";
    }

    @Override
    public @Nullable String getPermission()
    {
        return "playmoresounds.toggle";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable()
    {
        return (label, sender, args) -> lang.send(sender, lang.get("General.No Permission"));
    }

    private String getInvalidArgsMessage(String label, CommandSender sender, String[] args)
    {
        return lang.get("General.Invalid Arguments")
                .replace("<label>", label).replace("<label2>", args[0])
                .replace("<args>", (sender instanceof Player ?
                        "[" + lang.get("General.Target") + "]" : "<" + lang.get("General.Target") + ">") +
                        " [on|off|toggle]");
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args)
    {
        Boolean on = null;
        String invalidArgsMessage = getInvalidArgsMessage(label, sender, args);
        HashSet<Player> targets = CommandUtils.getTargets(sender, args, 1, invalidArgsMessage,
                "playmoresounds.toggle.others");

        if (targets == null)
            return;

        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("on"))
                on = true;
            else if (args[2].equalsIgnoreCase("off"))
                on = false;
            else if (!args[2].equalsIgnoreCase("toggle")) {
                lang.send(sender, invalidArgsMessage);
                return;
            }
        }

        if (on == null) {
            HashSet<Player> toOff = new HashSet<>();
            HashSet<Player> toOn = new HashSet<>();

            for (Player player : targets) {
                if (SoundManager.getSoundsState(player)) {
                    SoundManager.toggleSoundsState(player, false);
                    toOff.add(player);
                } else {
                    SoundManager.toggleSoundsState(player, true);
                    toOn.add(player);
                }
            }

            if (!toOff.isEmpty()) {
                String who = CommandUtils.getWho(toOff, sender);

                if (who.equals(lang.get("General.You")))
                    lang.send(sender, lang.get("Toggle.Disabled.Default"));
                else
                    lang.send(sender, lang.get("Toggle.Disabled.Player").replace("<target>", who));
            }
            if (!toOn.isEmpty()) {
                String who = CommandUtils.getWho(toOn, sender);

                if (who.equals(lang.get("General.You")))
                    lang.send(sender, lang.get("Toggle.Enabled.Default"));
                else
                    lang.send(sender, lang.get("Toggle.Enabled.Player").replace("<target>", who));
            }
        } else {
            String who = CommandUtils.getWho(targets, sender);
            String mode = "Enabled";

            if (on) {
                for (Player player : targets)
                    SoundManager.toggleSoundsState(player, true);
            } else {
                for (Player player : targets)
                    SoundManager.toggleSoundsState(player, false);

                mode = "Disabled";
            }

            if (who.equals(lang.get("General.You")))
                lang.send(sender, lang.get("Toggle." + mode + ".Default"));
            else
                lang.send(sender, lang.get("Toggle." + mode + ".Player").replace("<player>", who));
        }
    }
}
