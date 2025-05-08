package com.example.demo.domain.image.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
