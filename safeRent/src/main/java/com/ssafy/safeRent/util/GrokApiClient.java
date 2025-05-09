package com.ssafy.safeRent.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ssafy.safeRent.assessment.dto.ChatCompletionDTO;

import reactor.core.publisher.Mono;

@Component
public class GrokApiClient {
	private final WebClient webClient;
	private static final String S3_IMAGE_BASE_URL = "https://msgs-s3-bucket.s3.ap-northeast-2.amazonaws.com/pdf-img/";

	public GrokApiClient(WebClient.Builder webClientBuilder, @Value("${grok.api.key}") String apiKey) {
		this.webClient = webClientBuilder
				.baseUrl("https://api.x.ai/v1/chat/completions")
				.defaultHeader("Authorization", "Bearer " + apiKey)
				.build();
	}

	public Mono<String> analyzePropertyImages(List<String> base64Images) {
		// 이미지 개수 검증
		System.out.println("Processing " + base64Images.size() + " images for Grok API");
		if (base64Images.size() != 8) {
			System.out.println("Warning: Expected 8 images, but received " + base64Images.size());
		}

		// S3 URL 리스트 생성
		List<String> imageUrls = IntStream.range(0, base64Images.size())
				.mapToObj(i -> S3_IMAGE_BASE_URL + "page-" + (i + 1) + ".jpeg")
				.collect(Collectors.toList());

		// S3 URL을 JSON 배열로 구성
		String imageUrlsJson = imageUrls.stream()
				.map(url -> String.format("{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\"}}", url))
				.collect(Collectors.joining(","));

		// 요청 텍스트
		String requestBody = String.format(
				"{\"model\":\"grok-2-vision-1212\",\"messages\":[{\"role\":\"user\",\"content\":[{\"type\":\"text\",\"text\":\"현재 보낸 이미지들을 상세하게 설명해주고 각 장의 특징적인 텍스트도 알려줘\"},%s]}],\"max_tokens\":%d}",
				imageUrlsJson, base64Images.size() * 1000);

		return webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.doOnNext(response -> System.out.println("Grok API Response: " + response))
				.onErrorMap(e -> new RuntimeException("Failed to call Grok API", e));
	}
}