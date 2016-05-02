delete from conf_page_region;
DELETE  from conf_field_rule;
delete from conf_url_rule;


INSERT INTO `conf_page_region`
(`id`,
`name`,
`pageUrl`,
`cssSelector`,
`createdDate`)
VALUES
('4238905893045-423423-4234',
'楼盘列表',
'http://floor.0731fdc.com/jggs.php',
'.list-con',
now());

INSERT INTO `conf_page_region`
(`id`,
`name`,
`pageUrl`,
`cssSelector`,
`createdDate`)
VALUES
(
'4238905893045-423423-4235',
'分页区域',
'http://floor.0731fdc.com/jggs.php',
'.pageno',
now());


INSERT INTO `conf_field_rule`
(`id`,
`fieldName`,
`rule`,
`regionId`,
`createdDate`
)
VALUES
(
uuid(),
'floorName',
"xpath(li[@class='floorname']/a/text()).get()",
'4238905893045-423423-4234',
now());

INSERT INTO `conf_field_rule`
(`id`,
`fieldName`,
`rule`,
`regionId`,
`createdDate`
)
VALUES
(
uuid(),
'address',
'xpath(dl/dd[1]/text()).get()',
'4238905893045-423423-4234',
now());

INSERT INTO `conf_url_rule`
(`id`,
`rule`,
`regionId`,
`createdDate`)
VALUES
(
uuid(),
'links().regex(.*info.php?.*).all()',
'4238905893045-423423-4234',
now());

INSERT INTO `conf_url_rule`
(`id`,
`rule`,
`regionId`,
`createdDate`)
VALUES
(
uuid(),
'links().regex(.*dxx.php?.*).all()',
'4238905893045-423423-4234',
now());

INSERT INTO `conf_url_rule`
(`id`,
`rule`,
`regionId`,
`createdDate`)
VALUES
(
uuid(),
'links().all()',
'4238905893045-423423-4235',
now());

