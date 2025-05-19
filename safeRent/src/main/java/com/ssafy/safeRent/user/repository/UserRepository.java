package com.ssafy.safeRent.user.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.ssafy.safeRent.user.dto.model.User;

@Mapper
public interface UserRepository {

	@Select("SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE nickname = #{nickname}")
    boolean existsByNickname(String nickname);
    
    @Select("SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);
    
    @Insert("INSERT INTO users (password, email, nickname) " +
            "VALUES (#{password}, #{email}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "user_id")
    Long save(User user);

    @Select("select * from users where nickname = #{nickname}")
	User findByNickname(String nickname);

    @Select("select * from users where email = #{email}")
    @Results({
        @Result(property = "id", column = "user_id")
    })
	User findByEmail(String email);

}
