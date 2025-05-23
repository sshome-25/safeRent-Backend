package com.ssafy.safeRent.assessment.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.safeRent.assessment.dto.model.AssessResult;
import com.ssafy.safeRent.assessment.dto.model.AssessmentHouse;
import com.ssafy.safeRent.assessment.dto.model.AssessmentResult;
import com.ssafy.safeRent.assessment.dto.model.HouseInfo;
import com.ssafy.safeRent.assessment.dto.model.Register;
import com.ssafy.safeRent.assessment.dto.model.RegisterResult;
import com.ssafy.safeRent.assessment.dto.model.Statistic;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.response.AssessmentResultResponse;
import com.ssafy.safeRent.assessment.dto.response.RegisterAnalysisResponse;
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

	@Value("${aws.s3.base-url}")
	private String s3BaseUrl;

	@Transactional
	public AssessmentResponse assess(Long userId, HouseInfo houseInfo, MultipartFile registerFile) {
		AssessResult assessResult = isPriceSafe(houseInfo);
		RegisterResult registerResult = saveRegister(assessResult.getAssessmentHouseId(), registerFile);
		if (userId != 0) {
			assessmentRepository.saveAssessment(userId, registerResult.getRegisterId(), assessResult.getAssessmentHouseId());
		}

		AssessmentResponse response = AssessmentResponse.builder()
				.address(houseInfo.getAddress())
				.latitude(houseInfo.getLatitude())
				.longitude(houseInfo.getLongitude())
				.isSafe(assessResult.getIsSafe())
				.aroundAvgPrice(assessResult.getAroundAvgPrice().intValue())
				.grokResult(registerResult.getGrokResponse())
				.build();
		return response;
	}

	public AssessResult isPriceSafe(HouseInfo houseInfo) {
		Statistic statistic = assessmentRepository.getAreaStatistics(
				houseInfo.getLatitude(),
				houseInfo.getLongitude(),
				houseInfo.getArea());
		Boolean isSafe = statistic.getAvgPrice() > houseInfo.getPrice();
		Integer marketPrice = statistic.getAvgPrice().intValue();

		AssessmentHouse assessmentHouse = AssessmentHouse.builder()
				.address(houseInfo.getAddress())
				.latitude(houseInfo.getLatitude())
				.longitude(houseInfo.getLongitude())
				.area(houseInfo.getArea())
				.price(houseInfo.getPrice())
				.floor(houseInfo.getFloor())
				.marketPrice(marketPrice)
				.isSafe(isSafe)
				.build();

		if (houseInfo.getIsMember()) {
			assessmentRepository.saveAssessmentHouse(assessmentHouse);
		}
		return AssessResult.builder()
				.isSafe(isSafe)
				.assessmentHouseId(assessmentHouse.getId())
				.aroundAvgPrice(statistic.getAvgPrice())
				.build();

	}

	// return registerId
	@Transactional
	public RegisterResult saveRegister(Long assessmentId, MultipartFile registerFile) {

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
		AssessmentResult grokResponse = grokApiClient.analyzeRegisterImages(s3UrlList);

		System.out.println(grokResponse.getOverallAssessment());
		System.out.println(grokResponse.getRiskFactor1());
		System.out.println(grokResponse.getSolution1());
		System.out.println(grokResponse.getRiskFactor2());
		System.out.println(grokResponse.getSolution2());

		// 등본 진단 결과 DB에 저장
		Long registerId = saveAssessmentData(assessmentId, grokResponse, finalS3UrlList);
		return RegisterResult.builder()
				.registerId(registerId)
				.grokResponse(grokResponse)
				.build();
	}

	public void saveContract(ContractRequest contractRequest) {
		assessmentRepository.saveContract(null);
	}

	public RegisterAnalysisResponse getRegisterAnalysis(Long userId, Long assessmentId) {
		assessmentRepository.selectRegisterAnalysis();
		return null;
	}

	private Long saveAssessmentData(Long assessmentId, AssessmentResult grokResponse, List<String> s3UrlList) {

		// 1. analysis 테이블에 저장
		Long analysisId = assessmentRepository.saveAnalysis(grokResponse);
		if (analysisId == null) {
			throw new IllegalStateException("Failed to save analysis");
		}

		// 2. registers 테이블에 저장
		Register register = Register.builder()
				.analysisId(grokResponse.getId())
				.build();
		Long registerId = assessmentRepository.saveRegister(register);
		if (registerId == null) {
			throw new IllegalStateException("Failed to save register");
		}

		// 3. register_file_paths 테이블에 저장
		for (String filePath : s3UrlList) {
			assessmentRepository.saveRegisterFilePaths(filePath, register.getId());
		}

		return register.getId();
	}

	public List<AssessmentResultResponse> getAssessResults(Long userId) {
		return assessmentRepository.getAssessmentHousesByUserId(userId);
	}

	// 진단 결과 출력하는데 필요한 데이터 갖고옴
	public AssessmentResult getAnalysisResults(Long analysisId) {
		return assessmentRepository.getAnalysisById(analysisId);
	}
}
