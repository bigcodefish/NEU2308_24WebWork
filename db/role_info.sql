-- `public`.`role_info` definition

-- Drop table

-- DROP TABLE `public`.`role_info`;

CREATE TABLE `public`.`role_info` (
	`id` bigint AUTO_INCREMENT NOT NULL,
	`name` character varying(20 char) NOT NULL,
	`code` character varying(20 char) NOT NULL,
	`description` character varying(20 char) NULL,
	`data_scope` character varying(20 char) NOT NULL DEFAULT 'self'::varchar,
	`status` character(1 char) NOT NULL DEFAULT '0'::bpchar,
	`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`update_time` datetime NULL,
	CONSTRAINT `role_info_pkey` PRIMARY KEY (id),
	CONSTRAINT `uk_code` UNIQUE (code),
	CONSTRAINT `uk_name` UNIQUE (name)
) TABLESPACE sys_default AUTO_INCREMENT = 1;

INSERT INTO `public`.`role_info` (`name`,`code`,`description`,`data_scope`,`status`,`create_time`,`update_time`) VALUES
	 ('普通用户','user','普通用户，仅拥有基础权限','self','0','''2025-07-02 15:32:41''','''2025-07-03 16:00:18'''),
	 ('访客','guest','只读访客，仅能查看部分数据','self','0','''2025-07-02 15:32:41''','''2025-07-03 16:00:18'''),
	 ('超级管理员','admin','系统超级管理员，拥有所有权限','all','0','''2025-07-02 15:32:41''','''2025-07-03 16:00:19'''),
	 ('系统管理员','system','系统管理员，拥有大部分管理权限','all','0','''2025-07-02 15:32:41''','''2025-07-03 16:00:19'''),
	 ('部门管理员','dept_admin','部门管理员，管理本部门及下级部门','dept','0','''2025-07-02 15:32:41''','''2025-07-03 16:04:55''');