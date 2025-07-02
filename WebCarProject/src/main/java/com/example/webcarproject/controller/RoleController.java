package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Role;
import com.example.webcarproject.mapper.RoleMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleMapper roleMapper;

    public RoleController(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @GetMapping
    public List<Role> getByCondition(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        return roleMapper.selectByCondition(name, code, status, startTime, endTime);
    }

    @GetMapping("/{id}")
    public Role getById(@PathVariable Long id) {
        Role role = roleMapper.selectById(id);
        if (role != null) {
            List<Long> menuIds = roleMapper.selectMenuIdsByRoleId(id);
            role.setMenuIds(menuIds);
        }
        return role;
    }

    @PostMapping
    public String add(@RequestBody Role role) {
        role.setCreateTime(new Date());
        role.setStatus("0"); // 默认状态为启用
        int result = roleMapper.insert(role);
        if (result > 0 && role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            roleMapper.insertRoleMenus(role.getId(), role.getMenuIds());
        }
        return result > 0 ? "添加成功" : "添加失败";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        role.setUpdateTime(new Date());
        int result = roleMapper.update(role);
        if (result > 0) {
            roleMapper.deleteRoleMenus(id);
            if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
                roleMapper.insertRoleMenus(id, role.getMenuIds());
            }
        }
        return result > 0 ? "更新成功" : "更新失败";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        int result = roleMapper.updateStatus(id, status);
        return result > 0 ? "状态更新成功" : "状态更新失败";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        // 检查是否有用户关联
        int userCount = roleMapper.countUsersByRoleId(id);
        if (userCount > 0) {
            return "删除失败，该角色下有关联用户";
        }

        roleMapper.deleteRoleMenus(id);
        int result = roleMapper.deleteById(id);
        return result > 0 ? "删除成功" : "删除失败";
    }

    @GetMapping("/{id}/users")
    public List<Long> getUsersByRoleId(@PathVariable Long id) {
        return roleMapper.selectUserIdsByRoleId(id);
    }

    @PostMapping("/{id}/users")
    public String assignUsers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        roleMapper.deleteRoleUsers(id);
        if (userIds != null && !userIds.isEmpty()) {
            roleMapper.insertRoleUsers(id, userIds);
        }
        return "用户分配成功";
    }
}