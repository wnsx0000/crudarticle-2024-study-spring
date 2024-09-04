package springproject.crudarticle.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springproject.crudarticle.domain.Articleboard;
import springproject.crudarticle.domain.Member;
import springproject.crudarticle.domain.Post;
import springproject.crudarticle.service.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;

    @GetMapping("/user/{username}")
    public String getMemberBoard(
            @PathVariable("username") String username,
            Model model
    ) {
        Articleboard articleboard = memberService.getArticleboardByName(username);
        model.addAttribute("username", username);
        model.addAttribute("board", articleboard);

        Member member = articleboard.getMember();
        model.addAttribute("canwrite", member.getName().equals(username));

        //test
        System.out.println("user page->");
        for (Post post : articleboard.getPosts()) {
            System.out.println("post.getId() = " + post.getId());
        }

        return "user/userBoard";
    }

    @GetMapping("/user/{username}/write")
    public String getWriteForm(
            @PathVariable("username") String username,
            Model model
    ) {
        Articleboard board = memberService.getArticleboardByName(username);
        model.addAttribute("username", username);
        model.addAttribute("board", board);

        return "user/write-form";
    }

    @PostMapping("/user/{username}/write")
    public String setPost(
            @PathVariable("username") String username,
            @RequestParam("title") String title,
            @RequestParam("content") String content
    ) {
        memberService.savePost(username, title, content);
        return ("redirect:/user/" + username);
    }

    @GetMapping("/user/{username}/{postId}")
    public String getPostPage(
            @PathVariable("username") String username,
            @PathVariable("postId") String postId,
            HttpServletRequest request,
            Model model
    ) {
        Articleboard board = memberService.getArticleboardByName(username);
        model.addAttribute("board", board);

        model.addAttribute("post", memberService.getPostById(username, postId));
        model.addAttribute("postid", postId);

        Member member = (Member)request.getSession().getAttribute("member");
        model.addAttribute("canedit", member.getName().equals(username));

        return "user/postpage";
    }

    @GetMapping("/user/{username}/{postId}/delete")
    public String deletePost(
            @PathVariable("username") String username,
            @PathVariable("postId") String postId
    ){
        memberService.deletePostById(username, postId);

        //test
        System.out.println("delete controller->");
        Articleboard articleboard = memberService.getArticleboardByName(username);
        for (Post post : articleboard.getPosts()) {
            System.out.println("post.getId() = " + post.getId());
        }

        return ("redirect:/user/" + username);
    }
}
