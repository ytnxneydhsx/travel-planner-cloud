package org.example.userservice.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.userservice.entity.User;

@Mapper
public interface UserMapper {

    @Insert("""
            INSERT INTO users (
                username,
                password,
                nickname,
                status
            ) VALUES (
                #{username},
                #{password},
                #{nickname},
                #{status}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("""
            SELECT
                id,
                username,
                password,
                nickname,
                status,
                created_at,
                updated_at
            FROM users
            WHERE id = #{id}
            """)
    User selectById(@Param("id") Long id);

    @Select("""
            SELECT
                id,
                username,
                password,
                nickname,
                status,
                created_at,
                updated_at
            FROM users
            WHERE username = #{username}
            """)
    User selectByUsername(@Param("username") String username);

    @Update("""
            UPDATE users
            SET password = #{password}
            WHERE id = #{id}
            """)
    int updatePasswordById(User user);
}
