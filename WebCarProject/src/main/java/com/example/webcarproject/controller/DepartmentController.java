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
        return departmentMapper.selectByCondition(name, code, status, startTime, endTime);
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
        department.setCreateTime(new Date());
        department.setStatus("0"); // 默认状态为正常
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
        int userCount = departmentMapper.countUsersByDeptId(id);
        if (userCount > 0) {
            return "删除失败，该部门下有关联用户";
        }

        int childCount = departmentMapper.countChildren(id);
        if (childCount > 0) {
            return "删除失败，该部门下有子部门";
        }

        int result = departmentMapper.deleteById(id);
        return result > 0 ? "删除成功" : "删除失败";
    }

    @GetMapping("/{id}/users")
    public List<Long> getUsersByDeptId(@PathVariable Long id) {
        return departmentMapper.selectUserIdsByDeptId(id);
    }

    @GetMapping("/{parentId}/children")
    public List<Department> getChildrenByParentId(@PathVariable Long parentId) {
        return departmentMapper.selectChildren(parentId);
    }

    private List<Department> buildDeptTree(List<Department> allDepts) {
        // 创建ID到部门的映射
        Map<Long, Department> deptMap = new HashMap<>();
        allDepts.forEach(dept -> deptMap.put(dept.getId(), dept));

        // 构建树
        List<Department> tree = new ArrayList<>();
        for (Department dept : allDepts) {
            if (dept.getParentId() == null) {
                tree.add(dept);  // 添加根节点
            } else {
                Department parent = deptMap.get(dept.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dept);  // 添加子节点
                }
            }
        }
        return tree;
    }

}