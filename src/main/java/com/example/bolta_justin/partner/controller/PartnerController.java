package com.example.bolta_justin.partner.controller;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.partner.dto.PartnerDTO;
import com.example.bolta_justin.partner.enums.PartnerType;
import com.example.bolta_justin.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PartnerController {
    private final PartnerService partnerService;

    /**
     * 전체 가맹점 조회
     */
    @GetMapping("/partners")
    public Page<PartnerDTO> getAllPartners(@PageableDefault Pageable pageable){
        return partnerService.getAllPartners(pageable);
    }

    /**
     * 업종별 가맹점 조회
     */
    @GetMapping("/partners/{partnerType}")
    public Page<PartnerDTO> searchPartnersByType(@PathVariable String partnerType,
                                                 @PageableDefault Pageable pageable){
        return partnerService.searchPartnersByType(partnerType, pageable);
    }

    /**
     * 가맹점 이름으로 조회
     */
    @PostMapping("/partners/names")
    public Page<PartnerDTO> searchPartnersByName(@RequestBody PartnerDTO partnerDTO,
                                                 @PageableDefault Pageable pageable){

        return partnerService.searchPartnersByName(partnerDTO.getPartnerName(), pageable);
    }
}
