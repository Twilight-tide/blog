-- ============================================================
-- 升级脚本：角色体系 v2（ADMIN / AUTHOR / USER）
--
-- 用途：给已经存在的 blog_db 补上账号管理所需字段，
--       并把已经发过文章的老用户升级为 AUTHOR。
-- 注意：只执行一次；全新安装直接用 schema.sql 即可，不需要本脚本。
-- ============================================================

USE `blog_db`;

-- 1. user 表新增：启用状态、管理员备注、最近登录时间
ALTER TABLE `user`
  ADD COLUMN `enabled` bit(1) NOT NULL DEFAULT b'1',
  ADD COLUMN `remark` varchar(200) DEFAULT NULL,
  ADD COLUMN `last_login_time` datetime(6) DEFAULT NULL;

-- 2. 历史上已经发过文章的用户升级为「授权作者」
UPDATE `user` u
SET u.`role` = 'AUTHOR'
WHERE u.`role` = 'USER'
  AND EXISTS (SELECT 1 FROM `article` a WHERE a.`author_id` = u.`id`);

-- 3. 确认结果
SELECT `id`, `username`, `role`, `enabled` FROM `user` ORDER BY `id`;
