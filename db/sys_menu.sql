CREATE TABLE sys_menu (
  menuId bigserial NOT NULL,
  menuName varchar(500) NOT NULL,
  parentId bigint DEFAULT NULL,
  orderNum integer DEFAULT NULL,
  path varchar(500) DEFAULT '',
  component varchar(500) DEFAULT NULL,
  isFrame varchar(500) DEFAULT '1',
  menuType char(1) NOT NULL,
  visible varchar(500) DEFAULT '0',
  status varchar(500) DEFAULT '0',
  perms varchar(500) DEFAULT NULL,
  icon varchar(500) DEFAULT '#',
  createBy varchar(500) DEFAULT '',
  createTime timestamp DEFAULT CURRENT_TIMESTAMP,
  updateBy varchar(500) DEFAULT '',
  updateTime timestamp DEFAULT CURRENT_TIMESTAMP,
  remark varchar(500) DEFAULT '',
  PRIMARY KEY (menuId),
  CONSTRAINT idx_sys_menu_name_root UNIQUE (menuName),
  CONSTRAINT idx_sys_menu_name_parent UNIQUE (menuName, parentId)
);

-- 创建索引
CREATE INDEX idx_sys_menu_parentId ON sys_menu (parentId);
CREATE INDEX idx_sys_menu_status ON sys_menu (status);
CREATE INDEX idx_sys_menu_type ON sys_menu (menuType);


INSERT INTO `public`.`sys_menu` (`menuName`,`parentId`,`orderNum`,`path`,`component`,`isFrame`,`menuType`,`visible`,`status`,`perms`,`icon`,`createBy`,`createTime`,`updateBy`,`updateTime`,`remark`) VALUES
	 ('系统管理',NULL,1,'system',NULL,'1','M','0','0',NULL,'system','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','系统管理目录'),
	 ('用户管理',1,1,'user','system/user/index','1','C','0','0','system:user:list','user','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','用户管理菜单'),
	 ('用户新增',2,1,NULL,NULL,'1','F','0','0','system:user:add',NULL,'admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','用户新增按钮'),
	 ('官方网站',1,9,'http://www.example.com',NULL,'0','C','0','0',NULL,'link','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','系统官网链接'),
	 ('开发工具',NULL,3,'dev-tools',NULL,'1','M','1','0',NULL,'tool','admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','开发工具目录(隐藏)'),
	 ('用户删除',2,3,NULL,NULL,'1','F','0','1','system:user:remove',NULL,'admin','''2025-07-03 21:10:23''','admin','''2025-07-03 21:10:23''','用户删除按钮(停用)');