/*
 * This file is part of JuniperBot.
 *
 * JuniperBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBot. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.juniperbot.module.misc.commands;

import ru.juniperbot.common.worker.command.model.DiscordCommand;

@DiscordCommand(key = "discord.command.fox.key",
        description = "discord.command.fox.desc",
        group = "discord.command.group.fun",
        priority = 18)
public class FoxCommand extends AbstractPhotoCommand {

    @Override
    protected String getEndPoint() {
        return "http://wohlsoft.ru/images/foxybot/randomfox.php";
    }

    @Override
    protected String getErrorCode() {
        return "discord.command.fox.error";
    }
}
