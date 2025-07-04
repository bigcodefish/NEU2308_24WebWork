package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Task;
import com.example.webcarproject.mapper.TaskMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskMapper taskMapper;

    public TaskController(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    // 1. 获取所有任务 - GET /api/tasks
    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskMapper.findAllTasks();
    }

    // 2. 获取单个任务 - GET /api/tasks/{id}
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskMapper.findById(id);
        return task != null ?
                ResponseEntity.ok(task) :
                ResponseEntity.notFound().build();
    }
    // 多条件查询任务
    @PostMapping("/tasks/search")
    public List<Task> searchTasks(@RequestBody Map<String, Object> params) {
        String taskId = (String) params.get("taskId");
        String taskName = (String) params.get("taskName");
        String creator = (String) params.get("creator");
        String executor = (String) params.get("assignee");
        return taskMapper.searchTasks(taskId, taskName, creator, executor);
    }

    // 3. 创建任务 - POST /api/tasks
    @PostMapping("/task")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        taskMapper.insert(task);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/tasks/" + task.getId())
                .body(task);
    }

    // 4. 更新任务 - PUT /api/tasks/{id}
    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        if (taskMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        task.setId(id); // 确保ID一致
        taskMapper.update(task);
        return ResponseEntity.ok(task);
    }

    // 5. 删除任务 - DELETE /api/tasks/{id}
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        taskMapper.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 6. 批量删除 - DELETE /api/tasks
    @DeleteMapping("/tasks")
    public ResponseEntity<Void> deleteMultipleTasks(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            taskMapper.delete(id);
        }
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/tasks/stats")
    public Map<String, Object> getTaskStats() {
        // 获取巡视总次数
        int totalTasks = taskMapper.findAllTasks().size();

        // 获取巡视总距离
        double totalDistance = taskMapper.findAllTasks().stream()
                .filter(task -> task.getInspectionDistance() != null)
                .mapToDouble(Task::getInspectionDistance)
                .sum();

        System.out.println("后端统计结果 - 总次数: " + totalTasks + ", 总距离: " + totalDistance);

        return Map.of(
                "totalTasks", totalTasks,
                "totalDistance", totalDistance
        );
    }
}
