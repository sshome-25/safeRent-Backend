package com.ssafy.safeRent.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.dto.Response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.Response.ContractAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.service.AssessmentService;
import com.ssafy.safeRent.user.dto.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

	private final AssessmentService assessmentService;
	
	@GetMapping("/health")
	public ResponseEntity<?> healthCheck() {
		return ResponseEntity.ok().body("health check");
	}

	// 진단서 생성과 매물 가격, 위치 등록
	@PostMapping
	public ResponseEntity<?> gradeAssessment(@AuthenticationPrincipal User user,
			@RequestBody AssessmentRequest assessmentRequest) {
		AssessmentResponse assessmentResponse = assessmentService.gradeAssessment(assessmentRequest);
		return ResponseEntity.ok().body(assessmentResponse);
	}

	// 등기부 등록
	@PostMapping("/register")
	public Mono<ResponseEntity<String>> saveRegister(@AuthenticationPrincipal User user,
			@RequestPart("assessment_id") RegisterRequest registerRequest,
			@RequestPart("register_file") MultipartFile registerFile) {

		// 비동기 처리
		return assessmentService.saveRegister(
				registerRequest, registerFile)
				.then(Mono.just(ResponseEntity.ok("등록 완료")))
				.onErrorResume(Exception.class, e -> Mono.just(ResponseEntity.badRequest()
						.body("등록 실패: " + e.getMessage())));
	}

	// 계약서 등록
	@PostMapping("/contract")
	public ResponseEntity<?> saveContract(@AuthenticationPrincipal User user,
			@RequestBody ContractRequest contractRequest) {
		assessmentService.saveContract(contractRequest);
		return ResponseEntity.ok("등록 완료");
	}

	// 진단서 ID에 대해서 등기부 조회
	@GetMapping("/register")
	public ResponseEntity<?> getRegisterAnalyze(@AuthenticationPrincipal User user,
			@RequestParam("register_id") Long registerId) {
		RegisterAnalysisResponse registerAnalysisResponse = assessmentService
				.getRegisterAnalysis(user.getId(), registerId);
		System.out.println("register");
		return ResponseEntity.ok().body(registerAnalysisResponse);
	}

	// 진단서 ID에 대해서 계약서 조회
	@GetMapping("/contract")
	public ResponseEntity<?> getContractAnalyze(@AuthenticationPrincipal User user,
			@RequestParam("contractId") Long contractId) {
		ContractAnalysisResponse contractAnalysisResponse = assessmentService
				.getContractAnalysis(user.getId(), contractId);
		return ResponseEntity.ok().body(contractAnalysisResponse);
	}
	
	// 비회원에 대한 평가 api
	@PostMapping("/guest")
	public ResponseEntity<?> assessGuest() {
		return ResponseEntity.ok().body("success");
	}
}
