package com.example.webcarproject.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Role {
    private Long id;                // 角色ID
    private String name;            // 角色名称
    private String code;            // 角色编码
    private String description;     // 角色描述
    private List<Long> menuIds;     // 菜单权限ID列表
    private String dataScope;       // 数据权限范围: all, dept, self
    private String status;          // 状态：0-启用，1-禁用
    private Date createTime;        // 创建时间
    private Date updateTime;        // 更新时间
}