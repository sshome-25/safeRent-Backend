package com.ssafy.safeRent.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

	// 진단서 생성과 매뭉 가격, 위치 등록
	@PostMapping
	public ResponseEntity<?> issueAssessment(@RequestBody AssessmentRequest assessmentRequest) {
		return ResponseEntity.ok(null);
	}

	// 등기부 등록
	@PostMapping("/register")
	public ResponseEntity<?> saveRegister(@RequestBody RegisterRequest registerRequest) {		
		return ResponseEntity.ok(null);
	}

	// 계약서 등록
	@PostMapping("/contract")
	public ResponseEntity<?> saveContract(@RequestBody ContractRequest contractRequest) {		
		return ResponseEntity.ok(null);
	}

	// 진단서 ID에 대해서 등기부 조회
	@GetMapping("/register")
	public ResponseEntity<?> getRegisterAnalyze() {
		return ResponseEntity.ok(null);
	}

	// 진단서 ID에 대해서 계약서 조회
	@GetMapping("/contract")
	public ResponseEntity<?> getContractAnalyze() {		
		return ResponseEntity.ok(null);
	}
}
