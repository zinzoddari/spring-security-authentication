package nextstep.security.filter;

import jakarta.servlet.Filter;

import java.util.List;

public class DefaultSecurityFilterChain implements SecurityFilterChain {

    private final List<Filter> filters;

    public DefaultSecurityFilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public List<Filter> getFilter() {
        return filters;
    }
}
