package com.example.intermediate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ImageRepository extends JpaRepository<com.example.intermediate.domain.Image, String> {
}
