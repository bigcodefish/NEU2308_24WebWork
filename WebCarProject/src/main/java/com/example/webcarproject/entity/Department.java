package com.example.webcarproject.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Department {
    private Long id;                // 部门ID
    private String name;            // 部门名称
    private String code;            // 部门编码
    private Long parentId;          // 父部门ID
    private String parentName;      // 父部门名称
    private String leader;          // 部门负责人
    private String phone;           // 联系电话
    private String email;           // 邮箱地址
    private Integer orderNum;       // 显示顺序
    private String status;          // 状态：0-正常，1-停用
    private Date createTime;        // 创建时间
    // 移除了updateTime字段
    private List<Department> children; // 子部门列表
}