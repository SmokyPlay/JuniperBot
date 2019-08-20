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
package ru.juniperbot.common.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.juniperbot.common.service.BrandingService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class BrandingServiceImpl implements BrandingService {

    @Value("${branding.avatarUrl:}")
    private String avatarUrl;

    @Value("${branding.avatarSmallUrl:}")
    private String smallAvatarUrl;

    @Value("${branding.copy.imageUrl:}")
    private String copyImageUrl;

    @Value("${branding.web.host:juniper.bot}")
    private String wehHost;

    private Set<String> webAliases;

    @Override
    public String getAvatarUrl() {
        return getOrDefault(avatarUrl);
    }

    @Override
    public String getSmallAvatarUrl() {
        return getOrDefault(smallAvatarUrl);
    }

    @Override
    public String getCopyImageUrl() {
        return getOrDefault(copyImageUrl);
    }

    @Override
    public String getWebHost() {
        return wehHost;
    }

    @Override
    public String getWebHostName() {
        return wehHost != null ? wehHost
                .replace("https://", "")
                .replace("http://", "") : null;
    }

    private String getOrDefault(String url) {
        return StringUtils.isNotEmpty(url) ? url : null;
    }

    @Value("${branding.web.aliases:}")
    public void setAliases(String aliases) {
        if (StringUtils.isEmpty(aliases)) {
            webAliases = Collections.emptySet();
        }
        webAliases = new HashSet<>(Arrays.asList(aliases.split(",")));
    }

    @Override
    public Set<String> getWebAliases() {
        return Collections.unmodifiableSet(webAliases);
    }
}