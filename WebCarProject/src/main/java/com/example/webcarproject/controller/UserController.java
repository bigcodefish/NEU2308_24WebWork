package com.example.webcarproject.controller;

import com.example.webcarproject.entity.User;
import com.example.webcarproject.mapper.UserMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<User> getByCondition(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        List<User> users = userMapper.selectByCondition(username, name, phone, email,
                departmentId, status, startTime, endTime);

        // 查询每个用户的角色信息
        users.forEach(user -> {
            List<Long> roleIds = userMapper.selectRoleIdsByUserId(user.getId());
            user.setRoleIds(roleIds);
        });

        return users;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            List<Long> roleIds = userMapper.selectRoleIdsByUserId(id);
            user.setRoleIds(roleIds);
        }
        return user;
    }

    @PostMapping
    public String add(@RequestBody User user) {
        user.setCreateTime(new Date());
        user.setStatus("0"); // 默认状态为正常
        int result = userMapper.insert(user);
        if (result > 0 && user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            userMapper.insertUserRoles(user.getId(), user.getRoleIds());
        }
        return result > 0 ? "添加成功" : "添加失败";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setUpdateTime(new Date());
        int result = userMapper.update(user);
        if (result > 0) {
            userMapper.deleteUserRoles(id);
            if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
                userMapper.insertUserRoles(id, user.getRoleIds());
            }
        }
        return result > 0 ? "更新成功" : "更新失败";
    }

    @PutMapping("/{id}/password")
    public String resetPassword(@PathVariable Long id, @RequestParam String password) {
        int result = userMapper.updatePassword(id, password);
        return result > 0 ? "密码重置成功" : "密码重置失败";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        int result = userMapper.updateStatus(id, status);
        return result > 0 ? "状态更新成功" : "状态更新失败";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        userMapper.deleteUserRoles(id);
        int result = userMapper.deleteById(id);
        return result > 0 ? "删除成功" : "删除失败";
    }
}