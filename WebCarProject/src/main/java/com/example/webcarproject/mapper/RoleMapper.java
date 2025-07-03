package com.example.webcarproject.mapper;

import com.example.webcarproject.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("<script>" +
            "SELECT * FROM role_info " +
            "<where>" +
            "   <if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%') </if>" +
            "   <if test='code != null and code != \"\"'> AND code LIKE CONCAT('%', #{code}, '%') </if>" +
            "   <if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "   <if test='startTime != null'> AND create_time &gt;= #{startTime} </if>" +
            "   <if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            "</where>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<Role> selectByCondition(@Param("name") String name,
                                 @Param("code") String code,
                                 @Param("status") String status,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime);

    @Select("SELECT * FROM role_info WHERE id = #{id}")
    Role selectById(Long id);

    @Insert("INSERT INTO role_info(name, code, description, data_scope, status, create_time) " +
            "VALUES(#{name}, #{code}, #{description}, #{dataScope}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);

    @Update("UPDATE role_info SET " +
            "name = #{name}, " +
            "code = #{code}, " +
            "description = #{description}, " +
            "data_scope = #{dataScope}, " +
            "status = #{status}, " +
            "update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int update(Role role);

    @Update("UPDATE role_info SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM role_info WHERE id = #{id}")
    int deleteById(Long id);

    @Insert("<script>" +
            "INSERT INTO role_menu_relation(role_id, menu_id) VALUES " +
            "<foreach collection='menuIds' item='menuId' separator=','>" +
            "   (#{roleId}, #{menuId})" +
            "</foreach>" +
            "</script>")
    int insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    @Delete("DELETE FROM role_menu_relation WHERE role_id = #{roleId}")
    int deleteRoleMenus(Long roleId);

    @Select("SELECT menu_id FROM role_menu_relation WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);

    @Insert("<script>" +
            "INSERT INTO user_role_relation(user_id, role_id) VALUES " +
            "<foreach collection='userIds' item='userId' separator=','>" +
            "   (#{userId}, #{roleId})" +
            "</foreach>" +
            "</script>")
    int insertRoleUsers(@Param("roleId") Long roleId, @Param("userIds") List<Long> userIds);

    @Delete("DELETE FROM user_role_relation WHERE role_id = #{roleId}")
    int deleteRoleUsers(Long roleId);

    @Select("SELECT user_id FROM user_role_relation WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(Long roleId);

    @Select("SELECT COUNT(*) FROM user_role_relation WHERE role_id = #{roleId}")
    int countUsersByRoleId(Long roleId);

    @Delete("<script>" +
            "DELETE FROM role_info WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(@Param("ids") List<Long> ids);
}