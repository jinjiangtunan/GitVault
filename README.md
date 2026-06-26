# GitVault

> Git 仓库移动端阅读器 — Obsidian 风格暗色主题 · 纯原生 Android

[![Android](https://img.shields.io/badge/Android-10%2B-green?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.05-purple?logo=jetpackcompose)](https://developer.android.com/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## 简介

GitVault 是一个 Android 原生应用，用于快速拉取和阅读 Git 仓库中的 Markdown 文档。界面风格致敬 Obsidian 暗色主题，支持 HTTPS + Personal Access Token 认证私有仓库。

### 核心功能

| 功能 | 状态 |
|------|:--:|
| 添加远程仓库并浅克隆到本地 | ✅ |
| 支持 HTTPS + Access Token 认证（GitHub / Gitee） | ✅ |
| 一键 Pull 拉取最新内容 | ✅ |
| 仓库文件树浏览 | ✅ |
| Markdown 渲染阅读（含语法高亮） | ✅ |
| 多令牌管理 | ✅ |
| SSH 密钥认证 | 🔜 后续 |
| Git LFS 大文件支持 | 🔜 后续 |
| 文档内图片渲染 | ✅ |
| 代码块语法高亮（28+ 语言） | ✅ |

## 技术栈

| 层 | 技术 |
|----|------|
| 语言 | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Hilt DI |
| 持久化 | Room |
| Git 操作 | JGit 6.10 |
| Markdown 渲染 | Markwon + Prism4j |
| 图片加载 | Coil |
| 安全存储 | EncryptedSharedPreferences（规划中） |

## 项目结构

```
GitVault/
├── app/src/main/java/com/gitvault/app/
│   ├── GitVaultApp.kt              # Application 入口
│   ├── MainActivity.kt             # 单 Activity
│   ├── di/DatabaseModule.kt        # Hilt DI 模块
│   ├── navigation/
│   │   ├── Routes.kt               # 路由常量
│   │   └── NavGraph.kt             # Compose 导航图
│   ├── ui/theme/
│   │   ├── Color.kt                # Obsidian 暗色配色
│   │   ├── Type.kt                 # 字体排版
│   │   └── Theme.kt                # Material 3 主题
│   ├── ui/screens/
│   │   ├── RepoListScreen.kt       # 仓库列表首页
│   │   ├── AddRepoScreen.kt        # 添加仓库表单
│   │   ├── FileBrowserScreen.kt    # 文件树浏览
│   │   ├── MarkdownReaderScreen.kt # Markdown 阅读器
│   │   └── SettingsScreen.kt       # 令牌管理
│   ├── ui/components/
│   │   ├── RepoCard.kt             # 仓库卡片
│   │   ├── FileTreeItem.kt         # 可折叠文件树项
│   │   └── LoadingDialog.kt        # 加载进度弹窗
│   ├── data/model/                 # Room Entity
│   ├── data/db/                    # Room DAO + Database
│   ├── data/repository/            # Repository 层
│   └── domain/
│       ├── GitManager.kt           # JGit clone/pull 封装
│       └── FileExplorer.kt         # 文件树构建 + 读取
```

## 构建

### 环境要求

- Android Studio Hedgehog (2023.1) 或更高
- JDK 17
- Android SDK 34
- Gradle 8.7（项目自带 wrapper）

### 步骤

```bash
# 1. 克隆项目
git clone https://github.com/your-username/GitVault.git
cd GitVault

# 2. 用 Android Studio 打开项目根目录
# 3. Sync Gradle → Run on device/emulator

# 或命令行构建
./gradlew assembleDebug
# APK 输出: app/build/outputs/apk/debug/app-debug.apk
```

## 使用指南

### 添加公开仓库

1. 打开 GitVault → 点击右下角 **+**
2. 填写仓库名称和 HTTPS URL（如 `https://github.com/user/repo.git`）
3. 点击「克隆仓库」

### 添加私有仓库

1. 先在「设置」中添加 Access Token：
   - GitHub: Settings → Developer settings → Personal access tokens → 勾选 `repo` 权限
   - Gitee: 设置 → 私人令牌 → 勾选 `projects` 权限
2. 回到首页 → 添加仓库 → 选择刚添加的令牌 → 克隆

### 阅读文档

1. 首页点击仓库卡片 → 进入文件浏览
2. 展开目录树，点击 `.md` 文件 → 进入阅读器
3. 阅读器支持 Markdown 完整渲染 + 代码语法高亮

### 更新仓库

首页卡片点击「拉取」按钮，自动执行 `git pull`。

## 路线图

- [ ] SSH 密钥认证
- [ ] Git LFS 大文件支持
- [ ] 全文搜索
- [ ] 多标签页同时打开
- [ ] WikiLink `[[双向链接]]` 跳转
- [ ] 离线阅读列表
- [ ] 暗色/亮色主题切换

## License

MIT © 2026
