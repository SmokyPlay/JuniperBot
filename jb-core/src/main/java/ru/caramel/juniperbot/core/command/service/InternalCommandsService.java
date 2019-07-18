/*
 * This file is part of JuniperBotJ.
 *
 * JuniperBotJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBotJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBotJ. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.caramel.juniperbot.core.command.service;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import ru.caramel.juniperbot.core.command.model.Command;
import ru.caramel.juniperbot.core.command.model.CommandHandler;
import ru.caramel.juniperbot.core.command.persistence.CommandConfig;

public interface InternalCommandsService extends CommandsService, CommandHandler {

    String EXECUTIONS_METER = "commands.executions.rate";

    String EXECUTIONS_COUNTER = "commands.executions.persist";

    boolean isApplicable(Command command, CommandConfig commandConfig, User user, Member member, TextChannel channel);

    boolean isRestricted(String rawKey, TextChannel channel, Member member);

}