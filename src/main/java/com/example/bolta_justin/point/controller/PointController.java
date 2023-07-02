package com.example.bolta_justin.point.controller;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.point.dto.PointAvailableReqDTO;
import com.example.bolta_justin.point.dto.PointListReqDTO;
import com.example.bolta_justin.point.dto.PointListResDTO;
import com.example.bolta_justin.point.dto.PointUseReqDTO;
import com.example.bolta_justin.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final JwtUtil jwtUtil;
    private final PointService pointService;
    /**
     * 포인트 적립 및 사용
     */
    @PostMapping("/points")
    public ResponseDTO handlePoint(@RequestBody PointUseReqDTO pointUseReqDTO, HttpServletRequest request){
        String accessToken = jwtUtil.parseHeader(request, HttpHeaders.AUTHORIZATION);
        return pointService.handlePoint(pointUseReqDTO, accessToken);
    }

    /**
     * 회원 본인의 포인트 조회
     */
    @GetMapping("/points")
    public ResponseDTO getMyPoint(HttpServletRequest request){
        String accessToken = jwtUtil.parseHeader(request, HttpHeaders.AUTHORIZATION);
        return pointService.getMyPoint(accessToken);
    }

    /**
     * 멤버십 바코드의 현재 사용 가능 포인트 조회
     */
    @GetMapping("/points/barcodes")
    public ResponseDTO getBarcodePoint(@RequestBody PointAvailableReqDTO pointAvailableReqDTO){
        return pointService.getBarcodePoint(pointAvailableReqDTO.getMemberBarcode());
    }

    /**
     * 멤버십 바코드의 포인트 사용 내역 조회
     */
    @GetMapping("/points/uselists")
    public Page<PointListResDTO> getPointList(@RequestBody PointListReqDTO pointListReqDTO, @PageableDefault Pageable pageable){
        return pointService.getPointList(pointListReqDTO, pageable);
    }
}
