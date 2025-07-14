package com.example.demoSWP.api;

import com.example.demoSWP.dto.BlogPostDTO;
import com.example.demoSWP.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@Validated
@RestController
@RequestMapping("/api/blogposts")
public class BlogAPI {

    @Autowired
    private BlogService blogService;

    @PostMapping
    public BlogPostDTO createBlogPost(@RequestBody BlogPostDTO blogPostDTO) {
        return blogService.createBlogPost(blogPostDTO);
    }

    @GetMapping
    public List<BlogPostDTO> getAllBlogPosts() {
        return blogService.getAllBlogPosts();
    }

    @GetMapping("/{id}")
    public BlogPostDTO getBlogPostById(@PathVariable Long id) {
        return blogService.getBlogPostById(id);
    }

    @PutMapping("/{id}")
    public BlogPostDTO updateBlogPost(@PathVariable Long id, @RequestBody BlogPostDTO blogPostDTO) {
        return blogService.updateBlogPost(id, blogPostDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBlogPost(@PathVariable Long id) {
        blogService.deleteBlogPost(id);
    }
    @PostMapping(value = "/create-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BlogPostDTO createWithImage(
            @RequestPart("blog") BlogPostDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return blogService.createBlogPostWithImage(dto, imageFile);
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BlogPostDTO updateWithImage(
            @PathVariable Long id,
            @RequestPart("blog") BlogPostDTO blogDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return blogService.updateWithImage(id, blogDto, imageFile);
    }

}
