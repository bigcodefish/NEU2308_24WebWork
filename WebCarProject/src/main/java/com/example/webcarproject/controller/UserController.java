package com.example.webcarproject.controller;

import com.example.webcarproject.entity.User;
import com.example.webcarproject.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
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
            // 1. 验证必要字段
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空");
            }

            // 2. 确保ID为空（由数据库生成）
            user.setId(null);

            // 3. 设置默认值
            if (user.getStatus() == null) {
                user.setStatus("0"); // 默认激活状态
            }
            user.setCreateTime(new Date());

            // 4. 执行插入
            int result = userMapper.insert(user);
            if (result <= 0) {
                logger.error("数据库插入失败，受影响行数: {}", result);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("用户创建失败：数据库操作未生效");
            }

            // 5. 返回成功响应（包含生成的ID）
            logger.info("成功创建用户，ID: {}", user.getId());
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            logger.error("用户创建失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("用户创建失败: " + e.getMessage());
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
}