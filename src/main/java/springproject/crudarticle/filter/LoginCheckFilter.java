package springproject.crudarticle.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LoginCheckFilter implements Filter {
    private final String[] accessibleUri = {"/login", "/signup"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 접근 가능한 Uri이거나 로그인된 경우가 아니면 /login으로 리다이렉트
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();

        if((!isAccessibleUri(requestUri)) && (request.getSession(false) == null)) {
            response.sendRedirect("/login");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAccessibleUri(String requestUri) {
        for(String s : accessibleUri) {
            if(s.equals(requestUri)) {
                return true;
            }
        }
        return false;
    }
}
