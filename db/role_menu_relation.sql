CREATE TABLE role_menu_relation (
  id bigserial NOT NULL,
  role_id bigint NOT NULL,
  menu_id bigint NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_role_menu UNIQUE (role_id, menu_id)
);

INSERT INTO `public`.`role_menu_relation` (`role_id`,`menu_id`) VALUES
	 (1,1),
	 (1,2),
	 (1,3),
	 (1,4),
	 (1,5),
	 (1,6),
	 (1,7),
	 (1,8),
	 (1,9),
	 (1,10);
INSERT INTO `public`.`role_menu_relation` (`role_id`,`menu_id`) VALUES
	 (2,1),
	 (2,2),
	 (2,3),
	 (2,4),
	 (2,5),
	 (3,3),
	 (3,4),
	 (3,6),
	 (3,7),
	 (4,1);
INSERT INTO `public`.`role_menu_relation` (`role_id`,`menu_id`) VALUES
	 (4,2),
	 (4,8),
	 (5,1),
	 (5,8);