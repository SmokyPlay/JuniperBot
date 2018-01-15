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
package ru.caramel.juniperbot.core.listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import ru.caramel.juniperbot.core.model.DiscordEvent;
import ru.caramel.juniperbot.core.service.GuildResolverService;
import ru.caramel.juniperbot.core.service.LocaleService;

public abstract class DiscordEventListener extends ListenerAdapter implements ApplicationListener<DiscordEvent> {

    @Autowired
    private GuildResolverService resolverService;

    @Autowired
    private LocaleService localeService;

    @Override
    public void onApplicationEvent(DiscordEvent event) {
        Guild guild = resolverService.getGuild(event.getSource());
        if (guild != null) {
            localeService.initLocale(guild);
        }
        onEvent(event.getSource());
    }
}
