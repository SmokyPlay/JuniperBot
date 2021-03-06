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
package ru.juniperbot.common.worker.modules.audit.provider;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.juniperbot.common.persistence.entity.AuditAction;
import ru.juniperbot.common.persistence.entity.AuditConfig;
import ru.juniperbot.common.persistence.entity.base.NamedReference;
import ru.juniperbot.common.persistence.repository.AuditConfigRepository;
import ru.juniperbot.common.worker.event.service.ContextService;
import ru.juniperbot.common.worker.message.service.MessageService;
import ru.juniperbot.common.worker.modules.audit.service.AuditService;
import ru.juniperbot.common.worker.shared.service.DiscordService;

import java.time.Instant;
import java.util.Map;

public abstract class LoggingAuditForwardProvider implements AuditForwardProvider {

    @Autowired
    protected DiscordService discordService;

    @Autowired
    protected AuditService auditService;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected ContextService contextService;

    @Autowired
    protected AuditConfigRepository configRepository;

    @Override
    @Transactional
    public void send(AuditConfig config, AuditAction action, Map<String, byte[]> attachments) {
        Class<?> clazz = this.getClass();
        if (config.getForwardChannelId() == null
                || !config.isForwardEnabled()
                || CollectionUtils.isEmpty(config.getForwardActions())
                || !config.getForwardActions().contains(action.getActionType())
                || !clazz.isAnnotationPresent(ForwardProvider.class)
                || clazz.getAnnotation(ForwardProvider.class).value() != action.getActionType()) {
            return;
        }
        if (!discordService.isConnected(action.getGuildId())) {
            return;
        }
        Guild guild = discordService.getGuildById(action.getGuildId());
        if (guild == null) {
            return;
        }
        Member self = guild.getSelfMember();
        TextChannel channel = guild.getTextChannelById(config.getForwardChannelId());
        if (channel == null || !self.hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
            return;
        }

        contextService.withContext(guild, () -> {
            EmbedBuilder embedBuilder = messageService.getBaseEmbed();
            MessageBuilder messageBuilder = new MessageBuilder();
            build(action, messageBuilder, embedBuilder);
            if (!embedBuilder.isEmpty()) {
                embedBuilder.setTimestamp(Instant.now());
                if (action.getActionType().getColor() != null) {
                    embedBuilder.setColor(action.getActionType().getColor());
                }
                messageBuilder.setEmbed(embedBuilder.build());
            }
            if (!messageBuilder.isEmpty()) {
                MessageAction messageAction = channel.sendMessage(messageBuilder.build());
                if (attachments != null && self.hasPermission(channel, Permission.MESSAGE_ATTACH_FILES)) {
                    for (Map.Entry<String, byte[]> entry : attachments.entrySet()) {
                        messageAction.addFile(entry.getValue(), entry.getKey());
                    }
                }
                messageAction.queue();
            }
        });
    }

    protected void addChannelField(AuditAction action, EmbedBuilder embedBuilder) {
        if (action.getChannel() != null) {
            embedBuilder.addField(messageService.getMessage("audit.channel.title"),
                    getReferenceContent(action.getChannel(), true), true);
        }
    }

    protected String getReferenceContent(NamedReference reference, boolean channel) {
        return messageService.getMessage("audit.reference.content",
                reference.getName(),
                channel ? reference.getAsChannelMention() : reference.getAsUserMention());
    }

    protected String getReferenceShortContent(NamedReference reference) {
        return messageService.getMessage("audit.reference.short.content", reference.getName());
    }

    protected abstract void build(AuditAction action, MessageBuilder messageBuilder, EmbedBuilder embedBuilder);
}
