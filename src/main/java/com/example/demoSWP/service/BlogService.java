package com.example.demoSWP.service;

import com.example.demoSWP.entity.BlogPost;
import com.example.demoSWP.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogService {
    private final BlogPostRepository blogRepo;

    public BlogService(BlogPostRepository blogRepo) {
        this.blogRepo = blogRepo;
    }

    public List<BlogPost> getAllPosts() {
        return blogRepo.findAll();
    }

    public BlogPost getPostById(Long id) {
        return blogRepo.findById(id).orElse(null);
    }

    public BlogPost createPost(BlogPost post) {
        post.setCreatedAt(LocalDateTime.now());
        return blogRepo.save(post);
    }

    public BlogPost updatePost(Long id, BlogPost newPost) {
        BlogPost post = getPostById(id);
        if (post != null) {
            post.setTitle(newPost.getTitle());
            post.setContent(newPost.getContent());
            post.setTag(newPost.getTag());
            return blogRepo.save(post);
        }
        return null;
    }

    public void deletePost(Long id) {
        blogRepo.deleteById(id);
    }
}
