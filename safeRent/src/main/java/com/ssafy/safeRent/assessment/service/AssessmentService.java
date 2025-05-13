package com.ssafy.safeRent.assessment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.util.GrokApiClient;
import com.ssafy.safeRent.util.PDFConverter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AssessmentService {
	private final GrokApiClient grokApiClient;
	private final PDFConverter pdfConverter;

	@Value("${aws.s3.base-url}")
	private String s3BaseUrl;

	public Mono<ResponseEntity<String>> analyzeProperty(MultipartFile pdfFile) {
		// PDF 파일을 이미지로 변환 -> s3에 업로드
		// try {
		// pdfConverter.convertPDFToImages(pdfFile);
		// } catch (IOException ioe) {
		// ioe.printStackTrace();
		// }

		// 테스트용 S3 URL 리스트
		List<String> s3UrlList = new ArrayList<>();
		s3UrlList.add(s3BaseUrl + "/pdf-img/page-1.jpeg");
		s3UrlList.add(s3BaseUrl + "/pdf-img/page-2.jpeg");
		s3UrlList.add(s3BaseUrl + "/pdf-img/page-8.jpeg");

		return grokApiClient
			.analyzePropertyImages(s3UrlList)
			.map(grokResponse -> ResponseEntity.ok(grokResponse)) // Map.of 대신 직접 String 반환
			.defaultIfEmpty(ResponseEntity.notFound().build())
			.onErrorResume(IllegalArgumentException.class,
					e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())))
			.onErrorResume(Exception.class,
					e -> Mono.just(ResponseEntity.badRequest().body("Failed to process PDF file: " + e.getMessage())));

	}

}
