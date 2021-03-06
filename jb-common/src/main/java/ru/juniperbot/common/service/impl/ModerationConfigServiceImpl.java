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
package ru.juniperbot.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.juniperbot.common.configuration.CommonProperties;
import ru.juniperbot.common.persistence.entity.ModerationConfig;
import ru.juniperbot.common.persistence.repository.ModerationConfigRepository;
import ru.juniperbot.common.service.ModerationConfigService;

import java.util.ArrayList;

@Service
public class ModerationConfigServiceImpl
        extends AbstractDomainServiceImpl<ModerationConfig, ModerationConfigRepository>
        implements ModerationConfigService {

    public ModerationConfigServiceImpl(@Autowired ModerationConfigRepository repository,
                                       @Autowired CommonProperties commonProperties) {
        super(repository, commonProperties.getDomainCache().isModerationConfig());
    }

    @Override
    protected ModerationConfig createNew(long guildId) {
        ModerationConfig config = new ModerationConfig(guildId);
        config.setCoolDownIgnored(true);
        config.setActions(new ArrayList<>());
        return config;
    }

    @Override
    protected Class<ModerationConfig> getDomainClass() {
        return ModerationConfig.class;
    }
}
