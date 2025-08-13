# YumProxy 开源部署与配置指南

本项目已被@柳家科技清算，官网https://www.yumproxy.top，以下是配置教程

项目使用：

1.本项目使用佛系云作为支付接口，官网https://888.seven-cloud.cn/

2.使用CloudFlare作为托管，使用CloudFlare Turnstile作为人机验证

## 一、后端配置

- 数据库（必配）
  - 环境变量或 `resources/database.properties`：
    - `db.url` 示例：`jdbc:mysql://localhost:3306/yumproxy?useSSL=false&serverTimezone=Asia/Shanghai`
    - `db.username` 示例：`your_db_user`
    - `db.password` 示例：`your_db_password`
  - 注意：生产不要在代码中硬编码连接、账号或密码。

- Cloudflare Turnstile（人机验证）
  - 文件：`后端部分/turnstile_config.properties`
    - `turnstile.secret_key=0xYOUR_TURNSTILE_SECRET_KEY`
    - `turnstile.site_key=0xYOUR_TURNSTILE_SITE_KEY`
  - 或环境变量：`TURNSTILE_SECRET_KEY`
  - 前端通过环境变量注入站点 Key：`REACT_APP_TURNSTILE_SITE_KEY`

- 易支付
  - 代码默认示例：`MerchantWebSocketApi`
    - `API_URL=https://your-epay-host/xpay/epay/submit.php`
    - `API_MAPI_URL=https://your-epay-host/xpay/epay/mapi.php`
    - `PID=YOUR_EPAY_PID`
    - `KEY=YOUR_EPAY_KEY`
  - 建议改为从配置文件或环境变量加载，不要硬编码。

- 邮件/SMTP（使用API接口作为发件）
  - 文件：`后端部分/src/main/java/yumProxy/net/Config/smtp.properties`
    - `MAIL_API_URL=http://localhost:5000/send-email`
    - `SMTP_USER=your_email@example.com`
    - `SMTP_PASS=example_password`
    - `SMTP_FROM_NAME=YourTeam`
  - 或通过 `service_config.properties` 设置：
    - `mail.api.url`
    - `smtp.user`
    - `smtp.pass`
    - `smtp.from.name`

## 二、前端配置

- Turnstile 站点 Key
  - `.env`（或构建环境变量）：
    - `REACT_APP_TURNSTILE_SITE_KEY=0xYOUR_TURNSTILE_SITE_KEY`

- API 网关/后端地址
  - 在 `src/services/api.js` 中配置你的后端地址或通过环境变量注入。

## 三、敏感信息与安全建议

- 禁止将真实密钥、密码、外网IP等提交到版本库。
- 开启密钥轮换与权限最小化；对支付与数据库账户启用最小权限。
- 生产环境关闭冗余调试日志；不要打印包含密码/验证码的请求体或参数。
- 用户密码务必使用哈希存储（推荐 Argon2 或 bcrypt），本仓库当前示例未实现哈希，请务必在生产启用前改造为哈希存储与校验。
- 前端不要在 `localStorage` 存储与密码相关的任何数据；推荐使用服务端签发的 HttpOnly/Secure Cookie。

## 四、示例与模板

- `后端部分/turnstile_config.properties` 使用示例占位。
- `后端部分/src/main/java/yumProxy/net/Config/smtp.properties.example` 提供SMTP配置模板。
- `后端部分/service_config.properties` 为示例默认配置，可按需修改。

## 五、.gitignore 建议

将以下文件加入 `.gitignore`（避免提交真实配置）：

- `**/service_config.properties`
- `**/smtp.properties`
- `**/database.properties`

## 六、部署步骤（概览）

1. 准备数据库与用户，并配置 `database.properties` 或环境变量。
2. 配置 Turnstile `secret_key`（后端）与 `site_key`（前端）。
3. 配置邮件服务（SMTP 或独立邮件API）。
4. 如使用支付，配置易支付 `API_URL`/`API_MAPI_URL`、`PID`、`KEY`。
5. 构建与运行后端、前端，并进行端到端测试。
