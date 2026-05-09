package org.example.itineraryservice.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.itineraryservice.entity.Itinerary;

@Mapper
public interface ItineraryMapper {

    @Insert("""
            INSERT INTO itinerary (
                user_id,
                title,
                description
            ) VALUES (
                #{userId},
                #{title},
                #{description}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Itinerary itinerary);

    @Select("""
            SELECT
                id,
                user_id,
                title,
                description,
                gmt_create,
                gmt_modified
            FROM itinerary
            WHERE id = #{id}
            """)
    Itinerary selectById(@Param("id") Long id);

    @Select("""
            SELECT
                id,
                user_id,
                title,
                description,
                gmt_create,
                gmt_modified
            FROM itinerary
            WHERE user_id = #{userId}
            ORDER BY id DESC
            """)
    List<Itinerary> selectByUserId(@Param("userId") Long userId);

    @Update("""
            <script>
            UPDATE itinerary
            <set>
                <if test="title != null">title = #{title},</if>
                <if test="description != null">description = #{description},</if>
            </set>
            WHERE id = #{id}
            </script>
            """)
    int updateById(Itinerary itinerary);

    @Delete("""
            DELETE FROM itinerary
            WHERE id = #{id}
            """)
    int deleteById(@Param("id") Long id);
}
