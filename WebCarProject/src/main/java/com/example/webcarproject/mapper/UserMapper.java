package com.example.webcarproject.mapper;

import com.example.webcarproject.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user_info WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO user_info (username, password, salt) " +
            "VALUES(#{username}, #{password}, #{salt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertlogin(User auth);
    @Select("SELECT COUNT(*) FROM user_info  WHERE username = #{username}")
    int countByUsername(String username);
    @Select("SELECT id, username, password, name, email, phone, department_id as departmentId, " +
            "status, create_time as createTime " +
            "FROM user_info " +
            "ORDER BY id ASC")
    List<User> selectAll();

    @Select("<script>" +
            "SELECT u.*, d.name as department_name FROM user_info u " +
            "LEFT JOIN department_info d ON u.department_id = d.id " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND u.username LIKE CONCAT('%', #{username}, '%') </if>" +
            "   <if test='name != null and name != \"\"'> AND u.name LIKE CONCAT('%', #{name}, '%') </if>" +
            "   <if test='phone != null and phone != \"\"'> AND u.phone LIKE CONCAT('%', #{phone}, '%') </if>" +
            "   <if test='email != null and email != \"\"'> AND u.email LIKE CONCAT('%', #{email}, '%') </if>" +
            "   <if test='departmentId != null'> AND u.department_id = #{departmentId} </if>" +
            "   <if test='status != null and status != \"\"'> AND u.status = #{status} </if>" +
            "   <if test='startTime != null'> AND u.create_time &gt;= #{startTime} </if>" +
            "   <if test='endTime != null'> AND u.create_time &lt;= #{endTime} </if>" +
            "</where>" +
            " ORDER BY u.create_time DESC" +
            "<if test='page != null and size != null'> LIMIT #{size} OFFSET #{page} </if>" +
            "</script>")
    List<User> selectByCondition(@Param("username") String username,
                                 @Param("name") String name,
                                 @Param("phone") String phone,
                                 @Param("email") String email,
                                 @Param("departmentId") Long departmentId,
                                 @Param("status") String status,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime,
                                 @Param("page") Integer page,
                                 @Param("size") Integer size);

    @Select("SELECT * FROM user_info WHERE id = #{id}")
    User selectById(Long id);

    @Insert("INSERT INTO user_info(username, password, name, email, phone, department_id, status, create_time) " +
            "VALUES(#{username}, #{password}, #{name}, #{email}, #{phone}, #{departmentId}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id") // 明确指定主键列
    int insert(User user);

    @Update("UPDATE user_info SET " +
            "name = #{name}, " +
            "email = #{email}, " +
            "phone = #{phone}, " +
            "department_id = #{departmentId}, " +
            "status = #{status}, " +
            "update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int update(User user);

    @Update("UPDATE user_info SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE user_info SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM user_info WHERE id = #{id}")
    int deleteById(Long id);

    @Insert("<script>" +
            "INSERT INTO user_role_relation(user_id, role_id) VALUES " +
            "<foreach collection='roleIds' item='roleId' separator=','>" +
            "   (#{userId}, #{roleId})" +
            "</foreach>" +
            "</script>")
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    @Delete("DELETE FROM user_role_relation WHERE user_id = #{userId}")
    int deleteUserRoles(Long userId);

    @Select("SELECT role_id FROM user_role_relation WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(Long userId);

    @Delete("<script>" +
            "DELETE FROM user_info WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    int batchDeleteUsers(@Param("ids") List<Long> ids);

    @Select("<script>" +
            "SELECT * FROM user_info " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%') </if>" +
            "   <if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%') </if>" +
            "   <if test='phone != null and phone != \"\"'> AND phone LIKE CONCAT('%', #{phone}, '%') </if>" +
            "   <if test='email != null and email != \"\"'> AND email LIKE CONCAT('%', #{email}, '%') </if>" +
            "   <if test='departmentId != null'> AND department_id = #{departmentId} </if>" +
            "   <if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "   <if test='startTime != null'> AND create_time &gt;= #{startTime} </if>" +
            "   <if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            "</where>" +
            "</script>")

    List<User> selectByConditionForExport(
            @Param("username") String username,
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("status") String status,
            @Param("departmentId") Long departmentId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );

    @Select("<script>" +
            "SELECT COUNT(*) FROM user_info u " +
            "<where>" +
            "   <if test='username != null and username != \"\"'> AND u.username LIKE CONCAT('%', #{username}, '%') </if>" +
            "   <if test='name != null and name != \"\"'> AND u.name LIKE CONCAT('%', #{name}, '%') </if>" +
            "   <if test='phone != null and phone != \"\"'> AND u.phone LIKE CONCAT('%', #{phone}, '%') </if>" +
            "   <if test='email != null and email != \"\"'> AND u.email LIKE CONCAT('%', #{email}, '%') </if>" +
            "   <if test='departmentId != null'> AND u.department_id = #{departmentId} </if>" +
            "   <if test='status != null and status != \"\"'> AND u.status = #{status} </if>" +
            "   <if test='startTime != null'> AND u.create_time &gt;= #{startTime} </if>" +
            "   <if test='endTime != null'> AND u.create_time &lt;= #{endTime} </if>" +
            "</where>" +
            "</script>")
    int countByCondition(@Param("username") String username,
                         @Param("name") String name,
                         @Param("phone") String phone,
                         @Param("email") String email,
                         @Param("departmentId") Long departmentId,
                         @Param("status") String status,
                         @Param("startTime") Date startTime,
                         @Param("endTime") Date endTime);
}