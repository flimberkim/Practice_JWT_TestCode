package com.example.bolta_justin.partner.service;

import com.example.bolta_justin.partner.dto.PartnerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartnerService {
    Page<PartnerDTO> getAllPartners(Pageable pageable);

    Page<PartnerDTO> searchPartnersByType(String partnerType, Pageable pageable);

    Page<PartnerDTO> searchPartnersByName(String partnerName, Pageable pageable);
}
