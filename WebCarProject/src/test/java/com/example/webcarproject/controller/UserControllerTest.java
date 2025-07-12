package com.example.webcarproject.controller;

import com.example.webcarproject.entity.User;
import com.example.webcarproject.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for registration
    @Test
    void register_EmptyUsername() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "");
        params.put("password", "Password123");
        params.put("confirmPassword", "Password123");

        Map<String, Object> result = userController.register(params);

        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名不能为空", result.get("message"));
    }

    @Test
    void register_PasswordMismatch() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "newuser");
        params.put("password", "Password123");
        params.put("confirmPassword", "Different123");

        Map<String, Object> result = userController.register(params);

        assertFalse((Boolean) result.get("success"));
        assertEquals("两次输入的密码不一致", result.get("message"));
    }

    @Test
    void register_WeakPassword() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "newuser");
        params.put("password", "weak");
        params.put("confirmPassword", "weak");

        Map<String, Object> result = userController.register(params);

        assertFalse((Boolean) result.get("success"));
        assertEquals("密码强度不足，请使用字母+数字组合", result.get("message"));
    }

    @Test
    void register_DatabaseError() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "newuser");
        params.put("password", "Password123");
        params.put("confirmPassword", "Password123");

        when(userMapper.countByUsername("newuser")).thenReturn(0);
        when(userMapper.insertlogin(any(User.class))).thenReturn(0);

        Map<String, Object> result = userController.register(params);

        assertFalse((Boolean) result.get("success"));
        assertEquals("注册失败", result.get("message"));
    }

    // Test cases for login
    @Test
    void login_WrongPassword() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "testuser");
        params.put("password", "wrongpassword");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("hashedpassword");
        mockUser.setSalt("somesalt");

        when(userMapper.findByUsername("testuser")).thenReturn(mockUser);

        Map<String, Object> result = userController.login(params);

        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或密码错误", result.get("message"));
    }

    // Test cases for user creation
    @Test
    void createUser_DatabaseError() {
        User newUser = createUser(null, "newuser");
        when(userMapper.insert(any(User.class))).thenReturn(0);

        ResponseEntity<?> response = userController.createUser(newUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("数据库操作失败", response.getBody());
    }

    @Test
    void createUser_ExceptionHandling() {
        User newUser = createUser(null, "newuser");
        when(userMapper.insert(any(User.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = userController.createUser(newUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("创建失败"));
    }

    // Test cases for user update
    @Test
    void updateUser_NotFound() {
        Long userId = 999L;
        User updatedUser = createUser(userId, "updated");
        when(userMapper.selectById(userId)).thenReturn(null);

        ResponseEntity<?> response = userController.updateUser(userId, updatedUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser_DatabaseError() {
        Long userId = 1L;
        User existingUser = createUser(userId, "existing");
        User updatedUser = createUser(userId, "updated");

        when(userMapper.selectById(userId)).thenReturn(existingUser);
        when(userMapper.update(any(User.class))).thenReturn(0);

        ResponseEntity<?> response = userController.updateUser(userId, updatedUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("用户更新失败", response.getBody());
    }

    @Test
    void updateUser_ExceptionHandling() {
        Long userId = 1L;
        User existingUser = createUser(userId, "existing");
        User updatedUser = createUser(userId, "updated");

        when(userMapper.selectById(userId)).thenReturn(existingUser);
        when(userMapper.update(any(User.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = userController.updateUser(userId, updatedUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("用户更新失败"));
    }

    // Test cases for status update
    @Test
    void updateUserStatus_InvalidStatus() {
        Long userId = 1L;
        String invalidStatus = "invalid";

        ResponseEntity<String> response = userController.updateUserStatus(userId, invalidStatus);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("状态值必须是0或1", response.getBody());
    }

    @Test
    void updateUserStatus_DatabaseError() {
        Long userId = 1L;
        String status = "1";
        when(userMapper.updateStatus(userId, status)).thenReturn(0);

        ResponseEntity<String> response = userController.updateUserStatus(userId, status);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("状态更新失败", response.getBody());
    }

    // Test cases for batch delete
    @Test
    void batchDeleteUsers_EmptyList() {
        Map<String, List<Long>> payload = new HashMap<>();
        payload.put("ids", Collections.emptyList());

        ResponseEntity<String> response = userController.batchDeleteUsers(payload);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("请选择要删除的用户", response.getBody());
    }

    @Test
    void batchDeleteUsers_DatabaseError() {
        List<Long> userIds = Arrays.asList(1L, 2L);
        Map<String, List<Long>> payload = new HashMap<>();
        payload.put("ids", userIds);

        when(userMapper.batchDeleteUsers(userIds)).thenReturn(0);

        ResponseEntity<String> response = userController.batchDeleteUsers(payload);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("删除失败", response.getBody());
    }

    // Test cases for password reset
    @Test
    void resetPassword_EmptyPassword() {
        Long userId = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "");

        ResponseEntity<String> response = userController.resetPassword(userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("新密码不能为空", response.getBody());
    }

    @Test
    void resetPassword_DatabaseError() {
        Long userId = 1L;
        User existingUser = createUser(userId, "user");
        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "newpass123");

        when(userMapper.selectById(userId)).thenReturn(existingUser);
        when(userMapper.updatePassword(userId, "newpass123")).thenReturn(0);

        ResponseEntity<String> response = userController.resetPassword(userId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("密码更新失败", response.getBody());
    }

    // Test cases for role management
    @Test
    void updateUserRoles_EmptyRoleIds() {
        Long userId = 1L;
        Map<String, List<Long>> request = new HashMap<>();
        request.put("roleIds", Collections.emptyList());

        ResponseEntity<String> response = userController.updateUserRoles(userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("角色ID列表不能为空", response.getBody());
    }

    @Test
    void updateUserRoles_ExceptionHandling() {
        Long userId = 1L;
        List<Long> roleIds = Arrays.asList(1L, 2L);
        Map<String, List<Long>> request = new HashMap<>();
        request.put("roleIds", roleIds);

        when(userMapper.deleteUserRoles(userId)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<String> response = userController.updateUserRoles(userId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("角色分配失败"));
    }

    // Test cases for export functionality
    @Test
    void exportUsers_Success() throws Exception {
        List<User> mockUsers = Collections.singletonList(createUser(1L, "exportuser"));
        when(userMapper.selectByConditionForExport(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockUsers);

        ResponseEntity<?> response = userController.exportUsers(
                null, null, null, null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void exportUsers_ExceptionHandling() throws Exception {
        when(userMapper.selectByConditionForExport(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Export error"));

        ResponseEntity<?> response = userController.exportUsers(
                null, null, null, null, null, null, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Test cases for conditional query
    @Test
    void getUsersByCondition_ExceptionHandling() {
        when(userMapper.selectByCondition(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Query error"));

        ResponseEntity<?> response = userController.getUsersByCondition(
                null, null, null, null, null, null, null, null, 1, 10);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Test password strength checker
    @Test
    void checkPasswordStrength_Weak() {
        UserController.PasswordStrength strength = userController.checkPasswordStrength("weak");
        assertEquals(UserController.PasswordStrength.WEAK, strength);
    }

    @Test
    void checkPasswordStrength_Medium() {
        UserController.PasswordStrength strength = userController.checkPasswordStrength("Medium1");
        assertEquals(UserController.PasswordStrength.MEDIUM, strength);
    }

    @Test
    void checkPasswordStrength_Strong() {
        UserController.PasswordStrength strength = userController.checkPasswordStrength("Strong@123");
        assertEquals(UserController.PasswordStrength.STRONG, strength);
    }

    // Helper method
    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setName(username + " Name");
        user.setEmail(username + "@example.com");
        user.setPhone("1234567890");
        user.setStatus("0");
        user.setCreateTime(new Date());
        user.setSalt("salt");
        user.setPassword(DigestUtils.md5DigestAsHex(("password" + "salt").getBytes()));
        return user;
    }
}