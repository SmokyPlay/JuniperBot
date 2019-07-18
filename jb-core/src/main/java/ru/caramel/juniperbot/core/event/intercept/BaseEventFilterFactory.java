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
package ru.caramel.juniperbot.core.event.intercept;

import net.dv8tion.jda.core.events.Event;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseEventFilterFactory<T extends Event> implements EventFilterFactory<T> {

    private final ThreadLocal<FilterChain<T>> chains = new ThreadLocal<>();

    @Autowired
    private List<Filter<T>> filterList;

    @Override
    public FilterChain<T> createChain(T event) {
        if (event == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(filterList)) {
            return null;
        }
        FilterChain<T> chain = chains.get();
        if (chain == null) {
            chain = createInternal(event);
            chains.set(chain);
        }
        chain.reset();
        return chain;
    }

    private FilterChain<T> createInternal(Event event) {
        FilterChainImpl<T> chain = new FilterChainImpl<>();
        // TODO sort chains on order
        filterList.forEach(chain::addFilter);
        return chain;
    }
}