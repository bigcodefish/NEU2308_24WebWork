package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Menu;
import com.example.webcarproject.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuMapper menuMapper;

    private List<Menu> buildMenuTree(List<Menu> menus) {
        Map<Long, Menu> menuMap = new HashMap<>();
        menus.forEach(menu -> menuMap.put(menu.getMenuId(), menu));

        return menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .peek(menu -> menu.setChildren(findChildren(menu, menuMap)))
                .toList();
    }

    private List<Menu> findChildren(Menu parent, Map<Long, Menu> menuMap) {
        return menuMap.values().stream()
                .filter(menu -> menu.getParentId() != null && menu.getParentId().equals(parent.getMenuId()))
                .peek(menu -> menu.setChildren(findChildren(menu, menuMap)))
                .toList();
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) String menuName,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String visible) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Menu> allMenus = menuMapper.selectAllWithParams(menuName, status, visible);
            List<Menu> treeMenus = buildMenuTree(allMenus);

            result.put("success", true);
            result.put("data", treeMenus);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询菜单列表失败");
        }
        return result;
    }

    @GetMapping("/{menuId}")
    public Map<String, Object> getInfo(@PathVariable Long menuId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Menu menu = menuMapper.selectMenuById(menuId);
            if (menu != null) {
                result.put("success", true);
                result.put("data", menu);
            } else {
                result.put("success", false);
                result.put("message", "菜单不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询菜单详情失败");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody Menu menu) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId()) > 0) {
                result.put("success", false);
                result.put("message", "菜单名称已存在");
                return result;
            }

            int rows = menuMapper.insertMenu(menu);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "新增成功");
                result.put("data", menu);
            } else {
                result.put("success", false);
                result.put("message", "新增失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "新增菜单失败");
        }
        return result;
    }

    @PutMapping
    public Map<String, Object> edit(@RequestBody Menu menu) {
        Map<String, Object> result = new HashMap<>();
        try {
            Menu existingMenu = menuMapper.selectMenuById(menu.getMenuId());
            if (existingMenu == null) {
                result.put("success", false);
                result.put("message", "菜单不存在");
                return result;
            }

            if (!existingMenu.getMenuName().equals(menu.getMenuName()) &&
                    menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId()) > 0) {
                result.put("success", false);
                result.put("message", "菜单名称已存在");
                return result;
            }

            int rows = menuMapper.updateMenu(menu);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "修改成功");
                result.put("data", menu);
            } else {
                result.put("success", false);
                result.put("message", "修改失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修改菜单失败");
        }
        return result;
    }

    @PutMapping("/batch")
    public Map<String, Object> batchUpdate(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Long> menuIds = (List<Long>) params.get("menuIds");
            String status = (String) params.get("status");
            String visible = (String) params.get("visible");

            if (menuIds == null || menuIds.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要操作的菜单");
                return result;
            }

            int rows = menuMapper.batchUpdateMenu(menuIds, status, visible);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "批量操作成功，影响" + rows + "条记录");
            } else {
                result.put("success", false);
                result.put("message", "批量操作失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量操作失败");
        }
        return result;
    }

    @DeleteMapping("/{menuId}")
    public Map<String, Object> remove(@PathVariable Long menuId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Menu menu = menuMapper.selectMenuById(menuId);
            if (menu == null) {
                result.put("success", false);
                result.put("message", "菜单不存在");
                return result;
            }

            if (menuMapper.hasChildByMenuId(menuId) > 0) {
                result.put("success", false);
                result.put("message", "存在子菜单，不允许删除");
                return result;
            }

            int rows = menuMapper.deleteMenuById(menuId);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除菜单失败");
        }
        return result;
    }

    @DeleteMapping("/batch")
    public Map<String, Object> batchRemove(@RequestBody Map<String, List<Long>> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Long> menuIds = params.get("menuIds");
            if (menuIds == null || menuIds.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要删除的菜单");
                return result;
            }

            // 检查是否有子菜单
            for (Long menuId : menuIds) {
                if (menuMapper.hasChildByMenuId(menuId) > 0) {
                    result.put("success", false);
                    result.put("message", "菜单ID " + menuId + " 存在子菜单，不允许删除");
                    return result;
                }
            }

            int rows = menuMapper.batchDeleteMenu(menuIds);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "批量删除成功，共删除" + rows + "条记录");
            } else {
                result.put("success", false);
                result.put("message", "批量删除失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除失败");
        }
        return result;
    }
}