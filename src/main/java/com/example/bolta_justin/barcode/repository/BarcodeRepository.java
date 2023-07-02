package com.example.bolta_justin.barcode.repository;

import com.example.bolta_justin.barcode.entity.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    Optional<Barcode> findById(Long barcodeId);
    Optional<Barcode> findByBarcode(String barcode);
}
