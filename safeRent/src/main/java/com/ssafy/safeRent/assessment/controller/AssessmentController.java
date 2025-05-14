package com.ssafy.safeRent.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.service.AssessmentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/houses")
@RequiredArgsConstructor
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.safeRent.assessment.dto.Response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.Response.ContractAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.service.AssessmentService;
import com.ssafy.safeRent.user.dto.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {
  private final AssessmentService assessmentService;

  @PostMapping("/register")
  public Mono<ResponseEntity<String>> analyzeProperty(@RequestParam("id") String id,
      @RequestParam("register_file") MultipartFile pdfFile) {
    System.out.println("register 컨트롤러");
    return assessmentService.analyzeProperty(pdfFile);
  }
	private final AssessmentService assessmentService;
	
	// 진단서 생성과 매뭉 가격, 위치 등록
	@PostMapping
	public ResponseEntity<?> gradeAssessment(@AuthenticationPrincipal User user, @RequestBody AssessmentRequest assessmentRequest) {
		AssessmentResponse assessmentResponse = assessmentService.gradeAssessment(assessmentRequest);
		return ResponseEntity.ok()
				.body(assessmentResponse);
	}

	// 등기부 등록
	@PostMapping("/register")
	public ResponseEntity<?> saveRegister(@AuthenticationPrincipal User user, @RequestBody RegisterRequest registerRequest) {
		assessmentService.saveRegister(registerRequest);
		return ResponseEntity.ok("등록 완료");
	}

	// 계약서 등록
	@PostMapping("/contract")
	public ResponseEntity<?> saveContract(@AuthenticationPrincipal User user, @RequestBody ContractRequest contractRequest) {		
		assessmentService.saveContract(contractRequest);
		return ResponseEntity.ok("등록 완료");
	}

	// 진단서 ID에 대해서 등기부 조회
	@GetMapping("/register")
	public ResponseEntity<?> getRegisterAnalyze(@AuthenticationPrincipal User user,  @RequestParam("registerId") Long registerId) {
		RegisterAnalysisResponse registerAnalysisResponse = assessmentService.getRegisterAnalysis(user.getId(), registerId);
		return ResponseEntity.ok()
				.body(registerAnalysisResponse);
	}

	// 진단서 ID에 대해서 계약서 조회
	@GetMapping("/contract")
	public ResponseEntity<?> getContractAnalyze(@AuthenticationPrincipal User user, @RequestParam("contractId") Long contractId) {
		ContractAnalysisResponse contractAnalysisResponse = assessmentService.getContractAnalysis(user.getId(), contractId);
		return ResponseEntity.ok()
				.body(contractAnalysisResponse);
	}
}
