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
package ru.caramel.juniperbot.module.misc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.caramel.juniperbot.module.misc.model.dogapi.DogImage;
import ru.caramel.juniperbot.module.misc.model.dogapi.DogSearchQuery;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static ru.juniperbot.common.utils.CommonUtils.HTTP_TIMEOUT_DURATION;

@Service
public class DogApiService {

    private static final String BASE_URI = "https://api.thedogapi.com/v1/";

    @Value("${integrations.dogApi.key:}")
    private String apiKey;

    @Value("${integrations.dogApi.userId:}")
    private String userId;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        if (StringUtils.isNotEmpty(apiKey)) {
            this.restTemplate = new RestTemplateBuilder()
                    .rootUri(BASE_URI)
                    .setConnectTimeout(HTTP_TIMEOUT_DURATION)
                    .setReadTimeout(HTTP_TIMEOUT_DURATION)
                    .uriTemplateHandler(new DefaultUriBuilderFactory(BASE_URI))
                    .additionalInterceptors((request, body, execution) -> {
                        HttpHeaders headers = request.getHeaders();
                        headers.add("x-api-key", apiKey);
                        headers.add("User-Agent", "JuniperBot");
                        return execution.execute(request, body);
                    })
                    .build();
        }
    }

    public List<DogImage> search(DogSearchQuery query) {
        if (restTemplate == null) {
            return Collections.emptyList();
        }
        return execute(query.toUri(), new ParameterizedTypeReference<List<DogImage>>() {});
    }

    private <T> T execute(UriComponentsBuilder builder, ParameterizedTypeReference<T> type) {
        return restTemplate.exchange(
                URLDecoder.decode(builder.toUriString(), StandardCharsets.UTF_8),
                HttpMethod.GET,
                null,
                type).getBody();
    }
}
