package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Role;
import com.example.webcarproject.mapper.RoleMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleMapper roleMapper;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

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
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Role role = roleMapper.selectById(id);
            if (role == null) {
                response.put("success", false);
                response.put("message", "角色不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<Long> menuIds = roleMapper.selectMenuIdsByRoleId(id);
            role.setMenuIds(menuIds);

            response.put("success", true);
            response.put("data", role);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping
    public Map<String, Object> add(@RequestBody Role role) {
        Map<String, Object> result = new HashMap<>();
        role.setCreateTime(new Date());
        role.setStatus("0");
        int res = roleMapper.insert(role);
        if (res > 0 && role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            roleMapper.insertRoleMenus(role.getId(), role.getMenuIds());
        }
        result.put("success", res > 0);
        result.put("message", res > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Role role) {
        Map<String, Object> result = new HashMap<>();
        role.setId(id);
        role.setUpdateTime(new Date());
        int res = roleMapper.update(role);
        if (res > 0) {
            roleMapper.deleteRoleMenus(id);
            if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
                roleMapper.insertRoleMenus(id, role.getMenuIds());
            }
        }
        result.put("success", res > 0);
        result.put("message", res > 0 ? "更新成功" : "更新失败");
        return result;
    }
    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        int result = roleMapper.updateStatus(id, status);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "状态更新成功" : "状态更新失败");
        return response;
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

    @PostMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDelete(@RequestBody Map<String, List<Long>> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                response.put("success", false);
                response.put("message", "请提供要删除的角色ID列表");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查是否有角色关联用户
            for (Long id : ids) {
                if (roleMapper.countUsersByRoleId(id) > 0) {
                    response.put("success", false);
                    response.put("message", "角色ID " + id + " 下有关联用户，无法删除");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // 批量删除
            int count = 0;
            for (Long id : ids) {
                roleMapper.deleteRoleMenus(id);
                count += roleMapper.deleteById(id);
            }

            response.put("success", true);
            response.put("message", "成功删除 " + count + " 个角色");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {

        try {
            // 查询数据
            List<Role> roles = roleMapper.selectByCondition(name, code, status, startTime, endTime);

            // 转换为Excel
            ByteArrayInputStream excelStream = convertRolesToExcel(roles);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "roles.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(excelStream));

        } catch (Exception e) {
            logger.error("导出角色失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ByteArrayInputStream convertRolesToExcel(List<Role> roles) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Roles");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("角色ID");
            headerRow.createCell(1).setCellValue("角色名称");
            headerRow.createCell(2).setCellValue("角色编码");
            headerRow.createCell(3).setCellValue("角色描述");
            headerRow.createCell(4).setCellValue("数据权限");
            headerRow.createCell(5).setCellValue("状态");
            headerRow.createCell(6).setCellValue("创建时间");

            // 填充数据
            int rowNum = 1;
            for (Role role : roles) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(role.getId());
                row.createCell(1).setCellValue(role.getName());
                row.createCell(2).setCellValue(role.getCode());
                row.createCell(3).setCellValue(role.getDescription());
                row.createCell(4).setCellValue(role.getDataScope());
                row.createCell(5).setCellValue(role.getStatus().equals("0") ? "启用" : "禁用");
                row.createCell(6).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(role.getCreateTime()));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}