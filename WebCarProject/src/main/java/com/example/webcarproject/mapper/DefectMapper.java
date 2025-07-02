package com.example.webcarproject.mapper;


import com.example.webcarproject.entity.DefectStats;
import org.apache.ibatis.annotations.*;
import com.example.webcarproject.entity.Defect;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface DefectMapper {

    @Select("<script>" +
            "SELECT * FROM defect_info" +
            "<where>" +
            "   <if test='taskName != null and taskName != \"\"'> AND task_name LIKE CONCAT('%', #{taskName}, '%') </if>" +
            "   <if test='defectType != null and defectType != \"\"'> AND defect_type LIKE CONCAT('%', #{defectType}, '%') </if>" +
            "   <if test='isReal != null'> AND is_real = #{isReal} </if>" +
            "   <if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "   <if test='severity != null and severity != \"\"'> AND severity = #{severity} </if>" +
            "   <if test='startTime != null'> AND report_time &gt;= #{startTime} </if>" +
            "   <if test='endTime != null'> AND report_time &lt;= #{endTime} </if>" +
            "   <if test='minDistance != null'> AND distance &gt;= #{minDistance} </if>" +
            "   <if test='maxDistance != null'> AND distance &lt;= #{maxDistance} </if>" +
            "   <if test='discoverer != null and discoverer != \"\"'> AND discoverer LIKE CONCAT('%', #{discoverer}, '%') </if>" +
            "   <if test='discoveryMethod != null and discoveryMethod != \"\"'> AND discovery_method LIKE CONCAT('%', #{discoveryMethod}, '%') </if>" +
            "   <if test='processor != null and processor != \"\"'> AND processor LIKE CONCAT('%', #{processor}, '%') </if>" +
            "   <if test='location != null and location != \"\"'> AND location LIKE CONCAT('%', #{location}, '%') </if>" + // 新增位置搜索
            "</where>" +
            " ORDER BY report_time DESC" +
            "</script>")
    List<Defect> getByCondition(@Param("taskName") String taskName,
                                @Param("defectType") String defectType,
                                @Param("isReal") Boolean isReal,
                                @Param("status") String status,
                                @Param("severity") String severity,
                                @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime,
                                @Param("minDistance") Double minDistance,
                                @Param("maxDistance") Double maxDistance,
                                @Param("discoverer") String discoverer,
                                @Param("discoveryMethod") String discoveryMethod,
                                @Param("processor") String processor,
                                @Param("location") String location); // 新增位置参数
    @Insert("INSERT INTO defect_info(defect_no, task_name, defect_type, distance, location, image_url, is_real, severity, " +
            "defect_length, defect_area, defect_count, suggestion, report_time, status, discoverer, discovery_method, " +
            "processor, process_start_time, process_end_time, process_result, description, confirmer, remark) " +
            "VALUES(#{defectNo}, #{taskName}, #{defectType}, #{distance}, #{location}, #{imageUrl}, #{isReal}, #{severity}, " +
            "#{defectLength}, #{defectArea}, #{defectCount}, #{suggestion}, #{reportTime}, #{status}, #{discoverer}, " +
            "#{discoveryMethod}, #{processor}, #{processStartTime}, #{processEndTime}, #{processResult}, #{description}, " +
            "#{confirmer}, #{remark})")
    int insert(Defect defect);

    @Update("UPDATE defect_info SET remark = #{remark} WHERE id = #{id}")
    int updateRemark(@Param("id") Long id, @Param("remark") String remark);

    // 添加确认信息更新方法
    @Update("UPDATE defect_info SET confirmer = #{confirmer}, confirm_time = #{confirmTime} WHERE id = #{id}")
    int updateConfirmation(@Param("id") Long id,
                           @Param("confirmer") String confirmer,
                           @Param("confirmTime") Date confirmTime);

    // 添加处理开始时间更新方法
    @Update("UPDATE defect_info SET process_start_time = #{startTime}, processor = #{processor} WHERE id = #{id}")
    int updateProcessStartTime(@Param("id") Long id,
                               @Param("startTime") Date startTime,
                               @Param("processor") String processor);


    // 添加处理完成时间更新方法
    @Update("UPDATE defect_info SET process_end_time = #{endTime}, process_result = #{processResult} WHERE id = #{id}")
    int updateProcessEndTime(@Param("id") Long id,
                             @Param("endTime") Date endTime,
                             @Param("processResult") String processResult);

    @Update("UPDATE defect_info SET is_real = #{isReal}, status = CASE WHEN #{isReal} = true THEN '已确认' ELSE status END WHERE id = #{id}")
    int updateRealStatus(@Param("id") Long id, @Param("isReal") Boolean isReal);

    @Update("UPDATE defect_info SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("<script>" +
            "UPDATE defect_info SET is_real = #{isReal}, status = CASE WHEN #{isReal} = true THEN '已确认' ELSE status END WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateRealStatus(@Param("ids") List<Long> ids, @Param("isReal") Boolean isReal);

    @Update("<script>" +
            "UPDATE defect_info SET status = #{status} WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    @Delete("DELETE FROM defect_info WHERE id = #{id}")
    int deleteById(@Param("id") Long id);




    @Select("SELECT " +
            "COUNT(*) AS totalDefects, " +
            "SUM(CASE WHEN is_real = true THEN 1 ELSE 0 END) AS confirmedDefects, " +
            "SUM(CASE WHEN is_real = false THEN 1 ELSE 0 END) AS falseDefects " +
            "FROM defect_info")
    DefectStats getDefectStats();

    @Select("SELECT defect_type AS defectType, COUNT(*) AS count FROM defect_info GROUP BY defect_type")
    List<Map<String, Object>> getDefectTypeStats();



    @Select("SELECT TO_CHAR(report_time, 'YYYY-MM') AS month, COUNT(*) AS count " +
            "FROM defect_info " +
            "WHERE report_time >= NOW() - INTERVAL '1 year' " +
            "GROUP BY TO_CHAR(report_time, 'YYYY-MM') " +
            "ORDER BY month")
    List<Map<String, Object>> getMonthlyDefectStats();

}
