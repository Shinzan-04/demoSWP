package com.example.demoSWP.api;

import com.example.demoSWP.dto.BlogPostDTO;
import com.example.demoSWP.dto.DoctorDTO;
import com.example.demoSWP.entity.BlogPost;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.repository.BlogPostRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class BlogAPI {

    private final BlogPostRepository blogPostRepository;
    private final DoctorRepository doctorRepository;
    private final AuthenticationService authenticationService;

    // ✅ Anyone can view blog posts
    @GetMapping
    public ResponseEntity<List<BlogPostDTO>> getAllBlogs() {
        List<BlogPost> posts = blogPostRepository.findAll();
        List<BlogPostDTO> dtos = posts.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // ✅ Get specific blog post
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDTO> getBlogById(@PathVariable Long id) {
        return blogPostRepository.findById(id)
                .map(post -> ResponseEntity.ok(mapToDTO(post)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Only DOCTOR can create blog posts
    @PostMapping
    public ResponseEntity<BlogPostDTO> createBlog(@RequestBody BlogPost blogPost) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            Doctor currentDoctor = authenticationService.getCurrentDoctor();
            blogPost.setDoctor(currentDoctor);
            blogPost.setCreatedAt(LocalDateTime.now());
            BlogPost saved = blogPostRepository.save(blogPost);

            return ResponseEntity.ok(mapToDTO(saved));
        } catch (Exception e) {
            System.out.println("Error creating blog: " + e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    // ✅ Only DOCTOR can update their own blog posts
    @PutMapping("/{id}")
    public ResponseEntity<BlogPostDTO> updateBlog(@PathVariable Long id, @RequestBody BlogPost blogPost) {
        try {
            Doctor currentDoctor = authenticationService.getCurrentDoctor();

            return blogPostRepository.findById(id)
                    .map(existingPost -> {
                        if (!existingPost.getDoctor().getDoctorId().equals(currentDoctor.getDoctorId())) {
                            return ResponseEntity.status(403).<BlogPostDTO>build();
                        }

                        existingPost.setTitle(blogPost.getTitle());
                        existingPost.setContent(blogPost.getContent());
                        existingPost.setTag(blogPost.getTag());

                        BlogPost updated = blogPostRepository.save(existingPost);
                        return ResponseEntity.ok(mapToDTO(updated));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Only DOCTOR can delete their own blog posts
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        try {
            Doctor currentDoctor = authenticationService.getCurrentDoctor();

            return blogPostRepository.findById(id)
                    .map(existingPost -> {
                        if (!existingPost.getDoctor().getDoctorId().equals(currentDoctor.getDoctorId())) {
                            return ResponseEntity.status(403).<Void>build();
                        }

                        blogPostRepository.delete(existingPost);
                        return ResponseEntity.ok().<Void>build();
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Mapper: BlogPost -> BlogPostDTO
    private BlogPostDTO mapToDTO(BlogPost blogPost) {
        Doctor doctor = blogPost.getDoctor();
        DoctorDTO doctorDTO = new DoctorDTO(
                doctor.getFullName(),
                doctor.getPhone(),
                doctor.getEmail()
        );

        return new BlogPostDTO(
                blogPost.getId(),
                blogPost.getTitle(),
                blogPost.getContent(),
                blogPost.getCreatedAt(),
                blogPost.getTag(),
                doctorDTO
        );
    }
}
