-- `public`.`department_info` definition

-- Drop table

-- DROP TABLE `public`.`department_info`;

CREATE TABLE `public`.`department_info` (
	`id` bigint DEFAULT nextval('department_info_id_seq'::regclass),
	`name` character varying(10 char) NOT NULL,
	`code` character varying(10 char) NOT NULL,
	`parent_id` bigint NULL,
	`leader` character varying(10 char) NULL,
	`phone` character varying(10 char) NULL,
	`email` character varying(10 char) NULL,
	`order_num` integer NULL DEFAULT 0,
	`status` character varying(10 char) NULL DEFAULT '正常'::varchar,
	`create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `chk_email` CHECK (((email IS NULL) OR ((email)::text ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'::text))),
	CONSTRAINT `chk_status` CHECK (((status)::text = ANY ((ARRAY['正常'::varchar, '停用'::varchar])::text[]))),
	CONSTRAINT `department_info_code_key` UNIQUE (code),
	CONSTRAINT `department_info_pkey` PRIMARY KEY (id)
)TABLESPACE sys_default;
CREATE INDEX idx_department_code ON public.department_info USING btree (code NULLS FIRST) TABLESPACE sys_default;
CREATE INDEX idx_department_parent ON public.department_info USING btree (parent_id NULLS FIRST) TABLESPACE sys_default;
CREATE INDEX idx_department_status ON public.department_info USING btree (status NULLS FIRST) TABLESPACE sys_default;


-- `public`.`department_info` foreign keys

ALTER TABLE `public`.`department_info` ADD CONSTRAINT `fk_parent_department` FOREIGN KEY (parent_id) REFERENCES department_info(id);
INSERT INTO `public`.`department_info` (`name`,`code`,`parent_id`,`leader`,`phone`,`email`,`order_num`,`status`,`create_time`) VALUES
	 ('人力资源部','HR001',NULL,'张经理','13800138001','hr@company.com',1,'正常','''2025-07-02 11:15:37'''),
	 ('财务部','FIN001',NULL,'李经理','13800138002','finance@company.com',2,'正常','''2025-07-02 11:15:37'''),
	 ('技术研发部','TECH001',NULL,'王经理','13800138003','tech@company.com',3,'正常','''2025-07-02 11:15:37'''),
	 ('市场营销部','MKT001',103,'赵经理','13800138004','marketing@company.com',4,'正常','''2025-07-02 11:15:37'''),
	 ('客户服务部','CS001',101,'刘经理','13800138005','service@company.com',5,'正常','''2025-07-02 11:15:37'''),
	 ('产品设计部','PD001',103,'陈经理','13800138006','product@company.com',6,'正常','''2025-07-02 11:15:37'''),
	 ('行政部','ADM001',101,'杨经理','13800138007','admin@company.com',7,'正常','''2025-07-02 11:15:37'''),
	 ('质量保障部','QA001',103,'吴经理','13800138008','qa@company.com',8,'正常','''2025-07-02 11:15:37'''),
	 ('销售部','SALES001',102,'周经理','13800138009','sales@company.com',9,'正常','''2025-07-02 11:15:37'''),
	 ('采购部','PUR001',102,'郑经理','13800138010','purchase@company.com',10,'停用','''2025-07-02 11:15:37''');
INSERT INTO `public`.`department_info` (`name`,`code`,`parent_id`,`leader`,`phone`,`email`,`order_num`,`status`,`create_time`) VALUES
	 ('测试部门','123456',101,'张三','1234566','shiaboyi@qq.com',0,'正常','''2025-07-04 16:33:11'''),
	 ('测试子部门','111111',10,'李四','12345678901','572401752@qq.com',0,'正常','''2025-07-04 16:46:01'''),
	 ('测试子子部门','66666666666',13,'1','12345432134','shiaboyi@qq.com',0,'正常','''2025-07-04 16:57:47'''),
	 ('测试子子子部门','919191233',14,'1','15604029604','572401752@qq.com',0,'正常','''2025-07-05 01:05:31''');