# 🏃 校园闪电侠 · 校园跑腿平台

基于 Spring Boot 微服务架构的校园跑腿服务平台。

## ✨ 核心功能

- 用户注册/登录
- 任务发布 / 接单 / 送达 / 完成
- 跑腿员申请 + 人脸录入
- 钱包充值（支付宝沙箱）
- 钱包提现
- 个人中心（头像上传、信息编辑）
- 管理后台（仪表盘、用户管理、跑腿员审核）

## 🛠️ 技术栈

| 层级 | 技术 |
|:---|:---|
| 后端 | Spring Boot 2.2.5 + MyBatis |
| 数据库 | MySQL 8.0 + Redis + MongoDB |
| 文件存储 | FastDFS + 阿里云 OSS |
| 前端 | HTML + CSS + JavaScript |
| 第三方 | 高德地图 API、支付宝沙箱支付 |

## 🏗️ 微服务架构

| 服务 | 端口 | 说明 |
|:---|:---|:---|
| service-user | 8003 | 用户服务 |
| service-task | 8001 | 任务服务 |
| service-wallet | 8007 | 钱包服务 |
| service-admin | 8006 | 管理后台 |
| service-files | 8004 | 文件服务 |

## 📄 默认账号

| 角色 | 账号 | 密码 |
|:---|:---|:---|
| 管理员 | admin | 123456 |
| 普通用户 | 13234567890 | 111111 |

## 👤 作者

- GitHub: [worship114514](https://github.com/worship114514)

## 📝 License

MIT
