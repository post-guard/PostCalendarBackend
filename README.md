# 邮历

> 邮历，一路有你

北京邮电大学2021级数据结构课程设计大作业（邮历）后端仓库，
前端仓库在[post-guard/PostCalendarFrontend](https://github.com/post-guard/PostCalendarFrontend)。

本次大作业的要求为实现一个学生日程管理系统。

## 特点

- 自行实现的泛型数据结构
- `Docker`部署

## 开发

使用`Springboot + Maven`开发。

### 开发IDE

- `Jetbrains IDEA`

- `Eclipse`

### 配置数据库

使用`Mysql`作为后端使用的数据库，需要在`src/main/resources/application.yml`中配置数据连接字符串。

在运行程序之前需要使用`src/main/resources/database/schema.sql`初始化数据库。

### 打包Docker镜像

在项目根目录下运行：

```shell
docker build -t postcalendarbackend:<tag> .
```

## 支持

如果您在学习或者是抄袭的过程中发现了问题，我们十分欢迎您提出，您可以通过发起`issue`或者是发送电子邮件的方式联系我们。


