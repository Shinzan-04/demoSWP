package com.example.demoSWP.repository;

import com.example.demoSWP.entity.BlogPost;
import com.example.demoSWP.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {}


