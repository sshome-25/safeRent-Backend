package com.ssafy.safeRent.assessment.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ssafy.safeRent.assessment.dto.model.Assessment;

@Mapper
public interface AssessmentRepository {

	@Insert("insert into assessments ("
			+ "user_id, contract_id, register_id, assessment_house_id"
			+ ") values ("
			+ "#{userId},"
			+ "#{assessmentHouseId}"
			+ ");")
	void saveAssessment(Assessment assessment);

	@Update("update assessments "
			+ "set"
			+ "register_id = #{registerId}"
			+ "where assessment_id = #{assessmentId} and status = 'ACTIVE';")
	void updateAssessmentRegister();

	@Update("update assessments "
			+ "set"
			+ "contract_id = #{contractId}"
			+ "where assessment_id = #{assessmentId} and status = 'ACTIVE';")
	void updateAssessmentContract();

	@Insert("insert into registers ("
			+ "analysis_id"
			+ ") values ("
			+ "#{analysisId}"
			+ ");")
	void saveRegister(Long analysisId);

	@Insert("insert into contracts ("
			+ "analysis_id"
			+ ") values ("
			+ "#{analysisId}"
			+ ");")
	void saveContract(Long analysisId);
	
	// TODO: file query custom
	@Select("SELECT "
			+ "r.register_id,\r\n"
			+ "an.analysis_id AS register_analysis_id, "
			+ "an.summary AS register_analysis_summary, "
			+ "an.risk_degree AS register_risk_degree, "
			+ "GROUP_CONCAT(DISTINCT rp.file_path SEPARATOR '; ') AS register_files "
			+ "FROM "
			+ "    assessments a "
			+ "JOIN "
			+ "    registers r ON a.register_id = r.register_id "
			+ "JOIN "
			+ "    analysis an ON r.analysis_id = an.analysis_id "
			+ "LEFT JOIN "
			+ "    register_file_paths rp ON r.register_id = rp.register_id "
			+ "WHERE "
			+ "    a.status = 'ACTIVE' and "
			+ "    a.assessment_id = 1 "
			+ "ORDER BY "
			+ "    a.created_at DESC;")
	void selectRegisterAnalysis();

	@Select("SELECT "
			+ "c.contract_id, "
			+ "an.analysis_id AS contract_analysis_id, "
			+ "an.summary AS contract_analysis_summary, "
			+ "an.risk_degree AS contract_risk_degree, "
			+ "GROUP_CONCAT(DISTINCT cp.file_path SEPARATOR '; ') AS contract_files "
			+ "FROM "
			+ "    assessments a "
			+ "JOIN "
			+ "    contracts c ON a.register_id = c.contract_id "
			+ "JOIN "
			+ "    analysis an ON c.analysis_id = an.analysis_id "
			+ "LEFT JOIN "
			+ "   contract_file_paths cp ON r.register_id = rp.register_id "
			+ "WHERE "
			+ "    a.status = 'ACTIVE' and "
			+ "    a.assessment_id = 1 "
			+ "ORDER BY "
			+ "    a.created_at DESC;")
	void selectContractAnalysis();

}
