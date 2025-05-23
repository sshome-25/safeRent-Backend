package com.ssafy.safeRent.assessment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.safeRent.assessment.controller.AssessmentController;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.dto.response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.response.ContractAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.service.AssessmentService;
import com.ssafy.safeRent.user.dto.model.User;

import reactor.core.publisher.Mono;

@WebMvcTest(AssessmentController.class)
public class AssessmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AssessmentService assessmentService;

	private User mockUser;
	private AssessmentRequest assessmentRequest;
	private RegisterRequest registerRequest;
	private ContractRequest contractRequest;
	private AssessmentResponse assessmentResponse;
	private RegisterAnalysisResponse registerAnalysisResponse;
	private ContractAnalysisResponse contractAnalysisResponse;

	@BeforeEach
	void setUp() {
		// 테스트용 사용자 설정
		mockUser = User.builder().id(1L).nickname("testuser").build();
		new User();

		// 테스트용 요청 객체 설정
		assessmentRequest = AssessmentRequest.builder().build();

		registerRequest = RegisterRequest.builder().build();

		contractRequest = ContractRequest.builder().build();
		// 필요한 필드 설정...

		registerAnalysisResponse = new RegisterAnalysisResponse();
		// 필요한 필드 설정...

		contractAnalysisResponse = new ContractAnalysisResponse();
		// 필요한 필드 설정...
	}

}