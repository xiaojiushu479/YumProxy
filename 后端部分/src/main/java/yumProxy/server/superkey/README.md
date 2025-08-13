# 超级密钥管理系统

## 概述
超级密钥管理系统提供了基于时间戳的动态超级密钥生成、验证和管理功能。**现在系统只使用超级密钥进行权限验证，不再需要管理员账号。所有配置都直接集成在SuperKeyGenerator中，无需外部配置文件。**

## 文件结构
```
superkey/
├── SuperKeyGenerator.java        # 超级密钥生成器（集成所有配置）
├── SuperKeyTest.java           # 测试工具
├── TimeBasedSuperKey.java      # 基于时间戳的密钥生成器
├── TimeBasedSuperKeyTest.java  # 时间戳密钥测试工具
└── README.md                  # 使用说明
```

## 功能特性

### 1. 超级密钥权限验证
- **基于时间戳的动态密钥**: 每5分钟自动更新
- **每日变化密钥**: 基于日期的固定密钥
- **随机生成密钥**: 完全随机的密钥
- **无需外部配置文件**: 所有配置都集成在代码中
- **无需管理员账号**: 系统现在只验证超级密钥

### 2. 安全特性
- 基于时间戳的动态密钥生成
- SHA-256哈希加密
- 64位随机密钥生成
- 密钥格式验证
- 时间窗口管理（5分钟）
- 容错机制（支持前一个时间窗口）

### 3. 管理工具
- 密钥生成（多种方式）
- 密钥验证
- 当前密钥显示
- 命令行工具

## 使用方法

### 命令行工具
```bash
# 显示当前超级密钥信息
java yumProxy.server.superkey.SuperKeyGenerator

# 生成每日密钥
java yumProxy.server.superkey.SuperKeyGenerator daily

# 生成时间戳密钥
java yumProxy.server.superkey.SuperKeyGenerator time

# 生成随机密钥
java yumProxy.server.superkey.SuperKeyGenerator random

# 验证密钥
java yumProxy.server.superkey.SuperKeyGenerator validate YOUR_KEY_HERE

# 显示详细信息
java yumProxy.server.superkey.SuperKeyGenerator info
```

### 测试工具
```bash
# 运行完整测试
java yumProxy.server.superkey.SuperKeyTest

# 运行时间戳密钥测试
java yumProxy.server.superkey.TimeBasedSuperKeyTest
```

### API使用
```java
// 获取当前推荐使用的超级密钥（时间戳密钥）
String currentKey = SuperKeyGenerator.getCurrentSuperKey();

// 生成每日密钥
String dailyKey = SuperKeyGenerator.generateSuperKey();

// 生成时间戳密钥
String timeBasedKey = SuperKeyGenerator.generateTimeBasedSuperKey();

// 生成随机密钥
String randomKey = SuperKeyGenerator.generateRandomSuperKey();

// 验证密钥格式
boolean isValid = SuperKeyGenerator.isValidSuperKey(key);

// 验证时间戳密钥
boolean isValidTimeKey = SuperKeyGenerator.validateTimeBasedSuperKey(key);

// 显示当前密钥信息
SuperKeyGenerator.showCurrentKeyInfo();
```

## 密钥类型

### 1. 时间戳密钥（推荐）
- **生成方式**: 基于当前时间窗口（5分钟）
- **更新频率**: 每5分钟自动更新
- **安全性**: 最高
- **使用场景**: 生产环境推荐使用

### 2. 每日密钥
- **生成方式**: 基于当前日期
- **更新频率**: 每日更新
- **安全性**: 中等
- **使用场景**: 备用方案

### 3. 随机密钥
- **生成方式**: 完全随机生成
- **更新频率**: 每次调用都不同
- **安全性**: 高
- **使用场景**: 一次性使用

## API权限验证

### 需要超级密钥的操作
- **用户管理**: 删除用户、获取用户信息
- **卡密管理**: 创建、查询、删除卡密
- **时间戳管理**: 激活、续期、停用、删除时间戳
- **系统维护**: 时间戳修复

### 无需权限的操作
- **用户注册/登录**: 普通用户操作
- **卡密使用**: 用户激活卡密
- **时间戳查询**: 查询自己的时间戳状态

### 使用示例
```bash
# 获取用户信息（需要超级密钥）
curl -X POST http://localhost:5000/api/user \
  -H "Content-Type: application/json" \
  -H "user: GetInfo" \
  -H "super_key: 当前时间戳密钥" \
  -d '{"target_username": "user123"}'

# 创建卡密（需要超级密钥）
curl -X POST http://localhost:5000/api/key/create \
  -H "Content-Type: application/json" \
  -H "super_key: 当前时间戳密钥" \
  -d '{
    "prefix": "TEST",
    "count": "5",
    "time": "24"
  }'

# 激活时间戳（需要超级密钥）
curl -X POST http://localhost:5000/api/timestamp \
  -H "Content-Type: application/json" \
  -H "action: Activate" \
  -H "super_key: 当前时间戳密钥" \
  -d '{
    "username": "user123",
    "hours": "24"
  }'
```

## 内置配置

所有配置都直接集成在`SuperKeyGenerator`类中：

```java
// 密钥种子
private static final String SECRET_SEED = "YUMPROXY_SUPER_KEY_2024_固定盐值";
private static final String TIME_BASED_SECRET = "YUMPROXY_TIME_BASED_SECRET_2024";

// 时间窗口设置
private static final int TIME_WINDOW = 300; // 5分钟

// 密钥设置
private static final int KEY_LENGTH = 64;
private static final String KEY_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
```

## 配置优先级
1. **时间戳密钥** (最高优先级) - 每5分钟更新
2. **每日密钥** - 每日更新
3. **随机密钥** - 每次生成都不同

## 安全建议
1. **使用时间戳密钥**（推荐）- 每5分钟自动更新
2. **定期更换密钥种子** - 修改代码中的SECRET值
3. **使用强随机生成的密钥** - 用于一次性操作
4. **不要在代码中硬编码密钥** - 使用生成器
5. **监控密钥使用日志** - 记录验证尝试
6. **设置合适的时间窗口** - 默认5分钟，可根据需要调整

## 注意事项
- **无需外部配置文件** - 所有配置都集成在代码中
- 时间戳密钥每5分钟自动更新
- 密钥长度固定为64位
- 密钥只能包含字母和数字
- 支持前一个时间窗口的容错验证
- **系统现在只验证超级密钥，不再需要管理员账号**
- 修改密钥种子后需要重新编译和部署应用程序 