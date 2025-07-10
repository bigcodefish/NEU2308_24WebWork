package com.example.webcarproject.mapper;
import com.example.webcarproject.entity.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapper {
    // 查询所有任务
    @Select("SELECT " +
            "id, " +
            "task_id AS taskId, " +
            "task_name AS taskName, " +
            "task_type AS taskType, " +
            "priority, " +
            "description, " +
            "executor, " +
            "assistants, " +
            "planned_start_time AS plannedStartTime, " +
            "planned_end_time AS plannedEndTime, " +
            "actual_start_time AS actualStartTime, " +
            "actual_end_time AS actualEndTime, " +
            "inspection_line AS inspectionLine, " +
            "start_location AS startLocation, " +
            "end_location AS endLocation, " +
            "inspection_scope AS inspectionScope, " +
            "inspection_distance AS inspectionDistance, " +
            "status, " +
            "completion_rate AS completionRate, " +
            "execution_result AS executionResult, " +
            "issues_found AS issuesFound " +
            "FROM task")
    List<Task> findAllTasks();

    // 根据ID查询任务
    @Select("SELECT * FROM task WHERE id = #{id}")
    Task findById(Long id);

    // 新增任务
    @Insert("INSERT INTO task (" +
            "task_id, task_name, task_type, priority, description, " +
            "executor, assistants, planned_start_time, planned_end_time, " +
            "actual_start_time, actual_end_time, inspection_line, " +
            "start_location, end_location, inspection_scope, inspection_distance, status, " +
            "completion_rate, execution_result, issues_found" +
            ") VALUES (" +
            "#{taskId}, #{taskName}, #{taskType}, #{priority}, #{description}, " +
            "#{executor}, #{assistants}, #{plannedStartTime}, #{plannedEndTime}, " +
            "#{actualStartTime}, #{actualEndTime}, #{inspectionLine}, " +
            "#{startLocation}, #{endLocation}, #{inspectionScope}, #{inspectionDistance}, #{status}, " +
            "#{completionRate}, #{executionResult}, #{issuesFound}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);

    // 更新任务
    @Update("UPDATE task SET " +
            "task_id = #{taskId}, " +
            "task_name = #{taskName}, " +
            "task_type = #{taskType}, " +
            "priority = #{priority}, " +
            "description = #{description}, " +
            "executor = #{executor}, " +
            "assistants = #{assistants}, " +
            "planned_start_time = #{plannedStartTime}, " +
            "planned_end_time = #{plannedEndTime}, " +
            "actual_start_time = #{actualStartTime}, " +
            "actual_end_time = #{actualEndTime}, " +
            "inspection_line = #{inspectionLine}, " +
            "start_location = #{startLocation}, " +
            "end_location = #{endLocation}, " +
            "inspection_scope = #{inspectionScope}, " +
            "inspection_distance = #{inspectionDistance}, " +
            "status = #{status}, " +
            "completion_rate = #{completionRate}, " +
            "execution_result = #{executionResult}, " +
            "issues_found = #{issuesFound} " +
            "WHERE id = #{id}")
    int update(Task task);

    // 删除任务
    @Delete("DELETE FROM task WHERE id = #{id}")
    int delete(Long id);

    // 搜索任务
    @Select("<script>" +
            "SELECT id, " +
            "task_id AS taskId, " +
            "task_name AS taskName, " +
            "task_type AS taskType, " +
            "priority, " +
            "description, " +
            "executor, " +
            "assistants, " +
            "planned_start_time AS plannedStartTime, " +
            "planned_end_time AS plannedEndTime, " +
            "actual_start_time AS actualStartTime, " +
            "actual_end_time AS actualEndTime, " +
            "inspection_line AS inspectionLine, " +
            "start_location AS startLocation, " +
            "end_location AS endLocation, " +
            "inspection_scope AS inspectionScope, " +
            "inspection_distance AS inspectionDistance, " +
            "status, " +
            "completion_rate AS completionRate, " +
            "execution_result AS executionResult, " +
            "issues_found AS issuesFound " +
            "FROM task WHERE 1=1" +
            "<if test='taskId != null and taskId != \"\"'> AND task_id LIKE CONCAT('%', #{taskId}, '%')</if>" +
            "<if test='taskName != null and taskName != \"\"'> AND task_name LIKE CONCAT('%', #{taskName}, '%')</if>" +
            "<if test='creator != null and creator != \"\"'> AND creator LIKE CONCAT('%', #{creator}, '%')</if>" +
            "<if test='executor != null and executor != \"\"'> AND executor LIKE CONCAT('%', #{executor}, '%')</if>" +
            "</script>")
    List<Task> searchTasks(@Param("taskId") String taskId,
                           @Param("taskName") String taskName,
                           @Param("creator") String creator,
                           @Param("executor") String executor);




    // 获取指定日期的巡检数量
    @Select("SELECT COUNT(*) FROM task WHERE planned_start_time::date = #{date}")
    int getTaskCountByDate(String date);

    // 获取指定日期的巡视距离
    @Select("SELECT SUM(inspection_distance) FROM task WHERE planned_start_time::date = #{date}")
    double getTotalDistanceByDate(String date);

    // 获取每月的巡检次数
    @Select("SELECT TO_CHAR(planned_start_time, 'YYYY-MM') AS month, COUNT(*) AS count " +
            "FROM task " +
            "GROUP BY TO_CHAR(planned_start_time, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(planned_start_time, 'YYYY-MM')")
    List<Map<String, Object>> getMonthlyTaskCount();

    // 获取人员的任务统计信息
    @Select("SELECT executor, COUNT(*) AS taskCount FROM task GROUP BY executor")
    List<Map<String, Object>> getPersonTaskStats();

    // 获取指定月份的巡检详细信息
    @Select("SELECT * FROM task WHERE TO_CHAR(planned_start_time, 'YYYY-MM') = #{month}")
    List<Task> getMonthlyTaskDetail(String month);

    // 获取指定人员的任务详细信息
    @Select("SELECT * FROM task WHERE executor = #{executor}")
    List<Task> getPersonTaskDetail(@Param("executor") String executor);

    // 获取巡检中有问题的任务
    @Select("SELECT * FROM task WHERE issues_found > 0")
    List<Task> getInspectionsWithDefects();
}