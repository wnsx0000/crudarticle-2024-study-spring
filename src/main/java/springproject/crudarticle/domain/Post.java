package springproject.crudarticle.domain;

import java.util.List;

public class Post {
    private Long id;
    private String content;
    private User user;
    private List<Comment> comments;
}
