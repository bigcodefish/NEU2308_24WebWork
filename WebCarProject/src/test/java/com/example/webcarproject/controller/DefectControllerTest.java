package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Defect;
import com.example.webcarproject.entity.DefectStats;
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



    @Test
    void getDefectStats_Success() {
        DefectStats mockStats = new DefectStats();
        mockStats.setTotalDefects(10);
        mockStats.setConfirmedDefects(7);
        mockStats.setFalseDefects(3);

        when(defectMapper.getDefectStats()).thenReturn(mockStats);

        DefectStats result = defectController.getDefectStats();

        assertNotNull(result);
        assertEquals(10, result.getTotalDefects());
        assertEquals(7, result.getConfirmedDefects());
        assertEquals(3, result.getFalseDefects());
        verify(defectMapper, times(1)).getDefectStats();
    }

    @Test
    void getDefectStats_Empty() {
        when(defectMapper.getDefectStats()).thenReturn(null);

        DefectStats result = defectController.getDefectStats();

        assertNull(result);
        verify(defectMapper, times(1)).getDefectStats();
    }

    @Test
    void getDefectTypeStats_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> type1 = new HashMap<>();
        type1.put("defectType", "裂缝");
        type1.put("count", 5);
        mockList.add(type1);

        when(defectMapper.getDefectTypeStats()).thenReturn(mockList);

        List<Map<String, Object>> result = defectController.getDefectTypeStats();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("裂缝", result.get(0).get("defectType"));
        assertEquals(5, result.get(0).get("count"));
        verify(defectMapper, times(1)).getDefectTypeStats();
    }

    @Test
    void getDefectTypeStats_Empty() {
        when(defectMapper.getDefectTypeStats()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = defectController.getDefectTypeStats();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(defectMapper, times(1)).getDefectTypeStats();
    }

    @Test
    void getDefectTypeDetails_Success() {
        Map<String, Object> mockDetails = new HashMap<>();
        mockDetails.put("total", 10);
        mockDetails.put("confirmed", 7);
        mockDetails.put("falseDefects", 3);
        mockDetails.put("highSeverity", 2);
        mockDetails.put("mediumSeverity", 3);
        mockDetails.put("lowSeverity", 2);

        when(defectMapper.getDefectTypeDetails("裂缝")).thenReturn(mockDetails);

        Map<String, Object> result = defectController.getDefectTypeDetails("裂缝");

        assertNotNull(result);
        assertEquals(10, result.get("total"));
        assertEquals(7, result.get("confirmed"));
        assertEquals(3, result.get("falseDefects"));
        verify(defectMapper, times(1)).getDefectTypeDetails("裂缝");
    }

    @Test
    void getDefectTypeDetails_NotFound() {
        when(defectMapper.getDefectTypeDetails("不存在的类型")).thenReturn(null);

        Map<String, Object> result = defectController.getDefectTypeDetails("不存在的类型");

        assertNull(result);
        verify(defectMapper, times(1)).getDefectTypeDetails("不存在的类型");
    }

    @Test
    void getMonthlyDefectStats_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> month1 = new HashMap<>();
        month1.put("month", "2023-01");
        month1.put("count", 10);
        mockList.add(month1);

        when(defectMapper.getMonthlyStats()).thenReturn(mockList);

        List<Map<String, Object>> result = defectController.getMonthlyDefectStats();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2023-01", result.get(0).get("month"));
        assertEquals(10, result.get(0).get("count"));
        verify(defectMapper, times(1)).getMonthlyStats();
    }

    @Test
    void getMonthlyDefectStats_Empty() {
        when(defectMapper.getMonthlyStats()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = defectController.getMonthlyDefectStats();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(defectMapper, times(1)).getMonthlyStats();
    }

    @Test
    void getMonthlyDefectDetails_Success() {
        // 准备模拟数据
        List<Defect> mockDefects = Arrays.asList(new Defect(), new Defect());
        DefectStats mockStats = new DefectStats();
        mockStats.setTotalDefects(2);
        mockStats.setConfirmedDefects(1);
        mockStats.setFalseDefects(1);

        when(defectMapper.getMonthlyDefectDetails("2023-01", 1000)).thenReturn(mockDefects);
        when(defectMapper.getMonthlyDefectStats("2023-01")).thenReturn(mockStats);

        Map<String, Object> result = defectController.getMonthlyDefectDetails("2023-01", 1000);

        assertNotNull(result);
        assertEquals(2, ((List<?>)result.get("defects")).size());
        assertEquals(2, ((DefectStats)result.get("stats")).getTotalDefects());
        verify(defectMapper, times(1)).getMonthlyDefectDetails("2023-01", 1000);
        verify(defectMapper, times(1)).getMonthlyDefectStats("2023-01");
    }

    @Test
    void getMonthlyDefectDetails_Empty() {
        when(defectMapper.getMonthlyDefectDetails("2023-02", 1000)).thenReturn(Collections.emptyList());
        when(defectMapper.getMonthlyDefectStats("2023-02")).thenReturn(new DefectStats());

        Map<String, Object> result = defectController.getMonthlyDefectDetails("2023-02", 1000);

        assertNotNull(result);
        assertTrue(((List<?>)result.get("defects")).isEmpty());
        verify(defectMapper, times(1)).getMonthlyDefectDetails("2023-02", 1000);
        verify(defectMapper, times(1)).getMonthlyDefectStats("2023-02");
    }

    @Test
    void getMonthlyDefectDetails_InvalidPageSize() {
        // 测试边界条件 - 0或负数的pageSize
        List<Defect> mockDefects = Collections.singletonList(new Defect());
        when(defectMapper.getMonthlyDefectDetails("2023-01", 0)).thenReturn(mockDefects);
        when(defectMapper.getMonthlyDefectStats("2023-01")).thenReturn(new DefectStats());

        Map<String, Object> result = defectController.getMonthlyDefectDetails("2023-01", 0);

        assertNotNull(result);
        assertEquals(1, ((List<?>)result.get("defects")).size());
        verify(defectMapper, times(1)).getMonthlyDefectDetails("2023-01", 0);
    }
}
