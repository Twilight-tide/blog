# 数据库部署

## 首次部署（空服务器）

应用使用 `ddl-auto: validate`，**不会自动建表**——缺表会直接启动失败。所以先导入表结构：

```bash
mysql -u root -p < db/schema.sql
```

schema 已包含 `CREATE DATABASE IF NOT EXISTS blog_db`，导入后即有 `article`、`comment`、`user` 三张表（含 `user.role`、`user.enabled`、`user.remark`、`user.last_login_time`）。

## 已有数据库升级（角色体系 v2）

如果数据库是旧版本（`user` 表还没有 `enabled` / `remark` / `last_login_time`），执行一次升级脚本：

```bash
mysql -u root -p < db/upgrade-v2-roles.sql
```

它会给 `user` 表补上账号管理所需字段，并把历史上发过文章的用户自动升级为 `AUTHOR`。**只执行一次**，全新安装不需要。

## 角色说明

| 角色 | 能做什么 |
|---|---|
| `USER` | 注册即得。浏览文章、发表评论、删除自己的评论 |
| `AUTHOR` | 由管理员在后台授予。额外可发布文章、编辑/删除自己的文章、进入作者中心 |
| `ADMIN` | 站长。最高权限：管理所有账号、编辑/下架/删除任意文章与评论、进入后台 |

发文章**不靠注册**，必须由管理员在后台「账号管理」里把用户角色改为「作者」。

## 设置管理员

注册账号后，把你要作为站长的账号提升为 `ADMIN`：

```sql
UPDATE user SET role = 'ADMIN' WHERE username = '你的用户名';
```

也可以直接改数据库。注意系统会阻止「把自己降级」和「封禁自己」，并且必须至少保留一名管理员。

## 生产环境变量

| 变量 | 必填 | 说明 |
|---|---|---|
| `MYSQL_PASSWORD` | ✅ | 数据库密码 |
| `JWT_SECRET` | ✅ | JWT 签名密钥，**至少 32 字节随机串**，泄露即等于账号可被伪造 |
| `SPRING_DATASOURCE_URL` | 可选 | 默认 `jdbc:mysql://localhost:3306/blog_db?...` |
| `SPRING_DATASOURCE_USERNAME` | 可选 | 默认 `root` |
| `SPRING_PROFILES_ACTIVE` | 可选 | 本地开发用 `local`（读取 `application-local.yml` 里的密钥） |

> `JWT_SECRET` 与 `MYSQL_PASSWORD` 建议通过服务器环境变量或 systemd 的 `Environment=` 注入，**不要写进仓库文件**。
> 本地开发在项目根目录建 `application-local.yml`（已被 `.gitignore` 忽略）+ `SPRING_PROFILES_ACTIVE=local`。

## 启动

```bash
java -jar blog-0.0.1-SNAPSHOT.jar
```

默认监听 8080。建议前置 Nginx/Caddy 做 HTTPS 与反向代理。

## 表结构变更流程

因为 `ddl-auto: validate`：

1. 修改 `db/schema.sql`（或在库里执行 `ALTER TABLE`）
2. 在数据库执行变更
3. 再修改实体类
4. 重新打包启动（validate 会校验一致性，不一致会启动失败——这是有意为之）

## 常见问题

| 现象 | 原因 |
|---|---|
| 启动报 `Schema-validation: missing table` | 忘了先导入 `db/schema.sql` |
| 启动报 `Could not resolve placeholder 'MYSQL_PASSWORD'` | 环境变量没注入 |
| 启动报 `Schema-validation: wrong column type` | 实体与库表类型不一致，按第 4 步检查 |
