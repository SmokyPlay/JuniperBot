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
package ru.juniperbot.module.mafia;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.juniperbot.common.worker.event.intercept.Filter;
import ru.juniperbot.common.worker.event.intercept.FilterChain;
import ru.juniperbot.common.worker.event.intercept.MemberMessageFilter;
import ru.juniperbot.common.worker.message.service.MessageService;
import ru.juniperbot.module.mafia.model.MafiaInstance;
import ru.juniperbot.module.mafia.model.MafiaState;
import ru.juniperbot.module.mafia.service.MafiaService;

import java.util.Objects;

@Slf4j
@Order(Filter.PRE_FILTER)
@Component
public class MafiaFilter extends MemberMessageFilter {

    @Autowired
    private MafiaService mafiaService;

    @Autowired
    private MessageService messageService;

    @Override
    public void doInternal(GuildMessageReceivedEvent event, FilterChain<GuildMessageReceivedEvent> chain) {
        MafiaInstance instance = mafiaService.getRelatedInstance(event.getChannel().getIdLong());
        if (instance != null && !instance.isInState(MafiaState.FINISH)) {
            boolean isPlayer = instance.isPlayer(event.getAuthor());
            if (isPlayer) {
                instance.tick();
            }

            Member selfMember = event.getGuild().getSelfMember();
            if (!event.getMember().equals(selfMember)
                    && !instance.isInState(MafiaState.CHOOSING)
                    && !Objects.equals(event.getChannel().getIdLong(), instance.getGoonChannelId())
                    && selfMember.hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE)) {
                if (instance.isInState(MafiaState.DAY)) {
                    if (!isPlayer) {
                        messageService.delete(event.getMessage());
                        return;
                    }
                } else {
                    messageService.delete(event.getMessage());
                    return;
                }
            }
        }
        chain.doFilter(event);
    }
}
