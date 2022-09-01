package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.ImageService;
import com.example.intermediate.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ImageController {
    private final S3Uploader s3Uploader;
    @Autowired
    private final ImageService imageService;

    @RequestMapping(value = "/api/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
    public ResponseDto<?> upload(HttpServletRequest request, @RequestParam(value="image", required = false) MultipartFile multipartFile) throws IOException {
        String imgUrl = s3Uploader.upload(multipartFile, "static");

        return imageService.upload(request, imgUrl);
    }
}
