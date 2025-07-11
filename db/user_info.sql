-- `public`.`user_info` definition

-- Drop table

-- DROP TABLE `public`.`user_info`;

CREATE TABLE `public`.`user_info` (
	`id` bigint AUTO_INCREMENT NOT NULL,
	`username` character varying(40 char) NOT NULL,
	`password` character varying(40 char) NOT NULL,
	`name` character varying(40 char) NULL,
	`email` character varying(40 char) NULL,
	`phone` character varying(40 char) NULL,
	`department_id` bigint NULL,
	`status` character(1 char) NULL DEFAULT '0'::bpchar,
	`last_login_time` timestamp NULL,
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`update_time` timestamp NULL,
	`salt` character varying(40 char) NOT NULL,
	CONSTRAINT `user_info_pkey` PRIMARY KEY (id),
	CONSTRAINT `user_info_username_key` UNIQUE (username)
) TABLESPACE sys_default AUTO_INCREMENT = 1;


INSERT INTO `public`.`user_info` (`username`,`password`,`name`,`email`,`phone`,`department_id`,`status`,`last_login_time`,`create_time`,`update_time`,`salt`) VALUES
	 ('zzy','5603c3a5197e8180d0ebaed43fcedcbc',NULL,NULL,NULL,NULL,'0',NULL,'''2025-07-11 21:18:00''',NULL,'3fd30893b14f0004'),
	 ('user3','e10adc3949ba59abbe56e057f20f883e','李四','lisi@example.com','13800138002',102,'0','''2023-05-14 14:25:00''','''2023-01-11 09:15:00''','''2023-05-14 14:25:00''','def456'),
	 ('user2','e10adc3949ba59abbe56e057f20f883e','张三','zhangsan@example.com','13800138001',101,'0','''2023-05-15 09:30:00''','''2023-01-10 08:00:00''','''2023-05-15 09:30:00''','abc123');