package nextstep.security;

import jakarta.servlet.Filter;
import java.util.List;

public interface SecurityFilterChain {

    List<Filter> getFilter();
}
