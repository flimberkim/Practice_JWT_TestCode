package com.example.bolta_justin.point.repository;

import com.example.bolta_justin.point.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Page<Point> findAllByPointDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
