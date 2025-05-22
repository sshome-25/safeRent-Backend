package com.ssafy.safeRent.assessment.repository;

import com.ssafy.safeRent.assessment.dto.model.AssessmentResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ssafy.safeRent.assessment.dto.model.Assessment;
import com.ssafy.safeRent.assessment.dto.model.Statistic;

@Mapper
public interface AssessmentRepository {

	@Insert("insert into assessments ("
			+ "user_id, contract_id, register_id, assessment_house_id"
			+ ") values ("
			+ "#{userId},"
			+ "#{assessmentHouseId}"
			+ ")")
	void saveAssessment(Assessment assessment);

	@Update("update assessments "
			+ "set "
			+ "register_id = #{registerId}, "
			+ "post_id = #{assessmentId} and status = 'ACTIVE'")
	void updateAssessmentRegister(Long registerId, Long assessmentId);


@Insert("INSERT INTO analysis (overall_assessment, risk_factor1, solution1, risk_factor2, solution2) "
	+ "VALUES (#{overallAssessment}, #{riskFactor1}, #{solution1}, #{riskFactor2}, #{solution2})")
@Options(useGeneratedKeys = true, keyColumn = "analysis_id")
	Long saveAnalysis(AssessmentResult assessmentResult);

	@Insert("INSERT INTO registers (analysis_id) VALUES (#{analysisId})")
	@Options(useGeneratedKeys = true, keyColumn = "register_id")
	Long saveRegister(Long analysisId);

	@Insert("INSERT INTO register_file_paths (file_path, register_id) VALUES (#{filePath}, #{registerId})")
	void saveRegisterFilePaths(String filePath, Long registerId);

	// =======
	@Insert("insert into contracts ("
			+ "analysis_id"
			+ ") values ("
			+ "#{analysisId}"
			+ ")")
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
			+ "    a.created_at DESC")
	void selectRegisterAnalysis();

	@Select("SELECT "
			+ "COUNT(*) as trade_cnt, "
			+ "AVG(price) as avg_price, "
			+ "MIN(price) as min_price, "
			+ "MAX(price) as max_price, "
			+ "AVG(price / area) as price_per_area "
			+ "FROM traded_houses "
			+ "WHERE MBRContains( "
			+ "    ST_GeomFromText(CONCAT( "
			+ "        'POLYGON((', "
			+ "        #{latitude} - 0.045, ' ', #{longitude} - 0.057, ',', "
			+ "        #{latitude} + 0.045, ' ', #{longitude} - 0.057, ',', "
			+ "        #{latitude} + 0.045, ' ', #{longitude} + 0.057, ',', "
			+ "        #{latitude} - 0.045, ' ', #{longitude} + 0.057, ',', "
			+ "        #{latitude} - 0.045, ' ', #{longitude} - 0.057, "
			+ "        '))'"
			+ "    ), 4326), "
			+ "    location "
			+ ") "
			+ "AND ST_Distance_Sphere( "
			+ "    location, "
			+ "    ST_GeomFromText(CONCAT('POINT(', #{latitude}, ' ', #{longitude}, ')'), 4326) " // 수정됨
			+ ") <= 1000 "
			+ "AND area BETWEEN #{area} - 10 AND #{area} + 10 " // 수정됨
			+ "AND transaction_date >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR)")
	Statistic getAreaStatistics(@Param("latitude") Double latitude,
			@Param("longitude") Double longitude,
			@Param("area") Double area);
}
