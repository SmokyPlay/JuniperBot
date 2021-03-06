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
package ru.juniperbot.worker.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import ru.juniperbot.common.worker.event.DiscordEvent;
import ru.juniperbot.common.worker.event.listeners.DiscordEventListener;
import ru.juniperbot.common.worker.metrics.service.StatisticsService;

@DiscordEvent
public class StatisticsListener extends DiscordEventListener {

    @Autowired
    private StatisticsService statisticsService;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        statisticsService.notifyProviders(event.getJDA());
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        statisticsService.notifyProviders(event.getJDA());
    }

    @Override
    public void onReady(ReadyEvent event) {
        statisticsService.notifyProviders(event.getJDA());
    }
}
