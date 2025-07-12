package com.example.webcarproject.controller;

import com.example.webcarproject.entity.Menu;
import com.example.webcarproject.mapper.MenuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuControllerTest {

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listMenus_Success() {
        // Prepare test data
        List<Menu> mockMenus = Arrays.asList(
                createMenu(1L, "System", 0L),
                createMenu(2L, "User", 1L)
        );
        when(menuMapper.selectAllWithParams(anyString(), anyString(), anyString())).thenReturn(mockMenus);

        // Call method
        Map<String, Object> result = menuController.list(null, null, null);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        List<Menu> resultMenus = (List<Menu>) result.get("data");
        assertEquals(1, resultMenus.size()); // Only one root menu expected
        verify(menuMapper, times(1)).selectAllWithParams(null, null, null);
    }

    @Test
    void listMenus_WithFilters() {
        // Prepare test data
        List<Menu> mockMenus = Collections.singletonList(
                createMenu(1L, "System", 0L)
        );
        when(menuMapper.selectAllWithParams("Sys", "0", "0")).thenReturn(mockMenus);

        // Call method
        Map<String, Object> result = menuController.list("Sys", "0", "0");

        // Verify results
        assertTrue((Boolean) result.get("success"));
        verify(menuMapper, times(1)).selectAllWithParams("Sys", "0", "0");
    }

    @Test
    void listMenus_Exception() {
        // Prepare test data
        when(menuMapper.selectAllWithParams(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // Call method
        Map<String, Object> result = menuController.list(null, null, null);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("查询菜单列表失败", result.get("message"));
    }

    @Test
    void getMenuInfo_Found() {
        // Prepare test data
        Long menuId = 1L;
        Menu mockMenu = createMenu(menuId, "System", 0L);
        when(menuMapper.selectMenuById(menuId)).thenReturn(mockMenu);

        // Call method
        Map<String, Object> result = menuController.getInfo(menuId);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        Menu resultMenu = (Menu) result.get("data");
        assertEquals(menuId, resultMenu.getMenuId());
        verify(menuMapper, times(1)).selectMenuById(menuId);
    }

    @Test
    void getMenuInfo_NotFound() {
        // Prepare test data
        Long menuId = 999L;
        when(menuMapper.selectMenuById(menuId)).thenReturn(null);

        // Call method
        Map<String, Object> result = menuController.getInfo(menuId);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("菜单不存在", result.get("message"));
    }

    @Test
    void addMenu_Success() {
        // Prepare test data
        Menu newMenu = createMenu(null, "New Menu", 0L);
        Menu savedMenu = createMenu(1L, "New Menu", 0L);

        when(menuMapper.checkMenuNameUnique(newMenu.getMenuName(), newMenu.getParentId())).thenReturn(0);
        when(menuMapper.insertMenu(any(Menu.class))).thenAnswer(invocation -> {
            Menu menu = invocation.getArgument(0);
            menu.setMenuId(1L);
            return 1;
        });

        // Call method
        Map<String, Object> result = menuController.add(newMenu);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertEquals("新增成功", result.get("message"));
        assertNotNull(result.get("data"));
        verify(menuMapper, times(1)).insertMenu(any(Menu.class));
    }

    @Test
    void addMenu_DuplicateName() {
        // Prepare test data
        Menu newMenu = createMenu(null, "Duplicate", 0L);
        when(menuMapper.checkMenuNameUnique(newMenu.getMenuName(), newMenu.getParentId())).thenReturn(1);

        // Call method
        Map<String, Object> result = menuController.add(newMenu);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("菜单名称已存在", result.get("message"));
        verify(menuMapper, never()).insertMenu(any(Menu.class));
    }

    @Test
    void editMenu_Success() {
        // Prepare test data
        Long menuId = 1L;
        Menu existingMenu = createMenu(menuId, "System", 0L);
        Menu updatedMenu = createMenu(menuId, "System Updated", 0L);

        when(menuMapper.selectMenuById(menuId)).thenReturn(existingMenu);
        when(menuMapper.checkMenuNameUnique(updatedMenu.getMenuName(), updatedMenu.getParentId())).thenReturn(0);
        when(menuMapper.updateMenu(updatedMenu)).thenReturn(1);

        // Call method
        Map<String, Object> result = menuController.edit(updatedMenu);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertEquals("修改成功", result.get("message"));
        verify(menuMapper, times(1)).updateMenu(updatedMenu);
    }

    @Test
    void editMenu_NotFound() {
        // Prepare test data
        Long menuId = 999L;
        Menu updatedMenu = createMenu(menuId, "Not Found", 0L);
        when(menuMapper.selectMenuById(menuId)).thenReturn(null);

        // Call method
        Map<String, Object> result = menuController.edit(updatedMenu);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("菜单不存在", result.get("message"));
        verify(menuMapper, never()).updateMenu(any(Menu.class));
    }

    @Test
    void editMenu_DuplicateName() {
        // Prepare test data
        Long menuId = 1L;
        Menu existingMenu = createMenu(menuId, "System", 0L);
        Menu updatedMenu = createMenu(menuId, "Duplicate", 0L);

        when(menuMapper.selectMenuById(menuId)).thenReturn(existingMenu);
        when(menuMapper.checkMenuNameUnique(updatedMenu.getMenuName(), updatedMenu.getParentId())).thenReturn(1);

        // Call method
        Map<String, Object> result = menuController.edit(updatedMenu);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("菜单名称已存在", result.get("message"));
        verify(menuMapper, never()).updateMenu(any(Menu.class));
    }

    @Test
    void batchUpdate_Success() {
        // Prepare test data
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> params = new HashMap<>();
        params.put("menuIds", menuIds);
        params.put("status", "0");
        params.put("visible", "1");

        when(menuMapper.batchUpdateMenu(menuIds, "0", "1")).thenReturn(3);

        // Call method
        Map<String, Object> result = menuController.batchUpdate(params);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertEquals("批量操作成功，影响3条记录", result.get("message"));
        verify(menuMapper, times(1)).batchUpdateMenu(menuIds, "0", "1");
    }

    @Test
    void batchUpdate_NoMenuSelected() {
        // Prepare test data
        Map<String, Object> params = new HashMap<>();
        params.put("menuIds", Collections.emptyList());

        // Call method
        Map<String, Object> result = menuController.batchUpdate(params);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("请选择要操作的菜单", result.get("message"));
        verify(menuMapper, never()).batchUpdateMenu(any(), any(), any());
    }

    @Test
    void removeMenu_Success() {
        // Prepare test data
        Long menuId = 1L;
        Menu existingMenu = createMenu(menuId, "System", 0L);

        when(menuMapper.selectMenuById(menuId)).thenReturn(existingMenu);
        when(menuMapper.hasChildByMenuId(menuId)).thenReturn(0);
        when(menuMapper.deleteMenuById(menuId)).thenReturn(1);

        // Call method
        Map<String, Object> result = menuController.remove(menuId);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertEquals("删除成功", result.get("message"));
        verify(menuMapper, times(1)).deleteMenuById(menuId);
    }

    @Test
    void removeMenu_NotFound() {
        // Prepare test data
        Long menuId = 999L;
        when(menuMapper.selectMenuById(menuId)).thenReturn(null);

        // Call method
        Map<String, Object> result = menuController.remove(menuId);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("菜单不存在", result.get("message"));
        verify(menuMapper, never()).deleteMenuById(anyLong());
    }

    @Test
    void removeMenu_HasChildren() {
        // Prepare test data
        Long menuId = 1L;
        Menu existingMenu = createMenu(menuId, "System", 0L);

        when(menuMapper.selectMenuById(menuId)).thenReturn(existingMenu);
        when(menuMapper.hasChildByMenuId(menuId)).thenReturn(2);

        // Call method
        Map<String, Object> result = menuController.remove(menuId);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("存在子菜单，不允许删除", result.get("message"));
        verify(menuMapper, never()).deleteMenuById(anyLong());
    }

    @Test
    void batchRemove_Success() {
        // Prepare test data
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        Map<String, List<Long>> params = new HashMap<>();
        params.put("menuIds", menuIds);

        when(menuMapper.hasChildByMenuId(anyLong())).thenReturn(0);
        when(menuMapper.batchDeleteMenu(menuIds)).thenReturn(3);

        // Call method
        Map<String, Object> result = menuController.batchRemove(params);

        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertEquals("批量删除成功，共删除3条记录", result.get("message"));
        verify(menuMapper, times(3)).hasChildByMenuId(anyLong());
        verify(menuMapper, times(1)).batchDeleteMenu(menuIds);
    }

    @Test
    void batchRemove_NoMenuSelected() {
        // Prepare test data
        Map<String, List<Long>> params = new HashMap<>();
        params.put("menuIds", Collections.emptyList());

        // Call method
        Map<String, Object> result = menuController.batchRemove(params);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("请选择要删除的菜单", result.get("message"));
        verify(menuMapper, never()).batchDeleteMenu(any());
    }

    @Test
    void batchRemove_HasChildren() {
        // Prepare test data
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        Map<String, List<Long>> params = new HashMap<>();
        params.put("menuIds", menuIds);

        when(menuMapper.hasChildByMenuId(1L)).thenReturn(1);

        // Call method
        Map<String, Object> result = menuController.batchRemove(params);

        // Verify results
        assertFalse((Boolean) result.get("success"));
        assertEquals("菜单ID 1 存在子菜单，不允许删除", result.get("message"));
        verify(menuMapper, never()).batchDeleteMenu(any());
    }

    private Menu createMenu(Long menuId, String menuName, Long parentId) {
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName(menuName);
        menu.setParentId(parentId);
        menu.setOrderNum(1);
        menu.setPath("/" + menuName.toLowerCase());
        menu.setComponent(menuName + "Component");
        menu.setIsFrame("0");
        menu.setMenuType("M");
        menu.setVisible("0");
        menu.setStatus("0");
        return menu;
    }
}