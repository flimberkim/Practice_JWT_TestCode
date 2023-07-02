package com.example.bolta_justin.barcode.service.impl;

import com.example.bolta_justin.barcode.dto.BarcodeReqDTO;
import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.barcode.exception.BarcodeExceptionType;
import com.example.bolta_justin.barcode.repository.BarcodeRepository;
import com.example.bolta_justin.barcode.service.BarcodeService;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.member.entity.Member;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.member.exception.MemberExceptionType;
import com.example.bolta_justin.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {
    private final MemberRepository memberRepository;
    private final BarcodeRepository barcodeRepository;

    @Override
    public String barcodeGenerator() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    @Override
    public ResponseDTO createBarcode(BarcodeReqDTO barcodeReqDTO) {

        Integer identifierToInt = Integer.valueOf(barcodeReqDTO.getIdentifier());
        Member findMember = memberRepository.findByIdentifier(identifierToInt).orElseThrow(()-> new MemberException(MemberExceptionType.MEMBER_IDENTIFIER_NOT_FOUND));

        if(findMember.getBarcode() == null){
            //바코드 생성 후 바코드 테이블에 저장하고 그 엔티티 멤버에 저장
            Barcode barcode = Barcode.builder()
                    .barcode(barcodeGenerator())
                    .aPoint(0)
                    .bPoint(0)
                    .cPoint(0)
                    .build();
            barcodeRepository.save(barcode);
            findMember.setBarcode(barcode);
            memberRepository.save(findMember);

            return ResponseDTO.builder()
                    .stateCode(200)
                    .success(true)
                    .message("바코드 생성 성공")
                    .data(barcode.getBarcode())
                    .build();
        }
        else {
            //이미 바코드가 존재하면 바코드를 리턴
            return ResponseDTO.builder()
                    .stateCode(200)
                    .success(true)
                    .message("바코드가 존재합니다.")
                    .data(findMember.getBarcode().getBarcode())
                    .build();
        }
    }

    @Override
    public ResponseDTO getBarcode(Integer identifier) {
        Member findMember = memberRepository.findByIdentifier(identifier).orElseThrow(()-> new MemberException(MemberExceptionType.MEMBER_IDENTIFIER_NOT_FOUND));
        if(findMember.getBarcode() == null){
            throw new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND);
        }
        return ResponseDTO.builder()
                .stateCode(200)
                .success(true)
                .message("바코드 조회")
                .data(findMember.getBarcode().getBarcode())
                .build();
    }
}
