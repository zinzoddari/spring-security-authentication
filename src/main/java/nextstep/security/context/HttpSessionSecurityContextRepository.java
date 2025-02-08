package nextstep.security.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nextstep.security.authentication.Authentication;

public class HttpSessionSecurityContextRepository implements SecurityContextRepository {

    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    @Override
    public SecurityContext loadContext(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();

        Authentication authentication = (Authentication) httpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);

        if (authentication == null) {
            return null;
        }

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession();

        httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context.getAuthentication());
    }
}
