package com.ssafy.safeRent.assessment.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    // File 객체를 S3에 업로드
    public String upload(File uploadFile, String dirName) throws IOException {
        return upload(new FileInputStream(uploadFile), uploadFile.getName(), dirName, uploadFile.length());
    }

    // MultipartFile을 S3에 업로드
//    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
//        return upload(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), dirName, multipartFile.getSize());
//    }

    // 실제 업로드 처리 메서드
    private String upload(InputStream inputStream, String originalFilename, String dirName, long contentLength) throws IOException {
        // 파일명 생성 (UUID 사용)
        String fileName = createFileName(originalFilename);

        // S3에 저장될 경로 생성
        String fileKey = dirName + "/" + fileName;

        // 파일 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        // 이미지 파일인 경우 Content-Type 설정
        if (originalFilename.toLowerCase().endsWith(".jpg") || originalFilename.toLowerCase().endsWith(".jpeg")) {
            metadata.setContentType("image/jpeg");
        } else if (originalFilename.toLowerCase().endsWith(".png")) {
            metadata.setContentType("image/png");
        } else if (originalFilename.toLowerCase().endsWith(".gif")) {
            metadata.setContentType("image/gif");
        }

        // S3에 업로드
        try (InputStream is = inputStream) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileKey, is, metadata));
//                .withCannedpost_id(CannedAccessControlList.PublicRead));
        }

        // 업로드된 파일의 URL 반환
        return baseUrl + "/" + fileKey;
    }

    // 파일명 생성 메서드 (UUID 사용)
    private String createFileName(String originalFilename) {
        return UUID.randomUUID().toString() + getFileExtension(originalFilename);
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }
}
