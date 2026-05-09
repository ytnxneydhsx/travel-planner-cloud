package org.example.itineraryservice.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.itineraryservice.entity.ItineraryDestination;

@Mapper
public interface ItineraryDestinationMapper {

    @Insert("""
            INSERT INTO itinerary_destination (
                itinerary_id,
                destination_id,
                sort_order
            ) VALUES (
                #{itineraryId},
                #{destinationId},
                #{sortOrder}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ItineraryDestination itineraryDestination);

    @Select("""
            SELECT
                id,
                itinerary_id,
                destination_id,
                sort_order,
                gmt_create
            FROM itinerary_destination
            WHERE itinerary_id = #{itineraryId}
            ORDER BY sort_order ASC, id ASC
            """)
    List<ItineraryDestination> selectByItineraryId(@Param("itineraryId") Long itineraryId);

    @Select("""
            SELECT
                id,
                itinerary_id,
                destination_id,
                sort_order,
                gmt_create
            FROM itinerary_destination
            WHERE itinerary_id = #{itineraryId}
              AND destination_id = #{destinationId}
            """)
    ItineraryDestination selectByItineraryIdAndDestinationId(
            @Param("itineraryId") Long itineraryId,
            @Param("destinationId") Long destinationId);

    @Select("""
            SELECT COALESCE(MAX(sort_order), 0)
            FROM itinerary_destination
            WHERE itinerary_id = #{itineraryId}
            """)
    Integer selectMaxSortOrderByItineraryId(@Param("itineraryId") Long itineraryId);

    @Delete("""
            DELETE FROM itinerary_destination
            WHERE itinerary_id = #{itineraryId}
            """)
    int deleteByItineraryId(@Param("itineraryId") Long itineraryId);

    @Delete("""
            DELETE FROM itinerary_destination
            WHERE itinerary_id = #{itineraryId}
              AND destination_id = #{destinationId}
            """)
    int deleteByItineraryIdAndDestinationId(
            @Param("itineraryId") Long itineraryId,
            @Param("destinationId") Long destinationId);
}
