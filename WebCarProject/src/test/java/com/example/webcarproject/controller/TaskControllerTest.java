package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Task;
import com.example.webcarproject.mapper.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TaskControllerTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTasks() {
        // 准备测试数据
        List<Task> mockTasks = Arrays.asList(
                new Task(),
                new Task()
        );
        when(taskMapper.findAllTasks()).thenReturn(mockTasks);

        // 调用方法
        List<Task> result = taskController.getAllTasks();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).findAllTasks();
    }

    @Test
    void getTaskById_Found() {
        // 准备测试数据
        Long taskId = 1L;
        Task mockTask = new Task();
        mockTask.setId(taskId);
        when(taskMapper.findById(taskId)).thenReturn(mockTask);

        // 调用方法
        ResponseEntity<Task> response = taskController.getTaskById(taskId);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
        verify(taskMapper, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_NotFound() {
        // 准备测试数据
        Long taskId = 999L;
        when(taskMapper.findById(taskId)).thenReturn(null);

        // 调用方法
        ResponseEntity<Task> response = taskController.getTaskById(taskId);

        // 验证结果
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskMapper, times(1)).findById(taskId);
    }

    @Test
    void searchTasks() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", "T001");
        params.put("taskName", "巡检");
        params.put("creator", "张工");
        params.put("assignee", "李工");

        List<Task> mockTasks = Arrays.asList(
                new Task(),
                new Task()
        );
        when(taskMapper.searchTasks(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockTasks);

        // 调用方法
        List<Task> result = taskController.searchTasks(params);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).searchTasks(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void createTask() {
        // 准备测试数据
        Task newTask = new Task();
        newTask.setTaskName("新任务");
        when(taskMapper.insert(any(Task.class))).thenReturn(1);

        // 调用方法
        ResponseEntity<Task> response = taskController.createTask(newTask);

        // 验证结果
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders().getLocation());
        verify(taskMapper, times(1)).insert(any(Task.class));
    }

    @Test
    void updateTask_Success() {
        // 准备测试数据
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTaskName("更新后的任务");

        when(taskMapper.findById(taskId)).thenReturn(existingTask);
        when(taskMapper.update(any(Task.class))).thenReturn(1);

        // 调用方法
        ResponseEntity<Task> response = taskController.updateTask(taskId, updatedTask);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
        verify(taskMapper, times(1)).findById(taskId);
        verify(taskMapper, times(1)).update(any(Task.class));
    }

    @Test
    void updateTask_NotFound() {
        // 准备测试数据
        Long taskId = 999L;
        Task updatedTask = new Task();
        updatedTask.setId(taskId);

        when(taskMapper.findById(taskId)).thenReturn(null);

        // 调用方法
        ResponseEntity<Task> response = taskController.updateTask(taskId, updatedTask);

        // 验证结果
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskMapper, times(1)).findById(taskId);
        verify(taskMapper, never()).update(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        // 准备测试数据
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);

        when(taskMapper.findById(taskId)).thenReturn(existingTask);
        when(taskMapper.delete(taskId)).thenReturn(1);

        // 调用方法
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // 验证结果
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskMapper, times(1)).findById(taskId);
        verify(taskMapper, times(1)).delete(taskId);
    }

    @Test
    void deleteTask_NotFound() {
        // 准备测试数据
        Long taskId = 999L;

        when(taskMapper.findById(taskId)).thenReturn(null);

        // 调用方法
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // 验证结果
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskMapper, times(1)).findById(taskId);
        verify(taskMapper, never()).delete(taskId);
    }

    @Test
    void deleteMultipleTasks() {
        // 准备测试数据
        List<Long> taskIds = Arrays.asList(1L, 2L, 3L);

        // 调用方法
        ResponseEntity<Void> response = taskController.deleteMultipleTasks(taskIds);

        // 验证结果
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskMapper, times(3)).delete(anyLong());
    }

    @Test
    void getTaskStats() {
        // 准备测试数据
        List<Task> mockTasks = Arrays.asList(
                createTaskWithDistance(10.5),
                createTaskWithDistance(15.0),
                createTaskWithDistance(8.5)
        );
        when(taskMapper.findAllTasks()).thenReturn(mockTasks);

        // 调用方法
        Map<String, Object> result = taskController.getTaskStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.get("totalTasks"));
        assertEquals(34.0, result.get("totalDistance"));
        verify(taskMapper, times(1)).findAllTasks();
    }

    @Test
    void getDailyTaskStats() {
        // 准备测试数据
        String today = LocalDateTime.now().toLocalDate().toString();
        String yesterday = LocalDateTime.now().minusDays(1).toLocalDate().toString();

        when(taskMapper.getTaskCountByDate(today)).thenReturn(5);
        when(taskMapper.getTaskCountByDate(yesterday)).thenReturn(3);
        when(taskMapper.getTotalDistanceByDate(today)).thenReturn(25.5);
        when(taskMapper.getTotalDistanceByDate(yesterday)).thenReturn(18.0);

        // 调用方法
        Map<String, Object> result = taskController.getDailyTaskStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(5, result.get("todayTaskCount"));
        assertEquals(3, result.get("yesterdayTaskCount"));
        assertEquals(25.5, result.get("todayDistance"));
        assertEquals(18.0, result.get("yesterdayDistance"));
    }

    @Test
    void getMonthlyTaskCount() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> month1 = new HashMap<>();
        month1.put("month", "2023-01");
        month1.put("count", 10);
        mockData.add(month1);

        Map<String, Object> month2 = new HashMap<>();
        month2.put("month", "2023-02");
        month2.put("count", 15);
        mockData.add(month2);

        when(taskMapper.getMonthlyTaskCount()).thenReturn(mockData);

        // 调用方法
        List<Map<String, Object>> result = taskController.getMonthlyTaskCount();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).getMonthlyTaskCount();
    }

    @Test
    void getPersonTaskStats() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> person1 = new HashMap<>();
        person1.put("executor", "张工");
        person1.put("taskCount", 5);
        mockData.add(person1);

        Map<String, Object> person2 = new HashMap<>();
        person2.put("executor", "李工");
        person2.put("taskCount", 3);
        mockData.add(person2);

        when(taskMapper.getPersonTaskStats()).thenReturn(mockData);

        // 调用方法
        List<Map<String, Object>> result = taskController.getPersonTaskStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).getPersonTaskStats();
    }

    @Test
    void getMonthlyTaskDetail() {
        // 准备测试数据
        String month = "2023-01";
        List<Task> mockTasks = Arrays.asList(
                new Task(),
                new Task()
        );
        when(taskMapper.getMonthlyTaskDetail(month)).thenReturn(mockTasks);

        // 调用方法
        List<Task> result = taskController.getMonthlyTaskDetail(month);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).getMonthlyTaskDetail(month);
    }

    @Test
    void getPersonTaskDetail() {
        // 准备测试数据
        String executor = "张工";
        List<Task> mockTasks = Arrays.asList(
                new Task(),
                new Task(),
                new Task()
        );
        when(taskMapper.getPersonTaskDetail(executor)).thenReturn(mockTasks);

        // 调用方法
        List<Task> result = taskController.getPersonTaskDetail(executor);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(taskMapper, times(1)).getPersonTaskDetail(executor);
    }

    @Test
    void getDefectDiscoveryStats() {
        // 准备测试数据
        List<Task> allTasks = Arrays.asList(
                new Task(), new Task(), new Task()
        );
        List<Task> defectTasks = Arrays.asList(
                new Task(), new Task()
        );

        when(taskMapper.findAllTasks()).thenReturn(allTasks);
        when(taskMapper.getInspectionsWithDefects()).thenReturn(defectTasks);

        // 调用方法
        Map<String, Object> result = taskController.getDefectDiscoveryStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.get("totalInspections"));
        assertEquals(2, result.get("inspectionsWithDefects"));
        verify(taskMapper, times(1)).findAllTasks();
        verify(taskMapper, times(1)).getInspectionsWithDefects();
    }

    private Task createTaskWithDistance(double distance) {
        Task task = new Task();
        task.setInspectionDistance(distance);
        return task;
    }
}