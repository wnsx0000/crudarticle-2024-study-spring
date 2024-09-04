package springproject.crudarticle.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import springproject.crudarticle.domain.Member;

import java.io.IOException;

@Slf4j
public class AuthorCheckFilter implements Filter {
    private final String[] accessDeniedUrlName = { "delete", "write", "update"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);
        if(session == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Member member = (Member)session.getAttribute("member");
        String url = request.getRequestURI();

        log.debug("AuthorCheckFilter:uri from request={}", url);
        log.debug("AuthorCheckFilter:username from url={}", getUserNameFromUrl(url));
        log.debug("AuthorCheckFilter:username from session={}", member.getName());

        String userNameFromUrl = getUserNameFromUrl(url);
        if(userNameFromUrl == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if(userNameFromUrl.equals(member.getName())) {
            // 일치하는 경우
            request.setAttribute("authorized_member", true);
        }
        else {
            // 일치하지 않는 경우
            // delete, write, update url에 접근한 경우 다른 곳으로 리다이렉트
            if(PatternMatchUtils.simpleMatch(accessDeniedUrlName, getMethodFromUrl(url))) {
                response.sendRedirect("/user" + member.getName());
                return;
            }

            request.setAttribute("authorized_member", false);
        }

        log.debug("AuthorCheckFilter:authorized_member={}", request.getAttribute("authorized_member"));

        filterChain.doFilter(request, response);
    }

    private String getUserNameFromUrl(String url) {
        String[] arr = url.split("/");
        log.debug("AuthorCheckFilter:url decomposed to->");
        for (String s : arr) {
            log.debug("element={}", s);
        }

        if((arr.length >= 3) && (arr[1].equals("user"))) {
            return arr[2];
        }
        else {
            return null;
        }
    }

    private String getMethodFromUrl(String url) {
        String[] arr = url.split("/");
        int arrSize = arr.length;
        return (arr[arrSize - 1]);
    }
}
