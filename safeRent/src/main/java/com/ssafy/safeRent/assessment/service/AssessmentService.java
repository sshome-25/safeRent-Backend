package com.ssafy.safeRent.assessment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.dto.Response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.Response.ContractAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.model.Assessment;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.repository.AssessmentRepository;
import com.ssafy.safeRent.util.GrokApiClient;
import com.ssafy.safeRent.util.PDFConverter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AssessmentService {

	private final AssessmentRepository assessmentRepository;
	private final GrokApiClient grokApiClient;
	private final PDFConverter pdfConverter;

	@Value("${aws.s3.base-url}")
	private String s3BaseUrl;

	public AssessmentResponse gradeAssessment(AssessmentRequest assessmentRequest) {
		Assessment assessment = Assessment.builder()
				.build();
		assessmentRepository.saveAssessment(assessment);
		return null;
	}

	@Transactional
	public Mono<Void> saveRegister(RegisterRequest registerRequest, MultipartFile registerFile) {

		List<String> s3UrlList = new ArrayList<>();

		// PDF 파일을 이미지로 변환 -> s3에 업로드
		// try {
		// s3UrlList = pdfConverter.convertPDFToImages(pdfFile);
		// } catch (IOException ioe) {
		// ioe.printStackTrace();
		// }

		// 테스트용 S3 URL 리스트
		s3UrlList.add(s3BaseUrl + "/pdf-img/page-1.jpeg");
		s3UrlList.add(s3BaseUrl + "/pdf-img/page-2.jpeg");
		s3UrlList.add(s3BaseUrl + "/pdf-img/page-8.jpeg");

		return grokApiClient.analyzeRegisterImages(s3UrlList)
				.flatMap(grokResponse -> {
					// 등본 진단 결과 DB에 저장
					return saveAssessmentData(registerRequest.getAssessmentId(), grokResponse, s3UrlList);

				})
				.onErrorResume(Exception.class, e -> {
					// System.err.println("Error occurred: " + e.getMessage());
					return Mono.error(new RuntimeException("Failed to process registration: " + e.getMessage(), e));
				});

	}

	public void saveContract(ContractRequest contractRequest) {
		assessmentRepository.saveContract(null);
	}

	public RegisterAnalysisResponse getRegisterAnalysis(Long userId, Long assessmentId) {
		assessmentRepository.selectRegisterAnalysis();
		return null;
	}

	public ContractAnalysisResponse getContractAnalysis(Long userId, Long assessmentId) {
		assessmentRepository.selectContractAnalysis();
		return null;
	}

	private Mono<Void> saveAssessmentData(Long assessmentId, String grokResponse, List<String> s3UrlList) {
		// 1. analysis 테이블에 저장
		Long analysisId = assessmentRepository.saveAnalysis(grokResponse, 5);
		if (analysisId == null) {
			return Mono.error(new IllegalStateException("Failed to save analysis"));
		}

		// 2. registers 테이블에 저장
		Long registerId = assessmentRepository.saveRegister(analysisId);
		if (registerId == null) {
			return Mono.error(new IllegalStateException("Failed to save register"));
		}

		// 3. assessments 테이블 업데이트
		assessmentRepository.updateAssessmentRegister(registerId, assessmentId);

		// 4. register_file_paths 테이블에 저장
		return Flux.fromIterable(s3UrlList)
				.flatMap(
						filePath -> Mono.fromRunnable(() -> assessmentRepository.saveRegisterFilePaths(filePath, registerId)))
				.then();
	}
}
