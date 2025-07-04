package com.example.webcarproject.controller;

import com.example.webcarproject.entity.User;
import com.example.webcarproject.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // 确保ID为null（由数据库生成）
            user.setId(null); // 明确设置为null

            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus("0");
            }
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());

            // 执行插入
            int result = userMapper.insert(user);
            if (result <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("数据库操作失败");
            }

            // 返回带生成ID的用户信息
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("创建用户失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userMapper.selectAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("获取用户失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // 1. 验证用户是否存在
            User existingUser = userMapper.selectById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. 设置更新ID
            user.setId(id);

            // 3. 执行更新
            int result = userMapper.update(user);
            if (result <= 0) {
                logger.warn("用户更新失败，ID: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("用户更新失败");
            }

            logger.info("成功更新用户，ID: {}", id);
            return ResponseEntity.ok(userMapper.selectById(id));

        } catch (Exception e) {
            logger.error("更新用户失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("用户更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. 执行删除
            int result = userMapper.deleteById(id);
            if (result <= 0) {
                logger.warn("用户删除失败，ID: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("用户删除失败");
            }

            logger.info("成功删除用户，ID: {}", id);
            return ResponseEntity.ok("用户删除成功");

        } catch (Exception e) {
            logger.error("删除用户失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("用户删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            // 验证状态值
            if (!"0".equals(status) && !"1".equals(status)) {
                return ResponseEntity.badRequest().body("状态值必须是0或1");
            }

            int result = userMapper.updateStatus(id, status);
            if (result <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("状态更新失败");
            }

            logger.info("用户状态更新，ID: {}, 新状态: {}", id, status);
            return ResponseEntity.ok("状态更新成功");

        } catch (Exception e) {
            logger.error("更新用户状态失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("状态更新失败: " + e.getMessage());
        }
    }

    // 在 UserController.java 中添加以下方法
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<Long>> getUserRoles(@PathVariable Long id) {
        try {
            List<Long> roleIds = userMapper.selectRoleIdsByUserId(id);
            return ResponseEntity.ok(roleIds);
        } catch (Exception e) {
            logger.error("获取用户角色失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 批量删除用户
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<String> batchDeleteUsers(@RequestBody Map<String, List<Long>> payload) {
        try {
            List<Long> ids = payload.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body("请选择要删除的用户");
            }

            int result = userMapper.batchDeleteUsers(ids);
            if (result <= 0) {
                logger.warn("批量删除失败，ID列表: {}", ids);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败");
            }

            logger.info("成功删除 {} 个用户", ids.size());
            return ResponseEntity.ok("删除成功");

        } catch (Exception e) {
            logger.error("批量删除用户失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<String> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("新密码不能为空");
            }

            // 1. 验证用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. 执行密码更新（需要实现updatePassword方法）
            int result = userMapper.updatePassword(id, newPassword);
            if (result <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("密码更新失败");
            }

            logger.info("用户密码重置成功，ID: {}", id);
            return ResponseEntity.ok("密码重置成功");

        } catch (Exception e) {
            logger.error("密码重置失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("密码重置失败: " + e.getMessage());
        }
    }

    // 获取用户角色ID列表
    @GetMapping("/{userId}/role-ids")
    public ResponseEntity<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        try {
            List<Long> roleIds = userMapper.selectRoleIdsByUserId(userId);
            return ResponseEntity.ok(roleIds);
        } catch (Exception e) {
            logger.error("获取用户角色失败，ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 更新用户角色
    @PutMapping("/{userId}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody Map<String, List<Long>> request) {

        try {
            List<Long> roleIds = request.get("roleIds");
            if (roleIds == null) {
                return ResponseEntity.badRequest().body("角色ID列表不能为空");
            }

            // 1. 先删除现有角色关联
            userMapper.deleteUserRoles(userId);

            // 2. 添加新角色关联
            if (!roleIds.isEmpty()) {
                userMapper.insertUserRoles(userId, roleIds);
            }

            return ResponseEntity.ok("角色分配成功");
        } catch (Exception e) {
            logger.error("角色分配失败，用户ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("角色分配失败: " + e.getMessage());
        }


    }

    @GetMapping("/user-export")
    public ResponseEntity<InputStreamResource> exportUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedStartTime = startTime == null || startTime.isEmpty() ? null : formatter.parse(startTime);
            Date parsedEndTime = endTime == null || endTime.isEmpty() ? null : formatter.parse(endTime);


            List<User> users = userMapper.selectByConditionForExport(
                    username, name, phone, email, status, departmentId, parsedStartTime, parsedEndTime
            );

            ByteArrayInputStream excelStream = convertUsersToExcel(users);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "users.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(excelStream));
        } catch (Exception e) {
            logger.error("导出用户失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ByteArrayInputStream convertUsersToExcel(List<User> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Username");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Phone");
            headerRow.createCell(5).setCellValue("Department ID");
            headerRow.createCell(6).setCellValue("Status");
            headerRow.createCell(7).setCellValue("Create Time");

            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId().longValue() : 0);
                row.createCell(1).setCellValue(user.getUsername() != null ? user.getUsername() : "");
                row.createCell(2).setCellValue(user.getName() != null ? user.getName() : "");
                row.createCell(3).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(4).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(5).setCellValue(user.getDepartmentId() != null ? user.getDepartmentId().longValue() : 0);
                row.createCell(6).setCellValue(user.getStatus() != null ? user.getStatus() : "");
                row.createCell(7).setCellValue(user.getCreateTime() != null ? user.getCreateTime().toString() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/condition")
    public ResponseEntity<?> getUsersByCondition(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<User> users = userMapper.selectByCondition(
                    username, name, phone, email,
                    departmentId, status, startTime, endTime,
                    (page - 1) * size, size);

            // 获取总数
            int total = userMapper.countByCondition(
                    username, name, phone, email,
                    departmentId, status, startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("data", users);
            response.put("total", total);
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询用户失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}