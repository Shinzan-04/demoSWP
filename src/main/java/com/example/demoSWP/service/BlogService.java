package com.example.demoSWP.service;

import com.example.demoSWP.dto.BlogPostDTO;
import com.example.demoSWP.entity.BlogPost;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.repository.BlogPostRepository;
import com.example.demoSWP.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public BlogPostDTO createBlogPost(BlogPostDTO dto) {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(dto.getTitle());
        blogPost.setContent(dto.getContent());
        blogPost.setTag(dto.getTag());
        blogPost.setCreatedAt(LocalDateTime.now());

        if (dto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElse(null);
            blogPost.setDoctor(doctor);
        }

        BlogPost savedPost = blogPostRepository.save(blogPost);
        return convertToDTO(savedPost);
    }

    public BlogPostDTO getBlogPostById(Long id) {
        Optional<BlogPost> optional = blogPostRepository.findById(id);
        return optional.map(this::convertToDTO).orElse(null);
    }

    public List<BlogPostDTO> getAllBlogPosts() {
        return blogPostRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BlogPostDTO updateBlogPost(Long id, BlogPostDTO dto) {
        Optional<BlogPost> optional = blogPostRepository.findById(id);
        if (optional.isPresent()) {
            BlogPost blog = optional.get();
            blog.setTitle(dto.getTitle());
            blog.setContent(dto.getContent());
            blog.setTag(dto.getTag());

            if (dto.getDoctorId() != null) {
                Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElse(null);
                blog.setDoctor(doctor);
            } else {
                blog.setDoctor(null);
            }

            return convertToDTO(blogPostRepository.save(blog));
        }
        return null;
    }

    public void deleteBlogPost(Long id) {
        blogPostRepository.deleteById(id);
    }

    private BlogPostDTO convertToDTO(BlogPost blog) {
        BlogPostDTO dto = new BlogPostDTO();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setTag(blog.getTag());
        if (blog.getDoctor() != null) {
            dto.setDoctorId(blog.getDoctor().getDoctorId());
            dto.setDoctorName(blog.getDoctor().getFullName());
        }
        return dto;
    }
}
