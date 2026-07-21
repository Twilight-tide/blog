## 快速启动

### 1. 环境要求
- JDK 21+
- MySQL 8.0+
- Maven 3.8+

### 2. 创建数据库
```sql
CREATE DATABASE blog_db CHARACTER SET utf8mb4;
```

### 3. 配置数据库密码

**方式一：修改配置文件**

编辑 `src/main/resources/application.yml`，替换密码：

```yaml
spring:
  datasource:
    password: 你的MySQL密码   # ← 改成自己的
```

**方式二：使用环境变量（推荐）**

```bash
export MYSQL_PASSWORD=你的密码
mvn spring-boot:run
```

### 4. 启动项目
```bash
mvn spring-boot:run
```