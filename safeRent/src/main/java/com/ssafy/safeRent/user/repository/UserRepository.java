package com.ssafy.safeRent.user.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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

}
