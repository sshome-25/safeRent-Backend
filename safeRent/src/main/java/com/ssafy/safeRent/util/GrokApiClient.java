package com.ssafy.safeRent.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class GrokApiClient {
	private final WebClient webClient;
	// private static final String S3_IMAGE_BASE_URL =
	// "https://msgs-s3-bucket.s3.ap-northeast-2.amazonaws.com/pdf-img/";

	public GrokApiClient(WebClient.Builder webClientBuilder, @Value("${grok.api.key}") String apiKey) {
		this.webClient = webClientBuilder
				.baseUrl("https://api.x.ai/v1/chat/completions")
				.defaultHeader("Authorization", "Bearer " + apiKey)
				.build();
	}

	public Mono<String> analyzePropertyImages(List<String> s3UrlList) {
		// analyzePropertyImages() 시작 시간
		long startTime = System.currentTimeMillis();

		// S3 URL 리스트 검증
		System.out.println("Processing " + s3UrlList.size() + " images for Grok API");
		if (s3UrlList.isEmpty()) {
			throw new IllegalArgumentException("S3 URL list is empty");
		}

		// S3 URL을 JSON 객체로 구성
		String imageUrlsJson = s3UrlList.stream()
				.map(url -> String.format("{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\",\"detail\":\"high\"}}", url))
				.collect(Collectors.joining(","));

		// 요청 텍스트
		String requestBody = String.format(
				"{\"model\":\"grok-2-vision-1212\",\"messages\":[" +
						"{\"role\":\"system\",\"content\":\"You are a helpful AI assistant created by xAI.\"}," +
						"{\"role\":\"user\",\"content\":[%s," +
						// "{\"type\":\"text\",\"text\":\"전세사기를 피하고 싶은 구매자의 관점에서 내가 보낸 등기부등본 이미지를 보고
						// 요약설명해줘. 그리고 그 요약 설명 중에 마크다운으로 테이블과 같은 형식으로 보기 쉽게 표현할 수 있으면 테이블 형태로 보여줘. \"}"
						// "{\"type\":\"text\",\"text\":\"전세사기를 피하고 싶은 구매자의 관점에서 내가 보낸 등기부등본 이미지를 보고 이미지
						// 속 정보들을 근거로 해서 유의해야 할 사항들 알려줘 (소유자가 최근에 변경되었는지 여부 판단, 소유자가 단독 소유인지, 공동 소유인지
						// 확인, 근저당권, 전세권, 가압류, 경매 확인, 임차인이 실제로 거주하고 있는지, 임대차 계약이 유효한지, 등기부등본이 최신인지 여부를
						// 이미지를 보고 판단한 결과를 포함시켜줘)\"}"
						"{\"type\":\"text\",\"text\":\"앞에서  보낸 ocr 결과에서  싶은 구매자의 관점에서 내가 보낸 등기부등본 이미지를 보고 이미지\r\n" + //
						"\t\t\t\t\t\t// ocr 결과를 근거로 해서 유의해야 할 사항들 알려줘 (소유자가 최근에 변경되었는지 여부 판단, 소유자가 단독 소유인지, 공동 소유인지\r\n" + //
						"\t\t\t\t\t\t// 확인, 근저당권, 전세권, 가압류, 경매 확인, 임차인이 실제로 거주하고 있는지, 임대차 계약이 유효한지, 등기부등본이 최신인지 여부를\r\n" + //
						"\t\t\t\t\t\t// ocr 결과를 보고 판단한 결과를 포함시켜줘) \"}"
						+
						"]}],\"max_tokens\":%d,\"temperature\":%.1f}",
				imageUrlsJson, s3UrlList.size() * 1000, 0.5);

		return webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				// .bodyToMono(ChatCompletionDTO.class)
				.doOnNext(response -> {
					// analyzePropertyImages() 종료 시간
					long endTime = System.currentTimeMillis();

					System.out.println("Grok API Response: " + response);
					System.out.println("analyzePropertyImages execution time: " + (endTime - startTime) + " ms");
				})
				.onErrorMap(e -> new RuntimeException("Failed to call Grok API", e));
	}
}