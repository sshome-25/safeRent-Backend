package com.ssafy.safeRent.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class GrokApiClient {
	private final WebClient webClient;

	public GrokApiClient(WebClient.Builder webClientBuilder, @Value("${grok.api.key}") String apiKey) {
		HttpClient httpClient = HttpClient
				.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.doOnConnected(conn -> conn
						.addHandlerLast(new ReadTimeoutHandler(80))
						.addHandlerLast(new WriteTimeoutHandler(80)));

		this.webClient = webClientBuilder
				.baseUrl("https://api.x.ai/v1/chat/completions")
				.defaultHeader("Authorization", "Bearer " + apiKey)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}

	public Mono<String> analyzeRegisterImages(List<String> s3UrlList) {
		long startTime = System.currentTimeMillis();

		// S3 URL 리스트 검증
		if (s3UrlList.isEmpty()) {
			throw new IllegalArgumentException("S3 URL list is empty");
		}

		// S3 URL 리스트 검증 및 제어 문자 제거
		List<String> sanitizedUrls = s3UrlList.stream().map(this::sanitizeString).collect(Collectors.toList());

		String imageUrlsJson = sanitizedUrls
				.stream()
				.map(url -> String
						.format("{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\",\"detail\":\"high\"}}", url))
				.collect(Collectors.joining(","));

		// 텍스트 프롬프트에서 제어 문자 제거
		String textPrompt = sanitizeString(
				"전세사기를 피하고 싶은 구매자의 관점에서 내가 보낸 등기부등본 이미지를 보고 이미지 속 정보들을 근거로 해서"
						+ "유의해야 할 사항들 알려줘 (소유자가 최근에 변경되었는지 여부 판단, 소유자가 단독 소유인지, 공동 소유인지 확인, "
						+ "근저당권, 전세권, 가압류, 경매 확인, 임차인이 실제로 거주하고 있는지, 임대차 계약이 유효한지, "
						+ "등기부등본이 최신인지 여부를 이미지를 보고 판단한 결과를 포함시켜줘)");

		String requestBody = String
				.format("{\"model\":\"grok-2-vision-1212\",\"messages\":["
						+ "{\"role\":\"system\",\"content\":\"You are a helpful AI assistant created by xAI.\"},"
						+ "{\"role\":\"user\",\"content\":[%s," + "{\"type\":\"text\",\"text\":\"%s\"}"
						+ "]}],\"max_tokens\":%d,\"temperature\":%.1f}", imageUrlsJson, textPrompt, s3UrlList.size() * 1000,
						0.5);
		System.out.println("Request Body: " + requestBody);

		// JSON 형식 검증
		validateJson(requestBody);

		return webClient
				.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.doOnNext(response -> {
					long endTime = System.currentTimeMillis();
					System.out.println("Grok API Response: " + response);
					System.out.println("analyzePropertyImages() execution time: " + (endTime - startTime) + " ms");
				})
				.onErrorMap(e -> new RuntimeException(
						"Failed to call Grok API: " + e.getMessage() + ", Cause: " + e.getCause(), e));
	}

	private String sanitizeString(String input) {
		if (input == null) {
			return null;
		}
		// 제어 문자 제거
		return input.replaceAll("[\\p{Cntrl}]", "");
	}

	// JSON 형식 검증
	private void validateJson(String json) {
		try {
			new ObjectMapper().readTree(json);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
		}
	}
}