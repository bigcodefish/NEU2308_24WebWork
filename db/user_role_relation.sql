-- `public`.`user_role_relation` definition

-- Drop table

-- DROP TABLE `public`.`user_role_relation`;

CREATE TABLE `public`.`user_role_relation` (
	`id` bigint DEFAULT nextval('user_role_relation_id_seq'::regclass),
	`user_id` bigint NOT NULL,
	`role_id` bigint NOT NULL,
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `user_role_relation_pkey` PRIMARY KEY (id),
	CONSTRAINT `user_role_relation_user_id_role_id_key` UNIQUE (user_id, role_id)
)TABLESPACE sys_default;
CREATE INDEX idx_user_role_role ON public.user_role_relation USING btree (role_id NULLS FIRST) TABLESPACE sys_default;
CREATE INDEX idx_user_role_user ON public.user_role_relation USING btree (user_id NULLS FIRST) TABLESPACE sys_default;
INSERT INTO `public`.`user_role_relation` (`user_id`,`role_id`,`create_time`) VALUES
	 (2,1,'''2025-07-03 16:12:11'''),
	 (3,1,'''2025-07-03 16:12:11'''),
	 (4,1,'''2025-07-03 16:12:11'''),
	 (5,1,'''2025-07-03 16:12:11'''),
	 (6,1,'''2025-07-03 16:12:11'''),
	 (7,1,'''2025-07-03 16:12:11'''),
	 (8,1,'''2025-07-03 16:12:11'''),
	 (9,1,'''2025-07-03 16:12:11''');