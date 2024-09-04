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
import springproject.crudarticle.domain.Comment;
import springproject.crudarticle.domain.Member;
import springproject.crudarticle.domain.Post;
import springproject.crudarticle.service.MemberService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;

    @GetMapping("/user/{username}")
    public String getMemberBoard(
            @PathVariable("username") String username,
            HttpServletRequest request,
            Model model
    ) {
        Articleboard articleboard = memberService.getArticleboardByName(username);
        model.addAttribute("username", username);
        model.addAttribute("board", articleboard);
        model.addAttribute("authorized_member", request.getAttribute("authorized_member"));

        Member member = (Member)request.getSession().getAttribute("member");
        model.addAttribute("current_member", member.getName());

        return "user/userBoard";
    }

    @GetMapping("/user/{username}/write")
    public String getWriteForm(
            @PathVariable("username") String username,
            HttpServletRequest request,
            Model model
    ) {
        Articleboard board = memberService.getArticleboardByName(username);
        model.addAttribute("username", username);
        model.addAttribute("board", board);

        Member member = (Member)request.getSession().getAttribute("member");
        model.addAttribute("current_member", member.getName());

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
        model.addAttribute("current_member", member.getName());
        model.addAttribute("authorized_member", request.getAttribute("authorized_member"));

        List<Comment> comments = memberService.getCommentList(username, postId);
        model.addAttribute("comments", comments);

        return "user/postpage";
    }

    @PostMapping("/user/{username}/{postId}")
    public String addComment(
            @PathVariable("username") String username,
            @PathVariable("postId") String postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpServletRequest request
    ) {
        Member member = (Member) request.getSession().getAttribute("member");
        memberService.saveComment(member.getName(), username, postId, title, content);

        return ("redirect:/user/" + username + "/" + postId);
    }

    @GetMapping("/user/{username}/{postId}/delete")
    public String deletePost(
            @PathVariable("username") String username,
            @PathVariable("postId") String postId
    ){
        memberService.deletePostById(username, postId);

        return ("redirect:/user/" + username);
    }

    @GetMapping("/user/{username}/{postId}/update")
    public String getPostUpdateForm(
            @PathVariable("username") String username,
            @PathVariable("postId") String postId,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("username", username);
        model.addAttribute("postid", postId);
        model.addAttribute("board", memberService.getArticleboardByName(username));

        Post post = memberService.getPostById(username, postId);
        model.addAttribute("post", post);
        model.addAttribute("title", post.getTitle());
        model.addAttribute("content", post.getContent());

        Member member = (Member)request.getSession().getAttribute("member");
        model.addAttribute("current_member", member.getName());

        return "user/update-form";
    }

    @PostMapping("/user/{username}/{postId}/update")
    public String updatePost(
            @PathVariable("username") String username,
            @PathVariable("postId") String postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            Model model
    ) {
        memberService.updatePost(username, postId, title, content);

        return "redirect:/user/" + username + "/" + postId;
    }

    @GetMapping("/user/all")
    public String getAllBoardPage(
            HttpServletRequest request,
            Model model
    ) {
        Member member = (Member)request.getSession().getAttribute("member");
        model.addAttribute("current_member", member.getName());

        List<Member> members = memberService.getAllMember();
        model.addAttribute("members", members);

        return "user/allBoard";
    }
}
