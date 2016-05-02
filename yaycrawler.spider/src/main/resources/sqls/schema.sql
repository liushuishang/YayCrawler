CREATE SCHEMA `yaycrawler`
  DEFAULT CHARACTER SET utf8;



CREATE TABLE IF NOT EXISTS `conf_page_region` (
  `id`          VARCHAR(38)  NOT NULL DEFAULT '编号',
  `name`        VARCHAR(50)  NULL     DEFAULT '区域的名称',
  `pageUrl`     VARCHAR(255) NOT NULL DEFAULT '页面的Url',
  `cssSelector` VARCHAR(100) NULL     DEFAULT '区域的CSS选择权',
  `createdDate` TIMESTAMP             DEFAULT now(),
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  COMMENT = '表示一个页面包含的解析区域单元';

CREATE TABLE IF NOT EXISTS `conf_field_rule` (
  `id`          VARCHAR(38)  NOT NULL,
  `fieldName`   VARCHAR(50)  NOT NULL,
  `rule`        VARCHAR(100) NOT NULL,
  `valueType`   VARCHAR(20)  NULL DEFAULT 'string',
  `regionId`    VARCHAR(38)  NULL,
  `createdDate` TIMESTAMP         DEFAULT now(),
  PRIMARY KEY (`id`),
  INDEX `field_ref_region_idx` (`regionId` ASC),
  CONSTRAINT `field_ref_region`
  FOREIGN KEY (`regionId`)
  REFERENCES `conf_page_region` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS `conf_url_rule` (
  `id`          VARCHAR(38)  NOT NULL,
  `rule`        VARCHAR(100) NOT NULL,
  `method`      VARCHAR(10)  NULL DEFAULT 'get',
  `regionId`    VARCHAR(38)  NULL,
  `createdDate` TIMESTAMP         DEFAULT now(),
  PRIMARY KEY (`id`),
  INDEX `url_ref_region_idx` (`regionId` ASC),
  CONSTRAINT `url_ref_region`
  FOREIGN KEY (`regionId`)
  REFERENCES `conf_page_region` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);