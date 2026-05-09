package org.example.destinationservice.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.destinationservice.entity.Destination;

@Mapper
public interface DestinationMapper {

    @Insert("""
            INSERT INTO destination (
                name,
                region_code,
                address,
                summary,
                description,
                cover_image_url,
                status
            ) VALUES (
                #{name},
                #{regionCode},
                #{address},
                #{summary},
                #{description},
                #{coverImageUrl},
                #{status}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Destination destination);

    @Select("""
            SELECT
                id,
                name,
                region_code,
                address,
                summary,
                description,
                cover_image_url,
                status,
                gmt_create,
                gmt_modified
            FROM destination
            WHERE id = #{id}
            """)
    Destination selectById(@Param("id") Long id);

    @Select("""
            SELECT
                id,
                name,
                region_code,
                address,
                summary,
                description,
                cover_image_url,
                status,
                gmt_create,
                gmt_modified
            FROM destination
            WHERE name LIKE CONCAT(#{namePrefix}, '%')
            ORDER BY id DESC
            """)
    List<Destination> selectByNamePrefix(@Param("namePrefix") String namePrefix);

    @Select("""
            SELECT
                id,
                name,
                region_code,
                address,
                summary,
                description,
                cover_image_url,
                status,
                gmt_create,
                gmt_modified
            FROM destination
            WHERE name LIKE CONCAT('%', #{keyword}, '%')
            ORDER BY id DESC
            """)
    List<Destination> selectByNameKeyword(@Param("keyword") String keyword);

    @Update("""
            <script>
            UPDATE destination
            <set>
                <if test="name != null">name = #{name},</if>
                <if test="regionCode != null">region_code = #{regionCode},</if>
                <if test="address != null">address = #{address},</if>
                <if test="summary != null">summary = #{summary},</if>
                <if test="description != null">description = #{description},</if>
                <if test="coverImageUrl != null">cover_image_url = #{coverImageUrl},</if>
                <if test="status != null">status = #{status},</if>
            </set>
            WHERE id = #{id}
            </script>
            """)
    int updateById(Destination destination);

    @Delete("""
            DELETE FROM destination
            WHERE id = #{id}
            """)
    int deleteById(@Param("id") Long id);
}
