package com.example.webcarproject.entity;

import lombok.Data;

import java.util.Map;

@Data
public class DefectStats {
    private int totalDefects;
    private int confirmedDefects;
    private int falseDefects;
    private int highSeverity;
    private int mediumSeverity;
    private int lowSeverity;
    private Map<String, Integer> typeDistribution;
    private Map<String, Integer> discovererDistribution;
}