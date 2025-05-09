package com.ssafy.safeRent.util;

import java.util.List;

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

	public Mono<ChatCompletionDTO> analyzePropertyImages(List<String> base64Images) {
		// S3 URL 두 개 생성 (page-1.jpeg, page-2.jpeg)
		String imageUrl1 = S3_IMAGE_BASE_URL + "page-1.jpeg";
		String imageUrl2 = S3_IMAGE_BASE_URL + "page-2.jpeg";

		// 두 개의 S3 URL을 JSON 객체로 구성
		String imageUrlsJson = String.format(
				"{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\",\"detail\":\"high\"}}," +
						"{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\",\"detail\":\"high\"}}",
				imageUrl1, imageUrl2);

		// 요청 텍스트
		String requestBody = String.format(
				"{\"model\":\"grok-2-vision-1212\",\"messages\":[" +
						"{\"role\":\"system\",\"content\":\"You are a helpful AI assistant created by xAI.\"}," +
						"{\"role\":\"user\",\"content\":[%s," +
						"{\"type\":\"text\",\"text\":\"등기부등본 문서를 분석하여 소유권 및 권리 변동 내역, 근저당권을 요약하되, 결과를 JSON 형식으로 반환해주세요. JSON 구조는 다음과 같아야 합니다:\\n{\\n  \\\"property\\\": {\\n    \\\"address\\\": \\\"부동산의 전체 주소(예: 서울특별시 강남구 테헤란로 123, 101동 1001호)\\\",\\n    \\\"type\\\": \\\"부동산 종류(예: 집합건물, 토지, 건물)\\\",\\n    \\\"parcel_number\\\": \\\"지번(예: 강남구 역삼동 123-45)\\\",\\n    \\\"land_number\\\": \\\"토지 고유번호(예: 111-1)\\\",\\n    \\\"building_area\\\": \\\"건물면적(예: 84.32㎡)\\\",\\n    \\\"land_area\\\": \\\"토지면적(예: 23.45㎡)\\\",\\n    \\\"purpose\\\": \\\"용도/종류(예: 아파트, 오피스텔, 상가)\\\",\\n    \\\"floor\\\": \\\"층수(예: 11층)\\\"\\n  },\\n  \\\"owners\\\": [\\n    {\\n      \\\"name\\\": \\\"현재 소유자 이름\\\",\\n      \\\"registration_number\\\": \\\"주민등록번호(예: 750405-*******)\\\",\\n      \\\"address\\\": \\\"소유자 주소\\\",\\n      \\\"acquisition_date\\\": \\\"취득일(YYYY-MM-DD, 예: 2025-02-19)\\\",\\n      \\\"acquisition_reason\\\": \\\"취득 사유(예: 매매, 증여, 경매)\\\",\\n      \\\"share\\\": \\\"지분(예: 100분의 1)\\\"\\n    }\\n  ],\\n  \\\"ownership_history\\\": [\\n    {\\n      \\\"name\\\": \\\"과거 소유자 이름\\\",\\n      \\\"registration_number\\\": \\\"과거 소유자 주민등록번호(예: 710609-*******)\\\",\\n      \\\"address\\\": \\\"과거 소유자 주소\\\",\\n      \\\"acquisition_date\\\": \\\"과거 취득일(YYYY-MM-DD, 예: 2016-08-31)\\\",\\n      \\\"acquisition_reason\\\": \\\"과거 취득 사유(예: 매매, 증여, 경매)\\\",\\n      \\\"share\\\": \\\"지분(예: 100분의 1)\\\"\\n    }\\n  ],\\n  \\\"mortgages\\\": [\\n    {\\n      \\\"holder\\\": \\\"근저당권자 이름\\\",\\n      \\\"amount\\\": \\\"채권최고액(예: 948,000,000원)\\\",\\n      \\\"registration_date\\\": \\\"설정일(YYYY-MM-DD, 예: 2016-09-09)\\\",\\n      \\\"purpose\\\": \\\"목적(예: 근저당권, 대출금)\\\",\\n      \\\"registration_number\\\": \\\"등기번호(예: 110138-0000014)\\\"\\n    }\\n  ],\\n  \\\"other_rights\\\": [\\n    {\\n      \\\"type\\\": \\\"권리 종류(예: 전세권, 임차권, 지상권)\\\",\\n      \\\"holder\\\": \\\"권리자 이름\\\",\\n      \\\"amount\\\": \\\"금액(예: 100,000,000원)\\\",\\n      \\\"registration_date\\\": \\\"권리 설정일(YYYY-MM-DD, 예: 2018-03-01)\\\",\\n      \\\"expiration_date\\\": \\\"만기일(YYYY-MM-DD, 예: 2020-03-01)\\\"\\n    }\\n  ],\\n  \\\"special_notes\\\": [\\n    \\\"특기사항\\\"\\n  ],\\n  \\\"summary\\\": {\\n    \\\"current_owner\\\": \\\"현재 소유자 이름\\\",\\n    \\\"mortgage_status\\\": \\\"근저당 등 권리관계 요약(예: 설정, 말소)\\\",\\n    \\\"other_rights_status\\\": \\\"기타 권리 요약(예: 전세권 있음, 없음)\\\"\\n  }\\n}\\n반드시 유효한 JSON 형식으로 결과를 반환하고, 등기부등본 데이터를 기반으로 정확히 채워주세요.\"}"
						+
						"]}],\"max_tokens\":%d,\"temperature\":%.1f}",
				imageUrlsJson, 2000, 0.5);

		return webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
				.retrieve()
				// .bodyToMono(String.class)
				.bodyToMono(ChatCompletionDTO.class)
				.doOnNext(response -> System.out.println("Grok API Response: " + response))
				.onErrorMap(e -> new RuntimeException("Failed to call Grok API", e));
	}
}