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
import com.ssafy.safeRent.assessment.dto.Response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.Response.ContractAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
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

		// 테스트용 응답 객체 설정
		assessmentResponse = new AssessmentResponse();
		// 필요한 필드 설정...

		registerAnalysisResponse = new RegisterAnalysisResponse();
		// 필요한 필드 설정...

		contractAnalysisResponse = new ContractAnalysisResponse();
		// 필요한 필드 설정...
	}

	@Test
	@DisplayName("진단서 생성 API 테스트")
	@WithMockUser
	void gradeAssessmentTest() throws Exception {
		// Given
		when(assessmentService.gradeAssessment(any(AssessmentRequest.class))).thenReturn(assessmentResponse);

		// When & Then
		mockMvc
				.perform(post("/api/assessments")
						.with(SecurityMockMvcRequestPostProcessors.user(mockUser))
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(assessmentRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
	}

	@Test
	@DisplayName("등기부 등록 API 테스트 - 성공")
	@WithMockUser
	void saveRegisterTest_Success() throws Exception {
		// Given
		RegisterRequest registerRequest = RegisterRequest.builder().assessmentId(12345L).build();
		MockMultipartFile registerFile = new MockMultipartFile("register_file", "document.pdf", "application/pdf",
				"PDF content".getBytes());
		MockMultipartFile assessmentIdPart = new MockMultipartFile("assessment_id", "", "application/json",
				objectMapper.writeValueAsString(registerRequest).getBytes());
		doReturn(Mono.empty()).when(assessmentService).saveRegister(any(RegisterRequest.class), any());

		// When & Then
		mockMvc
				.perform(multipart("/api/assessments/register")
						.file(registerFile)
						.file(assessmentIdPart)
						.with(SecurityMockMvcRequestPostProcessors.user(mockUser))
						.with(csrf())
						.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("계약서 등록 API 테스트")
	@WithMockUser
	void saveContractTest() throws Exception {
		// Given
		doNothing().when(assessmentService).saveContract(any(ContractRequest.class));

		// When & Then
		mockMvc
				.perform(post("/api/assessments/contract")
						.with(SecurityMockMvcRequestPostProcessors.user(mockUser))
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(contractRequest)))
				.andExpect(status().isOk())
				.andExpect(content().string("등록 완료"));
	}

	@Test
	@DisplayName("등기부 조회 API 테스트")
	@WithMockUser
	void getRegisterAnalyzeTest() throws Exception {
		// Given
		Long registerId = 1L;
		when(assessmentService.getRegisterAnalysis(eq(mockUser.getId()), eq(registerId)))
				.thenReturn(registerAnalysisResponse);

		// When & Then
		mockMvc
				.perform(get("/api/assessments/register", registerId)
						.with(SecurityMockMvcRequestPostProcessors.user(mockUser))
						.param("registerId", String.valueOf(registerId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
	}

	@Test
	@DisplayName("계약서 조회 API 테스트")
	@WithMockUser
	void getContractAnalyzeTest() throws Exception {
		// Given
		Long contractId = 1L;
		when(assessmentService.getContractAnalysis(eq(mockUser.getId()), eq(contractId)))
				.thenReturn(contractAnalysisResponse);

		// When & Then
		mockMvc
				.perform(get("/api/assessments/contract")
						.with(SecurityMockMvcRequestPostProcessors.user(mockUser))
						.param("contractId", String.valueOf(contractId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
	}
}