package com.ssafy.safeRent.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.service.AssessmentService;
import com.ssafy.safeRent.user.dto.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/assessments")
@Slf4j
@RequiredArgsConstructor
public class AssessmentController {

	private final AssessmentService assessmentService;

	// 비회원에 대한 평가 api
	@PostMapping("/guest")
	// public ResponseEntity<?> assessGuest(
	public Long assessGuest(
			@RequestParam(value = "latitude") Double latitude,
			@RequestParam(value = "longitude") Double longitude,
			@RequestParam(value = "price") Integer price,
			@RequestParam(value = "area") Double area,
			@RequestPart("register_file") MultipartFile registerFile) {
		return assessmentService.saveRegister(new RegisterRequest(), registerFile);
		// Statistic statistic = assessmentService.guessPrice(latitude, longitude,
		// area);
		// return ResponseEntity.ok().body("success");

	}

	@PostMapping("/member")
	public ResponseEntity<?> assessMember(
			@RequestParam(value = "latitude") Double latitude,
			@RequestParam(value = "longitude") Double longitude,
			@RequestParam(value = "price") Integer price,
			@RequestPart("register_file") MultipartFile registerFile,
			@RequestPart("contract_file") MultipartFile contractFile) {
		log.info("member");
		return ResponseEntity.ok().body("success");
	}
}
