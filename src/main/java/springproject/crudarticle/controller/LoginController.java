package springproject.crudarticle.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;
import springproject.crudarticle.domain.Member;
import springproject.crudarticle.service.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;

    @GetMapping("/signup")
    public String getSignupForm() {
        return "login/signup";
    }

    @PostMapping("/signup")
    public String doSignup(
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            @RequestParam("boardname") String boardname,
            Model model
    ) {
        if(memberService.saveMember(name, password, boardname) == null){
            // 동일한 이름이 존재하는 경우
            model.addAttribute("nameError", true);
            return "login/signup";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginForm(
            Model model,
            HttpServletRequest request
    ) {
        // session이 존재하는 경우
        HttpSession session = request.getSession(false);
        if(session != null) {
            Member member = (Member)session.getAttribute("member");
            return ("redirect:/user/" + member.getName());
        }

        model.addAttribute("inputError", false);
        return "login/login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            HttpServletRequest request,
            Model model
    ) {
        // 로그인 검사
        Member member = memberService.getMemberByName(name);
        if((member == null) || (!member.getPassword().equals(password))) {
            // 잘못된 입력
            model.addAttribute("inputError", true);
            return "login/login";
        }

        // 로그인 성공, 세션 추가
        HttpSession session = request.getSession(true);
        session.setAttribute("member", member);
        return ("redirect:/user/" + member.getName());
    }

    @GetMapping("/logout")
    public String doLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();

        return "redirect:/login";
    }
}
