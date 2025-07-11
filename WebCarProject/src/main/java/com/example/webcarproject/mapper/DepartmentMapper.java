package com.example.webcarproject.mapper;

import com.example.webcarproject.entity.Department;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface DepartmentMapper {

    @Select("<script>" +
            "SELECT d.id, d.name, d.code, d.parent_id, p.name as parent_name, " +
            "d.leader, d.phone, d.email, d.order_num, d.status, d.create_time " + // 移除了update_time
            "FROM department_info d " +
            "LEFT JOIN department_info p ON d.parent_id = p.id " +
            "<where>" +
            "   <if test='name != null'> AND d.name LIKE CONCAT('%', #{name}, '%') </if>" +
            "   <if test='code != null'> AND d.code LIKE CONCAT('%', #{code}, '%') </if>" +
            "   <if test='status != null'> AND d.status = #{status} </if>" +
            "   <if test='startTime != null'> AND d.create_time &gt;= #{startTime} </if>" +
            "   <if test='endTime != null'> AND d.create_time &lt;= #{endTime} </if>" +
            "</where>" +
            " ORDER BY d.order_num ASC" +
            "</script>")
    List<Department> selectByCondition(@Param("name") String name,
                                       @Param("code") String code,
                                       @Param("status") String status,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);

    @Select("SELECT id, name, code, parent_id, leader, phone, email, " +
            "order_num, status, create_time " + // 移除了update_time
            "FROM department_info WHERE id = #{id}")
    Department selectById(Long id);

    @Select("SELECT id, name, code, parent_id, leader, phone, email, " +
            "order_num, status, create_time " + // 移除了update_time
            "FROM department_info WHERE parent_id = #{parentId} ORDER BY order_num ASC")
    List<Department> selectChildren(Long parentId);

    @Select("SELECT id, name, code, parent_id, leader, phone, email, " +
            "order_num, status, create_time " + // 移除了update_time
            "FROM department_info WHERE parent_id IS NULL ORDER BY order_num ASC")
    List<Department> selectTree();

    @Insert("INSERT INTO department_info(name, code, parent_id, leader, phone, email, " +
            "order_num, status, create_time) " + // 移除了update_time
            "VALUES(#{name}, #{code}, #{parentId}, #{leader}, #{phone}, #{email}, " +
            "#{orderNum}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Department department);

    @Update("UPDATE department_info SET " +
            "name = #{name}, " +
            "code = #{code}, " +
            "parent_id = #{parentId}, " +
            "leader = #{leader}, " +
            "phone = #{phone}, " +
            "email = #{email}, " +
            "order_num = #{orderNum}, " +
            "status = #{status} " +
            // 移除了update_time的更新
            "WHERE id = #{id}")
    int update(Department department);

    @Update("UPDATE department_info SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM department_info WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM user_info WHERE department_id = #{deptId}")
    int countUsersByDeptId(Long deptId);

    @Select("SELECT COUNT(*) FROM department_info WHERE parent_id = #{deptId}")
    int countChildren(Long deptId);

    @Select("SELECT id FROM user_info WHERE department_id = #{deptId}")
    List<Long> selectUserIdsByDeptId(Long deptId);

    @Select("SELECT id, name, code, parent_id, leader, phone, email, order_num, status, create_time FROM department_info")
    List<Department> selectAllDepts();

    // 查询部门及其所有子部门ID
    @Select("WITH RECURSIVE dept_tree AS (" +
            "  SELECT id FROM department_info WHERE id = #{deptId} " +
            "  UNION ALL " +
            "  SELECT d.id FROM department_info d " +
            "  JOIN dept_tree t ON d.parent_id = t.id " +
            ") SELECT id FROM dept_tree")
    List<Long> selectDeptAndChildrenIds(@Param("deptId") Long deptId);

    // 批量删除部门
    @Delete("<script>" +
            "DELETE FROM department_info WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDeleteDepts(@Param("ids") List<Long> ids);
}