package com.example.webcarproject.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class User {
    private Long id;                // 用户ID
    private String username;        // 用户名
    private String password;        // 密码
    private String name;            // 真实姓名
    private String email;           // 电子邮箱
    private String phone;           // 手机号
    private Long departmentId;      // 部门ID
    private String departmentName;  // 部门名称
    private List<Long> roleIds;     // 角色ID列表
    private List<String> roleNames; // 角色名称列表
    private String status;          // 状态：0-正常，1-禁用，2-锁定
    private Date lastLoginTime;     // 最后登录时间
    private Date createTime;        // 创建时间
    private Date updateTime;        // 更新时间
}