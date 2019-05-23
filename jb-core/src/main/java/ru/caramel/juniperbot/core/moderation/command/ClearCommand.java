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
package ru.caramel.juniperbot.core.moderation.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RequestFuture;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import ru.caramel.juniperbot.core.audit.service.ActionsHolderService;
import ru.caramel.juniperbot.core.command.model.BotContext;
import ru.caramel.juniperbot.core.command.model.DiscordCommand;
import ru.caramel.juniperbot.core.common.model.exception.DiscordException;
import ru.caramel.juniperbot.core.common.model.exception.ValidationException;
import ru.caramel.juniperbot.core.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DiscordCommand(key = "discord.command.mod.clear.key",
        description = "discord.command.mod.clear.desc",
        group = "discord.command.group.moderation",
        source = ChannelType.TEXT,
        permissions = {
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_HISTORY
        }
)
public class ClearCommand extends ModeratorCommandAsync {

    private static final int MAX_MESSAGES = 1000;

    private static final Pattern COUNT_PATTERN = Pattern.compile("^(\\d+)");

    @Autowired
    private ActionsHolderService actionsHolderService;

    @Override
    protected void doCommandAsync(MessageReceivedEvent event, BotContext context, String query) throws DiscordException {
        TextChannel channel = event.getTextChannel();
        List<Message> messages;

        int number = getCount(query);
        boolean mention = CollectionUtils.isNotEmpty(event.getMessage().getMentionedUsers());
        if (mention) {
            Member mentioned = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
            if (mentioned == null) {
                return;
            }
            channel.sendTyping().queue();
            messages = getMessages(channel, number, m -> Objects.equals(m.getMember(), mentioned));
        } else {
            channel.sendTyping().queue();
            messages = getMessages(channel, number + 1, null);
        }

        if (CollectionUtils.isEmpty(messages) || messages.size() == 1) {
            messageService.onError(event.getChannel(), "discord.mod.clear.absent");
            fail(event);
            return;
        }

        messages.forEach(actionsHolderService::markAsDeleted);
        RequestFuture.allOf(channel.purgeMessages(messages)).whenComplete((v, t) ->
                contextService.withContext(channel.getGuild(), () -> {
                    int count = messages.size();
                    if (!mention) {
                        count--;
                    }
                    String pluralMessages = messageService.getCountPlural(count, "discord.plurals.message");
                    messageService.onTempMessage(channel, 5, "discord.mod.clear.deleted", count, pluralMessages);
                }));
    }

    private int getCount(String queue) throws DiscordException {
        int result = 10;
        if (StringUtils.isNotBlank(queue)) {
            Matcher matcher = COUNT_PATTERN.matcher(queue.trim());
            if (!matcher.find()) {
                throw new ValidationException("discord.mode.clear.help");
            }
            try {
                result = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                result = MAX_MESSAGES;
            }
        }
        return Math.min(result, MAX_MESSAGES);
    }

    private List<Message> getMessages(TextChannel channel, Integer limitCount, Predicate<Message> predicate) {
        List<Message> messages = limitCount != null ? new ArrayList<>(limitCount) : new ArrayList<>();
        MessageHistory history = channel.getHistory();
        DateTime limit = new DateTime()
                .minusWeeks(2)
                .minusHours(1);
        while (limitCount == null || limitCount > 0) {
            int count = limitCount == null || limitCount > 100 ? 100 : limitCount;
            List<Message> retrieved = history.retrievePast(count).complete();
            if (CollectionUtils.isEmpty(retrieved)) {
                break;
            }
            for (Message message : retrieved) {
                DateTime creationDate = CommonUtils.getDate(message.getCreationTime());
                if (creationDate.isBefore(limit)) {
                    return messages;
                }
                if (predicate == null || predicate.test(message)) {
                    messages.add(message);
                    if (limitCount != null) {
                        limitCount--;
                    }
                }
            }
        }
        return messages;
    }
}