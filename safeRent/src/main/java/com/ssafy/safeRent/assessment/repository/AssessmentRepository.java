package com.ssafy.safeRent.assessment.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ssafy.safeRent.assessment.dto.model.AssessmentHouse;
import com.ssafy.safeRent.assessment.dto.model.AssessmentResult;
import com.ssafy.safeRent.assessment.dto.model.Register;
import com.ssafy.safeRent.assessment.dto.model.Statistic;
import com.ssafy.safeRent.assessment.dto.response.AssessmentResultResponse;

@Mapper
public interface AssessmentRepository {

	@Update("update assessments "
			+ "set "
			+ "register_id = #{registerId}, "
			+ "post_id = #{assessmentId} and status = 'ACTIVE'")
	void updateAssessmentRegister(Long registerId, Long assessmentId);

	@Insert("INSERT INTO analysis (overall_assessment, risk_factor_1, solution_1, risk_factor_2, solution_2) "
			+ "VALUES (#{overallAssessment}, #{riskFactor1}, #{solution1}, #{riskFactor2}, #{solution2})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	Long saveAnalysis(AssessmentResult assessmentResult);

	@Select("SELECT " +
			"a.analysis_id, " +
			"a.overall_assessment, " +
			"a.risk_factor_1, " +
			"a.solution_1, " +
			"a.risk_factor_2, " +
			"a.solution_2, " +
			"a.created_at, " +
			"a.updated_at, " +
			"a.status, " +
			"ah.price, " +
			"ah.market_price AS market_price " +
			"FROM analysis a " +
			"INNER JOIN registers r ON a.analysis_id = r.analysis_id " +
			"INNER JOIN assessments s ON r.register_id = s.register_id " +
			"INNER JOIN assessment_houses ah ON s.assessment_house_id = ah.assessment_house_id " +
			"WHERE a.analysis_id = #{analysisId} " +
			"LIMIT 1")
	AssessmentResult getAnalysisById(Long analysisId);

	@Insert("INSERT INTO registers (analysis_id) VALUES (#{analysisId})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	Long saveRegister(Register register);

	@Insert("INSERT INTO register_file_paths (file_path, register_id) VALUES (#{filePath}, #{registerId})")
	void saveRegisterFilePaths(@Param("filePath") String filePath, @Param("registerId") Long registerId);

	@Insert("insert into contracts ("
			+ "analysis_id"
			+ ") values ("
			+ "#{analysisId}"
			+ ")")
	void saveContract(Long analysisId);

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

	@Insert("INSERT INTO assessment_houses ("
			+ "location, "
			+ "price, "
			+ "market_price, "
			+ "area, "
			+ "floor, "
			+ "address, "
			+ "is_safe"
			+ ") VALUES ("
			+ "ST_SRID(POINT(#{longitude}, #{latitude}), 4326),"
			+ "#{price}, "
			+ "#{marketPrice}, "
			+ "#{area}, "
			+ "#{floor}, "
			+ "#{address}, "
			+ "#{isSafe}"
			+ ")")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	Long saveAssessmentHouse(AssessmentHouse assessmentHouse);

	@Select("SELECT ah.assessment_house_id as id, " +
			"ST_X(ah.location) as longitude, " +
			"ST_Y(ah.location) as latitude, " +
			"ah.price as price, " +
			"ah.market_price as market_price, " +
			"ah.area as area, " +
			"ah.floor as floor, " +
			"ah.created_at as createdAt, " +
			"ah.address as address, " +
			"ah.status as is_status, " +
			"ah.is_safe as is_safe, " +
			"r.analysis_id as analysis_id " +
			"FROM assessment_houses ah " +
			"INNER JOIN assessments a ON ah.assessment_house_id = a.assessment_house_id  " +
			"INNER JOIN registers r ON a.register_id = r.register_id " +
			"WHERE a.user_id = #{userId} " +
			"AND a.status = 'ACTIVE' " +
			"AND ah.status = 'ACTIVE'")
	List<AssessmentResultResponse> getAssessmentHousesByUserId(@Param("userId") Long userId);

	@Insert("INSERT INTO assessments ("
			+ "user_id, "
			+ "register_id, "
			+ "assessment_house_id"
			+ ") VALUES ("
			+ "#{userId}, "
			+ "#{registerId},"
			+ "#{assessmentHouseId}"
			+ ")")
	void saveAssessment(@Param("userId") Long userId, @Param("registerId") Long registerId,
			@Param("assessmentHouseId") Long assessmentHouseId);
}
