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
import com.epicnicity322.playmoresounds.bukkit.sound.PlayableSound;
import com.epicnicity322.playmoresounds.core.sound.SoundCategory;
import com.epicnicity322.playmoresounds.core.sound.SoundType;
import com.epicnicity322.playmoresounds.core.util.PMSHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

public final class PlaySubCommand extends Command implements Helpable
{
    private static final @NotNull MessageSender lang = PlayMoreSounds.getLanguage();
    private static final @NotNull HashSet<String> soundTypes = new HashSet<>();

    static {
        for (SoundType type : SoundType.values()) {
            soundTypes.add(type.name());
        }
    }

    @Override
    public @NotNull CommandRunnable onHelp()
    {
        return (label, sender, args) -> lang.send(sender, false, lang.get("Help.Play").replace("<label>", label));
    }

    @Override
    public @NotNull String getName()
    {
        return "play";
    }

    @Override
    public int getMinArgsAmount()
    {
        return 2;
    }

    @Override
    public @Nullable String getPermission()
    {
        return "playmoresounds.play";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable()
    {
        return (label, sender, args) -> lang.send(sender, lang.get("General.No Permission"));
    }

    @Override
    protected @Nullable CommandRunnable getNotEnoughArgsRunnable()
    {
        return (label, sender, args) -> lang.send(sender, getInvalidArgsMessage(label, sender, args));
    }

    private String getInvalidArgsMessage(String label, CommandSender sender, String[] args)
    {
        return lang.get("General.Invalid Arguments").replace("<label>", label)
                .replace("<label2>", args[0]).replace("<args>", "<" +
                        lang.get("Play.Sound") + "> " + (sender instanceof Player ? "[" + lang.get("General.Player")
                        + "]" : "<" + lang.get("General.Player") + ">") + " [" + lang.get("Play.Volume") + "] [" +
                        lang.get("Play.Pitch") + "]");
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args)
    {
        HashSet<Player> targets = CommandUtils.getTargets(sender, args, 2,
                getInvalidArgsMessage(label, sender, args), "playmoresounds.play.others");

        if (targets == null)
            return;

        String sound = args[1];

        if (soundTypes.contains(sound.toUpperCase(Locale.ROOT))) {
            SoundType type = SoundType.valueOf(sound.toUpperCase(Locale.ROOT));
            Optional<String> versionSound = type.getSound();

            if (versionSound.isPresent()) {
                sound = versionSound.get();
            } else {
                lang.send(sender, lang.get("Play.Error.Unavailable").replace("<sound>", sound));
                return;
            }
        } else if (!PMSHelper.isNamespacedKey(sound)) {
            lang.send(sender, lang.get("Play.Error.Invalid Sound").replace("<sound>", sound));
            return;
        }

        String who = CommandUtils.getWho(targets, sender);
        float volume = 10;
        float pitch = 1;

        if (args.length > 3) {
            try {
                volume = Float.parseFloat(args[3]);
            } catch (NumberFormatException e) {
                lang.send(sender, lang.get("General.Not A Number").replace("<number>", args[3]));
                return;
            }

            if (args.length > 4)
                try {
                    pitch = Float.parseFloat(args[4]);
                } catch (NumberFormatException e) {
                    lang.send(sender, lang.get("General.Not A Number").replace("<number>", args[4]));
                    return;
                }
        }

        PlayableSound pmsSound = new PlayableSound(sound, SoundCategory.MASTER, volume, pitch, 0, null);

        for (Player player : targets)
            pmsSound.play(player);

        lang.send(sender, lang.get("Play.Success.Default").replace("<sound>", sound).replace(
                "<player>", who).replace("<volume>", Float.toString(volume)).replace(
                "<pitch>", Float.toString(pitch)));
    }
}
