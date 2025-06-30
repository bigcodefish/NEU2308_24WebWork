package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Defect;
import com.example.webcarproject.mapper.DefectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefectControllerTest {

    private DefectMapper defectMapper;
    private DefectController defectController;

    @BeforeEach
    void setUp() {
        defectMapper = mock(DefectMapper.class);
        defectController = new DefectController(defectMapper);
    }

    // 查询
    @Test
    void getByCondition() {
        List<Defect> mockList = new ArrayList<>();
        mockList.add(new Defect());
        when(defectMapper.getByCondition(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockList);

        List<Defect> result = defectController.getByCondition(
                "任务1", "裂缝", true, "待确认", "高",
                new Date(), new Date(), 100.0, 200.0,
                "张工", "AI识别", "李工", "杭州");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // 添加
    @Test
    void add() {
        Defect defect = new Defect();
        when(defectMapper.insert(defect)).thenReturn(1);

        String result = defectController.add(defect);
        assertEquals("添加成功", result);
    }

    @Test
    void addFailure() {
        Defect defect = new Defect();
        when(defectMapper.insert(defect)).thenReturn(0);

        String result = defectController.add(defect);
        assertEquals("添加失败", result);
    }

    // 添加备注
    @Test
    void addRemark() {
        when(defectMapper.updateRemark(1L, "需要跟进")).thenReturn(1);

        String result = defectController.addRemark(1L, "需要跟进");
        assertEquals("备注添加成功", result);
    }

    @Test
    void addRemarkFailure() {
        when(defectMapper.updateRemark(1L, "需要跟进")).thenReturn(0);

        String result = defectController.addRemark(1L, "需要跟进");
        assertEquals("备注添加失败", result);
    }

    // 添加确认
    @Test
    void addConfirmation() {
        when(defectMapper.updateConfirmation(eq(1L), eq("张工"), any(Date.class))).thenReturn(1);

        String result = defectController.addConfirmation(1L, "张工");
        assertEquals("确认信息更新成功", result);
    }

    @Test
    void addConfirmationFailure() {
        when(defectMapper.updateConfirmation(eq(1L), eq("张工"), any(Date.class))).thenReturn(0);

        String result = defectController.addConfirmation(1L, "张工");
        assertEquals("更新失败", result);
    }


    // 开始处理
    @Test
    void startProcessing() {
        Map<String, String> req = new HashMap<>();
        req.put("processor", "李工");

        when(defectMapper.updateProcessStartTime(eq(1L), any(Date.class), eq("李工")))
                .thenReturn(1);

        String result = defectController.startProcessing(1L, req);
        assertEquals("处理开始时间更新成功", result);
    }

    @Test
    void startProcessingFailure() {
        Map<String, String> req = new HashMap<>();
        req.put("processor", "李工");

        when(defectMapper.updateProcessStartTime(eq(1L), any(Date.class), eq("李工")))
                .thenReturn(0);

        String result = defectController.startProcessing(1L, req);
        assertEquals("更新失败", result);
    }

    // 完成处理
    @Test
    void completeProcessing() {
        Map<String, String> req = new HashMap<>();
        req.put("processResult", "完成修复");

        when(defectMapper.updateProcessEndTime(eq(1L), any(Date.class), eq("完成修复")))
                .thenReturn(1);

        String result = defectController.completeProcessing(1L, req);
        assertEquals("处理完成时间更新成功", result);
    }

    @Test
    void completeProcessingFailure() {
        Map<String, String> req = new HashMap<>();
        req.put("processResult", "完成修复");

        when(defectMapper.updateProcessEndTime(eq(1L), any(Date.class), eq("完成修复")))
                .thenReturn(0);

        String result = defectController.completeProcessing(1L, req);
        assertEquals("更新失败", result);
    }

    // 更新实际状态
    @Test
    void updateRealStatus() {
        when(defectMapper.updateRealStatus(1L, true)).thenReturn(1);

        String result = defectController.updateRealStatus(1L, true);
        assertEquals("更新成功", result);
    }

    @Test
    void updateRealStatusFailure() {
        when(defectMapper.updateRealStatus(1L, true)).thenReturn(0);

        String result = defectController.updateRealStatus(1L, true);
        assertEquals("更新失败", result);
    }

    // 更新状态
    @Test
    void updateStatus() {
        when(defectMapper.updateStatus(1L, "已完成")).thenReturn(1);

        String result = defectController.updateStatus(1L, "已完成");
        assertEquals("更新成功", result);
    }

    @Test
    void updateStatusFailure() {
        when(defectMapper.updateStatus(1L, "已完成")).thenReturn(0);

        String result = defectController.updateStatus(1L, "已完成");
        assertEquals("更新失败", result);
    }

    // 批量更新实际状态
    @Test
    void batchUpdateRealStatus() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(defectMapper.batchUpdateRealStatus(ids, true)).thenReturn(2);

        String result = defectController.batchUpdateRealStatus(ids, true);
        assertEquals("批量更新成功", result);
    }

    @Test
    void batchUpdateRealStatusFailure() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(defectMapper.batchUpdateRealStatus(ids, true)).thenReturn(0);

        String result = defectController.batchUpdateRealStatus(ids, true);
        assertEquals("批量更新失败", result);
    }

    // 批量更新状态
    @Test
    void batchUpdateStatus() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(defectMapper.batchUpdateStatus(ids, "已完成")).thenReturn(2);

        String result = defectController.batchUpdateStatus(ids, "已完成");
        assertEquals("批量更新成功", result);
    }

    @Test
    void batchUpdateStatusFailure() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(defectMapper.batchUpdateStatus(ids, "已完成")).thenReturn(0);

        String result = defectController.batchUpdateStatus(ids, "已完成");
        assertEquals("批量更新失败", result);
    }

    // 删除
    @Test
    void delete() {
        when(defectMapper.deleteById(1L)).thenReturn(1);

        String result = defectController.delete(1L);
        assertEquals("删除成功", result);
    }

    @Test
    void deleteFailure() {
        when(defectMapper.deleteById(1L)).thenReturn(0);

        String result = defectController.delete(1L);
        assertEquals("删除失败", result);
    }
}
