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
package ru.juniperbot.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.juniperbot.common.persistence.entity.AuditConfig;
import ru.juniperbot.common.persistence.repository.AuditConfigRepository;

@Service
public class AuditConfigServiceImpl
        extends AbstractDomainServiceImpl<AuditConfig, AuditConfigRepository>
        implements AuditConfigService {

    public AuditConfigServiceImpl(@Autowired AuditConfigRepository repository) {
        super(repository, true);
    }

    @Override
    protected AuditConfig createNew(long guildId) {
        return new AuditConfig(guildId);
    }

    @Override
    protected Class<AuditConfig> getDomainClass() {
        return AuditConfig.class;
    }
}
