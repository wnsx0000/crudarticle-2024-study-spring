package springproject.crudarticle.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springproject.crudarticle.domain.Articleboard;
import springproject.crudarticle.domain.Comment;
import springproject.crudarticle.domain.Member;
import springproject.crudarticle.domain.Post;
import springproject.crudarticle.repository.JpaUnitedRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final JpaUnitedRepository repository;

    @Transactional
    public Member saveMember(String name, String password, String boardname) {
        // 존재 여부 검사
        if(!repository.findByName(name).isEmpty()) {
            return null;
        }

        // 객체 생성
        Member member = new Member();
        member.setName(name);
        member.setPassword(password);
        member.setComments(new ArrayList<>());

        Articleboard articleboard = new Articleboard();
        articleboard.setName(boardname);
        articleboard.setMember(member);
        articleboard.setPosts(new ArrayList<>());
        member.setArticleboard(articleboard);

        repository.save(member);

        return member;
    }

    public Member getMemberByName(String name) {
        List<Member> memberList = repository.findByName(name);
        if(memberList.isEmpty()) {
            return null;
        }

        return (repository.findByName(name).getFirst());
    }

    public Articleboard getArticleboardByName(String name) {
        List<Member> memberList = repository.findByName(name);
        if(memberList.isEmpty()) {
            return null;
        }

        return (memberList.getFirst().getArticleboard());
    }

    @Transactional
    public void savePost(String name, String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setComments(new ArrayList<>());

        Articleboard articleboard = getArticleboardByName(name);
        articleboard.getPosts().add(post);
        post.setArticleboard(articleboard);
    }

    public Post getPostById(String name, String id) {
        List<Post> posts = getArticleboardByName(name).getPosts();
        for (Post post : posts) {
            if(post.getId() == Long.parseLong(id)) {
                return post;
            }
        }
        return null;
    }

    @Transactional
    public void deletePostById(String name, String id) {
        List<Post> posts = getArticleboardByName(name).getPosts();
        posts.removeIf(post -> (post.getId().intValue() == Integer.parseInt(id)));
        
        //test
        System.out.println("delete service->");
        for (Post post : posts) {
            System.out.println("post.getId() = " + post.getId());
        }
        getArticleboardByName(name).setName("333");
    }
}
