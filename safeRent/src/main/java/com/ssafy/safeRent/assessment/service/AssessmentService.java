package com.ssafy.safeRent.assessment.service;

import org.springframework.stereotype.Service;

import com.ssafy.safeRent.assessment.dto.Response.AssessmentResponse;
import com.ssafy.safeRent.assessment.dto.Response.ContractAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.Response.RegisterAnalysisResponse;
import com.ssafy.safeRent.assessment.dto.model.Assessment;
import com.ssafy.safeRent.assessment.dto.request.AssessmentRequest;
import com.ssafy.safeRent.assessment.dto.request.ContractRequest;
import com.ssafy.safeRent.assessment.dto.request.RegisterRequest;
import com.ssafy.safeRent.assessment.repository.AssessmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentService {
	
	private final AssessmentRepository assessmentRepository;
	
	public AssessmentResponse gradeAssessment(AssessmentRequest assessmentRequest) {
		Assessment assessment = Assessment.builder()
				.build();
		assessmentRepository.saveAssessment(assessment);
		return null;
	}
    
	public void saveRegister(RegisterRequest registerRequest) {
		assessmentRepository.saveRegister(null);
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
}
