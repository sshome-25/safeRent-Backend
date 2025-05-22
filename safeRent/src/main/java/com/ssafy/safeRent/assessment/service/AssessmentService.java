package com.ssafy.safeRent.assessment.service;

import com.ssafy.safeRent.assessment.dto.model.AssessmentResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.dto.Response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.model.Assessment;
import com.ssafy.safeRent.assessment.dto.model.Statistic;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.repository.AssessmentRepository;
import com.ssafy.safeRent.util.GrokApiClient;
import com.ssafy.safeRent.util.PDFConverter;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AssessmentService {

	private final AssessmentRepository assessmentRepository;
	private final GrokApiClient grokApiClient;
	private final PDFConverter pdfConverter;

	public AssessmentResponse gradeAssessment(AssessmentRequest assessmentRequest) {
		Assessment assessment = Assessment.builder()
				.build();
		assessmentRepository.saveAssessment(assessment);
		return null;
	}

	public Statistic guessPrice(Double latitude, Double longitude, Double area) {
		Statistic statistic = assessmentRepository.getAreaStatistics(latitude, longitude, area);
		System.out.println(statistic);
		return statistic;
	}





	// return registerId
	@Transactional
	public Long saveRegister(RegisterRequest registerRequest, MultipartFile registerFile) {

		List<String> s3UrlList = new ArrayList<>();

		// 1. PDF 파일을 이미지로 변환 -> s3에 업로드
		try {
			s3UrlList = pdfConverter.convertPDFToImagesAndUpload(registerFile, "pdf-img");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}


		System.out.println("// 2. api에 이미지 보내서 받은 String 응답 -> 파싱하기");
		// 2. api에 이미지 보내서 받은 String 응답 -> 파싱하기
		// 3. 진단 테이블에 insert 하기.
		List<String> finalS3UrlList = s3UrlList;
		Long registerId = null;
		AssessmentResult grokResponse =  grokApiClient.analyzeRegisterImages(s3UrlList);

		System.out.println(grokResponse.getOverallAssessment());
		System.out.println(grokResponse.getRiskFactor1());
		System.out.println(grokResponse.getSolution1());
		System.out.println(grokResponse.getRiskFactor2());
		System.out.println(grokResponse.getSolution2());
		System.out.println();

		// 등본 진단 결과 DB에 저장
		return saveAssessmentData(registerRequest.getAssessmentId(), grokResponse, finalS3UrlList);

	}

	public void saveContract(ContractRequest contractRequest) {
		assessmentRepository.saveContract(null);
	}

	public RegisterAnalysisResponse getRegisterAnalysis(Long userId, Long assessmentId) {
		assessmentRepository.selectRegisterAnalysis();
		return null;
	}

	private Long saveAssessmentData(Long assessmentId, AssessmentResult grokResponse, List<String> s3UrlList) {

		System.out.println(" save 메소드 진입");

		// 1. analysis 테이블에 저장
		Long analysisId = assessmentRepository.saveAnalysis(grokResponse);
		if (analysisId == null) {
			throw new IllegalStateException("Failed to save analysis");
		}

		// 2. registers 테이블에 저장
		Long registerId = assessmentRepository.saveRegister(analysisId);
		if (registerId == null) {
			throw new IllegalStateException("Failed to save register");
		}


		// 3. register_file_paths 테이블에 저장
		for(String filePath : s3UrlList){
			assessmentRepository.saveRegisterFilePaths(filePath, registerId);

		}


		return registerId;
	}
}
