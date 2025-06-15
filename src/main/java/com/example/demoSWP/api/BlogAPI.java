package com.example.demoSWP.api;

import com.example.demoSWP.dto.BlogPostDTO;
import com.example.demoSWP.service.BlogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogposts")
@CrossOrigin(origins = "*")
public class BlogAPI {

    @Autowired
    private BlogService blogPostService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BlogPostDTO dto) {
        try {
            return ResponseEntity.ok(blogPostService.createBlogPost(dto));
        } catch (Exception e) {
            e.printStackTrace(); // log ra terminal
            return ResponseEntity.badRequest().body("Lỗi khi tạo blog: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDTO> getById(@PathVariable Long id) {
        BlogPostDTO result = blogPostService.getBlogPostById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<BlogPostDTO>> getAll() {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPostDTO> update(@PathVariable Long id, @RequestBody BlogPostDTO dto) {
        BlogPostDTO result = blogPostService.updateBlogPost(id, dto);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.ok().build();
    }
}
