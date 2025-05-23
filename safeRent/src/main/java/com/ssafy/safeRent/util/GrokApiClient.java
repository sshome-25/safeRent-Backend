package com.ssafy.safeRent.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.safeRent.assessment.dto.model.AssessmentResult;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	public AssessmentResult analyzeRegisterImages(List<String> s3UrlList) {
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
		// String textPrompt = sanitizeString(
		// "전세사기를 피하고 싶은 구매자의 관점에서 내가 보낸 등기부등본 이미지를 보고 이미지 속 정보들을 근거로 해서"
		// + "유의해야 할 사항들 알려줘 (소유자가 최근에 변경되었는지 여부 판단, 소유자가 단독 소유인지, 공동 소유인지 확인, "
		// + "근저당권, 전세권, 가압류, 경매 확인, 임차인이 실제로 거주하고 있는지, 임대차 계약이 유효한지, "
		// + "등기부등본이 최신인지 여부를 이미지를 보고 판단한 결과를 포함시켜줘)");

		String textPrompt = sanitizeString(
				"전세사기를 피하고 싶은 구매자의 관점에서 내가 보낸 등기부등본 이미지를 보고 이미지 속 정보들을 근거로 해서 등본을 분석해줘."
						+ "답변은 key와 value 값으로 짝지어지게 답변해주고, key값과 value 사이에 :를 넣고, 각 key와 value 쌍 앞뒤로 |를 넣어서 구분해줘."
						+ "key값으로 꼭 들어가야 할 것은 종합평가, 발견된 위험요소1, 해결방안1, 발견된 위험요소2, 해결방안2로 총 5개야."
						+ "각각의 key 값은 overallAssessment, riskFactor1, solution1, riskFactor2, solution2로 답변해줘.");
		// + "소유자가 최근에 변경되었는지 여부 판단, 소유자가 단독 소유인지, 공동 소유인지 확인, "
		// + "근저당권, 전세권, 가압류, 경매 확인, 임차인이 실제로 거주하고 있는지, 임대차 계약이 유효한지, "
		// + "등기부등본이 최신인지를 확인해서 나온 발견된 위험요소와 해결방안을 짝지어서 답변해줘. ");

		String requestBody = String
				.format("{\"model\":\"grok-2-vision-1212\",\"messages\":["
						+ "{\"role\":\"system\",\"content\":\"You are a helpful AI assistant created by xAI.\"},"
						+ "{\"role\":\"user\",\"content\":[%s," + "{\"type\":\"text\",\"text\":\"%s\"}"
						+ "]}],\"max_tokens\":%d,\"temperature\":%.1f}", imageUrlsJson, textPrompt, s3UrlList.size() * 1000,
						0.5);
		System.out.println("Request Body: " + requestBody);
		System.out.println("========================================JSON 형식 검증 시작========================================");

		// JSON 형식 검증
		validateJson(requestBody);
		System.out.println(" webClient\n"
				+ "\t\t\t\t.post()");

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

				}).map(response -> extractKeyValues(response)) // 응답을 변환
				.onErrorMap(e -> {
					System.out.println(e.getMessage());
					return new RuntimeException(
							"Failed to call Grok API: " + e.getMessage() + ", Cause: " + e.getCause(),
							e);
				}).block();
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

	public static AssessmentResult extractKeyValues(String jsonResponse) {
		AssessmentResult result = new AssessmentResult();

		try {
			// JSON 파싱
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jsonResponse);

			// content 부분 추출
			String content = rootNode.path("choices")
					.path(0)
					.path("message")
					.path("content")
					.asText();

			// 마크다운 코드 블록 제거
			content = content.replaceAll("``````", "").trim();

			// 각 키에 대한 값 추출
			result.setOverallAssessment(extractKeyValue(content, "overallAssessment"));
			result.setRiskFactor1(extractKeyValue(content, "riskFactor1"));
			result.setSolution1(extractKeyValue(content, "solution1"));
			result.setRiskFactor2(extractKeyValue(content, "riskFactor2"));
			result.setSolution2(extractKeyValue(content, "solution2"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String extractKeyValue(String content, String key) {
		// 패턴 6: | key : 라벨 | value | (라벨이 한글인 경우)
		Pattern sixthPattern = Pattern.compile(
				"\\|\\s*" + key + "\\s*:\\s*[^|]+\\|\\s*([^|]+)\\s*\\|");
		Matcher sixthMatcher = sixthPattern.matcher(content);

		if (sixthMatcher.find()) {
			System.out.println("[extractKeyValue] 패턴6 매칭됨: " + key);
			return sixthMatcher.group(1).trim();
		}

		// 패턴: | key : | value |
		Pattern pattern = Pattern.compile("\\|\\s*" + key + "\\s*:\\s*\\|\\s*([^|]+)\\s*\\|");
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			System.out.println("[extractKeyValue] 패턴1 매칭됨: " + key);
			return matcher.group(1).trim();
		}

		// 패턴: | key : value |
		Pattern alternativePattern = Pattern.compile("\\|\\s*" + key + "\\s*:\\s*([^|]+)\\s*\\|");
		Matcher alternativeMatcher = alternativePattern.matcher(content);

		if (alternativeMatcher.find()) {
			System.out.println("[extractKeyValue] 패턴2 매칭됨: " + key);
			return alternativeMatcher.group(1).trim();
		}

		// 패턴 3: | key : 종류 | value |
		Pattern thirdPattern = Pattern.compile("\\|\\s*" + key + "\\s*:\\s*[^|]+\\|\\s*([^|]+)\\s*\\|");
		Matcher thirdMatcher = thirdPattern.matcher(content);

		if (thirdMatcher.find()) {
			System.out.println("[extractKeyValue] 패턴3 매칭됨: " + key);
			return thirdMatcher.group(1).trim();
		}

		// 패턴 4: | key | value |
		Pattern fourthPattern = Pattern.compile("\\|\\s*" + key + "\\s*\\|\\s*([^|]+)\\s*\\|");
		Matcher fourthMatcher = fourthPattern.matcher(content);

		if (fourthMatcher.find()) {
			System.out.println("[extractKeyValue] 패턴4 매칭됨: " + key);
			return fourthMatcher.group(1).trim();
		}

		// 패턴 5: |key:value|
		Pattern fifthPattern = Pattern.compile("\\|" + key + "\\:([^|]+)\\|");
		Matcher fifthMatcher = fifthPattern.matcher(content);

		if (fifthMatcher.find()) {
			System.out.println("[extractKeyValue] 패턴5 매칭됨: " + key);
			return fifthMatcher.group(1).trim();
		}

		System.out.println("[extractKeyValue] 매칭 실패: " + key);
		return null;
	}

}