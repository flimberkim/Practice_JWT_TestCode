package com.example.bolta_justin.partner.repository;

import com.example.bolta_justin.partner.entity.Partner;
import com.example.bolta_justin.partner.enums.PartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long>{
    Page<Partner> findAll(Pageable pageable);
    Page<Partner> findAllByPartnerType(PartnerType partnerType, Pageable pageable);
    Page<Partner> findAllByName(String partnerName, Pageable pageable);
}
