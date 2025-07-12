package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Department;
import com.example.webcarproject.mapper.DepartmentMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentMapper departmentMapper;

    public DepartmentController(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @GetMapping
    public List<Department> getByCondition(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {

        List<Department> departments = departmentMapper.selectByCondition(name, code, status, startTime, endTime);
        for (Department dept : departments) {
            dept.setUserCount(departmentMapper.countUsersByDeptId(dept.getId()));
        }
        return departments;
    }

    @GetMapping("/tree")
    public List<Department> getTree() {
        // 获取所有部门数据
        List<Department> allDepts = departmentMapper.selectAllDepts();

        // 构建树形结构
        return buildDeptTree(allDepts);
    }

    @GetMapping("/{id}")
    public Department getById(@PathVariable Long id) {
        return departmentMapper.selectById(id);
    }

    @PostMapping
    public String add(@RequestBody Department department) {
        // 不再设置默认status，直接使用前端传递的值
        department.setCreateTime(new Date());
        int result = departmentMapper.insert(department);
        return result > 0 ? "添加成功" : "添加失败";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        // 移除了设置updateTime的代码
        int result = departmentMapper.update(department);
        return result > 0 ? "更新成功" : "更新失败";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        int result = departmentMapper.updateStatus(id, status);
        return result > 0 ? "状态更新成功" : "状态更新失败";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        // 检查是否有用户关联
        int userCount = departmentMapper.countUsersByDeptId(id);
        if (userCount > 0) {
            return "删除失败，该部门下有关联用户";
        }

        // 递归删除子部门
        deleteDepartmentAndChildren(id);

        return "删除成功";
    }

    @GetMapping("/{id}/users")
    public List<Long> getUsersByDeptId(@PathVariable Long id) {
        return departmentMapper.selectUserIdsByDeptId(id);
    }

    // 构建树形结构的方法
    private List<Department> buildDeptTree(List<Department> allDepts) {
        // 创建ID到部门的映射
        Map<Long, Department> deptMap = new HashMap<>();
        allDepts.forEach(dept -> {
            dept.setChildren(new ArrayList<>()); // 初始化children为空列表
            deptMap.put(dept.getId(), dept);
        });

        // 构建树
        List<Department> tree = new ArrayList<>();
        for (Department dept : allDepts) {
            if (dept.getParentId() == null) {
                tree.add(dept);  // 添加根节点
            } else {
                Department parent = deptMap.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);  // 添加子节点
                }
            }
        }
        return tree;
    }

    private void deleteDepartmentAndChildren(Long deptId) {
        // 获取所有子部门ID
        List<Department> children = departmentMapper.selectChildren(deptId);

        // 递归删除子部门
        for (Department child : children) {
            // 检查子部门是否有用户
            if (departmentMapper.countUsersByDeptId(child.getId()) == 0) {
                deleteDepartmentAndChildren(child.getId());
            }
        }

        // 删除当前部门
        departmentMapper.deleteById(deptId);
    }
}