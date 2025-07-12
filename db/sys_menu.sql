INSERT INTO `public`.`sys_menu` (`menuName`,`parentId`,`orderNum`,`path`,`component`,`isFrame`,`menuType`,`visible`,`status`,`perms`,`icon`,`createBy`,`createTime`,`updateBy`,`updateTime`,`remark`) VALUES
	 ('系统管理',NULL,1,'system',NULL,'1','M','0','0',NULL,'system','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','系统管理目录'),
	 ('用户管理',1,1,'user','system/user/index','1','C','0','0','system:user:list','user','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','用户管理菜单'),
	 ('用户新增',2,1,NULL,NULL,'1','F','0','0','system:user:add',NULL,'admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','用户新增按钮'),
	 ('官方网站',1,9,'http://www.example.com',NULL,'0','C','0','0',NULL,'link','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','系统官网链接'),
	 ('开发工具',NULL,3,'dev-tools',NULL,'1','M','1','0',NULL,'tool','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','开发工具目录(隐藏)'),
	 ('用户删除',2,3,NULL,NULL,'1','F','0','1','system:user:remove',NULL,'admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','用户删除按钮(停用)');