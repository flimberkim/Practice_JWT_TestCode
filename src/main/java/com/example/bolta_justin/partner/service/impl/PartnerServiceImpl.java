package com.example.bolta_justin.partner.service.impl;

import com.example.bolta_justin.partner.dto.PartnerDTO;
import com.example.bolta_justin.partner.enums.PartnerType;
import com.example.bolta_justin.partner.repository.PartnerRepository;
import com.example.bolta_justin.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final PartnerRepository partnerRepository;
    @Override
    public Page<PartnerDTO> getAllPartners(Pageable pageable) {
        return partnerRepository.findAll(pageable).map(PartnerDTO::new);
    }

    @Override
    public Page<PartnerDTO> searchPartnersByType(String partnerType, Pageable pageable) {
        switch (partnerType){
            case "식품":
                return partnerRepository.findAllByPartnerType(PartnerType.A, pageable).map(PartnerDTO::new);
            case "화장품":
                return partnerRepository.findAllByPartnerType(PartnerType.B, pageable).map(PartnerDTO::new);
            case "식당":
                return partnerRepository.findAllByPartnerType(PartnerType.C, pageable).map(PartnerDTO::new);
        }
        return null;
    }

    @Override
    public Page<PartnerDTO> searchPartnersByName(String partnerName, Pageable pageable) {
        return partnerRepository.findAllByName(partnerName, pageable).map(PartnerDTO::new);
    }
}
