package com.example.webcarproject.mapper;

import com.example.webcarproject.entity.Menu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuMapper {
    @Select({
            "<script>",
            "SELECT",
            "   menuId, parentId, menuName, path, component, isFrame,",
            "   menuType, visible, status, perms, icon, createBy, createTime, orderNum, remark",
            "FROM sys_menu",
            "<where>",
            "   <if test='menuName != null and menuName != \"\"'>",
            "       AND menuName LIKE CONCAT('%', #{menuName}, '%')",
            "   </if>",
            "   <if test='status != null and status != \"\"'>",
            "       AND status = #{status}",
            "   </if>",
            "   <if test='visible != null and visible != \"\"'>",
            "       AND visible = #{visible}",
            "   </if>",
            "</where>",
            "ORDER BY parentId, orderNum",
            "</script>"
    })
    List<Menu> selectAllWithParams(@Param("menuName") String menuName,
                                   @Param("status") String status,
                                   @Param("visible") String visible);

    @Select("SELECT * FROM sys_menu WHERE menuId = #{menuId}")
    Menu selectMenuById(Long menuId);

    @Select("SELECT COUNT(*) FROM sys_menu WHERE menuName = #{menuName} AND parentId = #{parentId}")
    int checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    @Select("SELECT COUNT(*) FROM sys_menu WHERE parentId = #{menuId}")
    int hasChildByMenuId(Long menuId);

    @Insert("INSERT INTO sys_menu(menuName, parentId, orderNum, path, component, isFrame, " +
            "menuType, visible, status, perms, icon, createBy, createTime, remark) " +
            "VALUES(#{menuName}, #{parentId}, #{orderNum}, #{path}, #{component}, #{isFrame}, " +
            "#{menuType}, #{visible}, #{status}, #{perms}, #{icon}, #{createBy}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "menuId")
    int insertMenu(Menu menu);

    @Update("UPDATE sys_menu SET " +
            "menuName = #{menuName}, " +
            "parentId = #{parentId}, " +
            "orderNum = #{orderNum}, " +
            "path = #{path}, " +
            "component = #{component}, " +
            "isFrame = #{isFrame}, " +
            "menuType = #{menuType}, " +
            "visible = #{visible}, " +
            "status = #{status}, " +
            "perms = #{perms}, " +
            "icon = #{icon}, " +
            "updateBy = #{updateBy}, " +
            "updateTime = NOW(), " +
            "remark = #{remark} " +
            "WHERE menuId = #{menuId}")
    int updateMenu(Menu menu);

    @Update({
            "<script>",
            "UPDATE sys_menu",
            "<set>",
            "   <if test='status != null'>status = #{status},</if>",
            "   <if test='visible != null'>visible = #{visible},</if>",
            "   updateTime = NOW()",
            "</set>",
            "WHERE menuId IN",
            "<foreach collection='menuIds' item='menuId' open='(' separator=',' close=')'>",
            "   #{menuId}",
            "</foreach>",
            "</script>"
    })
    int batchUpdateMenu(@Param("menuIds") List<Long> menuIds,
                        @Param("status") String status,
                        @Param("visible") String visible);

    @Delete("DELETE FROM sys_menu WHERE menuId = #{menuId}")
    int deleteMenuById(Long menuId);

    @Delete({
            "<script>",
            "DELETE FROM sys_menu WHERE menuId IN",
            "<foreach collection='menuIds' item='menuId' open='(' separator=',' close=')'>",
            "   #{menuId}",
            "</foreach>",
            "</script>"
    })
    int batchDeleteMenu(@Param("menuIds") List<Long> menuIds);

    @Select("SELECT * FROM sys_menu WHERE parentId = #{parentId} ORDER BY orderNum")
    List<Menu> selectByParentId(Long parentId);

    @Select("SELECT COUNT(*) FROM sys_menu WHERE parentId = #{parentId} AND menuId != #{excludeMenuId} AND menuName = #{menuName}")
    int checkMenuNameUniqueExcludeSelf(@Param("menuName") String menuName,
                                       @Param("parentId") Long parentId,
                                       @Param("excludeMenuId") Long excludeMenuId);
}