package com.example.demoSWP.service;

import com.example.demoSWP.dto.BlogPostDTO;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.entity.BlogPost;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.enums.Role;
import com.example.demoSWP.exception.ResourceNotFoundException;
import com.example.demoSWP.repository.AuthenticationRepository;
import com.example.demoSWP.repository.BlogPostRepository;
import com.example.demoSWP.repository.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BlogService {

    /* ---------- DEPENDENCIES ---------- */
    @Autowired private BlogPostRepository blogPostRepository;
    @Autowired private DoctorRepository   doctorRepository;
    @Autowired private AuthenticationRepository authenticationRepository;
    @Autowired private ModelMapper        modelMapper;

    /* ──────────────────────────────────────────────────────────── */
    /*                          CREATE – JSON                      */
    /* ──────────────────────────────────────────────────────────── */
    public BlogPostDTO createBlogPost(BlogPostDTO dtoIn) {

        // Chỉ Doctor mới gọi hàm này → lấy bác sĩ hiện tại
        Doctor doctor = currentDoctor();

        BlogPost entity = new BlogPost();
        entity.setTitle(dtoIn.getTitle());
        entity.setContent(dtoIn.getContent());
        entity.setTag(dtoIn.getTag());

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDoctor(doctor);

        blogPostRepository.save(entity);
        return toDTO(entity);
    }

    /* ──────────────────────────────────────────────────────────── */
    /*                 CREATE – MULTIPART  (ảnh)                   */
    /* ──────────────────────────────────────────────────────────── */
    public BlogPostDTO createBlogPostWithImage(BlogPostDTO dtoIn, MultipartFile imageFile) {

        /* 1. Lấy email + role hiện tại */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account acc  = authenticationRepository.findAccountByEmail(email);
        Role role    = acc.getRole();

        /* 2. Khởi tạo entity */
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(dtoIn.getTitle());
        blogPost.setContent(dtoIn.getContent());
        blogPost.setTag(dtoIn.getTag());
        blogPost.setCreatedAt(LocalDateTime.now());
        blogPost.setUpdatedAt(LocalDateTime.now());

        /* 3. Gán doctor theo role */
        if (role == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByAccount_Email(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với email: " + email));
            blogPost.setDoctor(doctor);
        } else if (role == Role.ADMIN) {
            blogPost.setDoctor(null);          // admin → không gán doctor
        }

        /* 4. Lưu ảnh nếu có */
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String url = saveBlogImage(imageFile);
                blogPost.setImageUrl(url);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage(), e);
            }
        }

        blogPostRepository.save(blogPost);
        return toDTO(blogPost);
    }

    /* ──────────────────────────────────────────────────────────── */
    /*                            READ                             */
    /* ──────────────────────────────────────────────────────────── */
    public List<BlogPostDTO> getAllBlogPosts() {
        return blogPostRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BlogPostDTO getBlogPostById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết"));
        return toDTO(post);
    }

    /* ──────────────────────────────────────────────────────────── */
    /*                       UPDATE – JSON                         */
    /* ──────────────────────────────────────────────────────────── */
    public BlogPostDTO updateBlogPost(Long id, BlogPostDTO dtoIn) {

        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết"));

        post.setTitle(dtoIn.getTitle());
        post.setContent(dtoIn.getContent());
        post.setTag(dtoIn.getTag());
        post.setUpdatedAt(LocalDateTime.now());

        if (dtoIn.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dtoIn.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ"));
            post.setDoctor(doctor);
        }

        blogPostRepository.save(post);
        return toDTO(post);
    }

    /* ──────────────────────────────────────────────────────────── */
    /*                     UPDATE – MULTIPART                      */
    /* ──────────────────────────────────────────────────────────── */
    public BlogPostDTO updateWithImage(Long id,
                                       BlogPostDTO dto,
                                       MultipartFile imageFile) {

        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setTag(dto.getTag());
        post.setUpdatedAt(LocalDateTime.now());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                post.setImageUrl(saveBlogImage(imageFile));
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage(), e);
            }
        }

        blogPostRepository.save(post);
        return toDTO(post);
    }

    /* ──────────────────────────────────────────────────────────── */
    /*                           DELETE                            */
    /* ──────────────────────────────────────────────────────────── */
    public void deleteBlogPost(Long id) {
        blogPostRepository.deleteById(id);
    }

    /* ──────────────────────────────────────────────────────────── */
    /*                HELPER – Lưu file ảnh                         */
    /* ──────────────────────────────────────────────────────────── */
    private String saveBlogImage(MultipartFile file) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploads/blog";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(dir, filename);
        file.transferTo(dest);

        return "/uploads/blog/" + filename;   // đường dẫn truy cập từ FE
    }

    /* ──────────────────────────────────────────────────────────── */
    /*             HELPER – Map entity → DTO                       */
    /* ──────────────────────────────────────────────────────────── */
    private BlogPostDTO toDTO(BlogPost post) {
        BlogPostDTO dto = modelMapper.map(post, BlogPostDTO.class);

        if (post.getDoctor() != null) {
            dto.setDoctorId(post.getDoctor().getDoctorId());
            dto.setDoctorName(post.getDoctor().getFullName());
        } else {
            dto.setDoctorName("admin");   // hiển thị admin nếu không có doctor
        }
        return dto;
    }

    /* ──────────────────────────────────────────────────────────── */
    /*          HELPER – Lấy bác sĩ hiện tại (Doctor role)          */
    /* ──────────────────────────────────────────────────────────── */
    private Doctor currentDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return doctorRepository.findByAccount_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy bác sĩ với email: " + email));
    }
}
