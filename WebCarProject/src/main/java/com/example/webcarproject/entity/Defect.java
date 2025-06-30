package com.example.webcarproject.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Defect {
    private Long id;
    private String defectNo; // 缺陷编号
    private String taskName;
    private String defectType;
    private Double distance; // 缺陷距离具体位置的长度（米）
    private String location; // 具体位置（新增）
    private String imageUrl;
    private Boolean isReal;
    private String severity; // 严重程度
    private Double defectLength;
    private Double defectArea;
    private Integer defectCount;
    private String suggestion;
    private Date reportTime; // 上报时间
    private String status; // 状态

    // 新增字段
    private String discoverer; // 发现人员
    private String discoveryMethod; // 发现方式
    private String processor; // 处理人员
    private Date processStartTime; // 处理开始时间
    private Date processEndTime; // 处理完成时间
    private String processResult; // 处理结果

    // 新添加的字段
    private String description; // 缺陷描述
    private Date confirmTime;   // 确认时间
    private String confirmer;   // 确认负责人
    private String remark;      // 处理过程备注
}