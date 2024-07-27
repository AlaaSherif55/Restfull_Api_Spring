package com.example.test;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepositery extends JpaRepository<Post,Long> {
}
