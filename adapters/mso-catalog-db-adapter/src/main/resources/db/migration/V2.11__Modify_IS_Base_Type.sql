USE catalogdb;

ALTER TABLE
  `vf_module` CHANGE COLUMN `IS_BASE` `IS_BASE` TINYINT(1) NOT NULL;