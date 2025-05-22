package com.ssafy.safeRent.assessment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.dto.model.HouseInfo;
import com.ssafy.safeRent.assessment.dto.request.HouseInfoRequest;
import com.ssafy.safeRent.assessment.dto.response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.response.AssessmentResultResponse;
import com.ssafy.safeRent.assessment.dto.response.ContractAnalysisResponse;
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

	@GetMapping
	public ResponseEntity<?> getAssessment(@AuthenticationPrincipal User user) {
		List<AssessmentResultResponse> results = assessmentService.getAssessResults(user.getId());
		return ResponseEntity.ok().body(results);
	}

	// 비회원에 대한 평가 api
	@PostMapping("/guest")
	public ResponseEntity<?> assessGuest(
			@RequestPart(value = "house_info") HouseInfoRequest houseInfoRequest,
			@RequestPart("register_file") MultipartFile registerFile) {
		HouseInfo houseInfo = HouseInfo.builder()
				.address(houseInfoRequest.getAddress())
				.area(houseInfoRequest.getArea())
				.floor(houseInfoRequest.getFloor())
				.isMember(false)
				.latitude(houseInfoRequest.getLatitude())
				.longitude(houseInfoRequest.getLongitude())
				.price(houseInfoRequest.getPrice())
				.build();

		AssessmentResponse assessmentResponse = assessmentService.assess(houseInfo);
		return ResponseEntity.ok().body(assessmentResponse);

	}

	@PostMapping("/member")
	public ResponseEntity<?> assessMember(
			@RequestPart(value = "house_info") HouseInfoRequest houseInfoRequest,
			@RequestPart("register_file") MultipartFile registerFile) {
		HouseInfo houseInfo = HouseInfo.builder()
				.address(houseInfoRequest.getAddress())
				.area(houseInfoRequest.getArea())
				.floor(houseInfoRequest.getFloor())
				.latitude(houseInfoRequest.getLatitude())
				.longitude(houseInfoRequest.getLongitude())
				.price(houseInfoRequest.getPrice())
				.isMember(true)
				.build();

		AssessmentResponse assessmentResponse = assessmentService.assess(houseInfo);
		return ResponseEntity.ok().body(assessmentResponse);

	}
}
