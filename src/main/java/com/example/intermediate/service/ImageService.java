package com.example.intermediate.service;

import com.example.intermediate.controller.response.ImageResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Image;
import com.example.intermediate.domain.Member;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final TokenProvider tokenProvider;


    @Transactional
    public ResponseDto<?> upload(HttpServletRequest request, String imgUrl) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        if (null == imgUrl) {
            return ResponseDto.fail("EMPTY", "multipart file is empty");
        }

        Image image = Image.builder()
                .imgUrl(imgUrl)
                .build();
        imageRepository.save(image);
        return ResponseDto.success(
                ImageResponseDto.builder()
                        .data(image.getImgUrl())
                        .build()
        );
    }
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}






