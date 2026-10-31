# 今天吃什么 Android APP — 开发交接文档

## 1. 项目背景与目标
用户希望 Android 手机上直接安装 APK 使用“今天吃什么”随机菜谱应用。默认 **两个人、三道菜加一道汤**，支持随机推荐与自由搭配；根据冰箱已有食材筛选候选菜谱，查看具体做法和食材用量；未来希望扩展网上公开可参考的菜谱内容（注意授权与来源）。

## 2. 当前交付状态（2026-10-31）
- 本压缩包为 **Android 源码包，不是 APK**。
- 已有最小原型：Kotlin + Jetpack Compose 单 Activity，10 道内置菜谱；食材选择、严格食材匹配开关、随机选三菜一汤、手动选菜、展开制作步骤。
- `.github/workflows/android.yml` 已写入自动构建配置，**尚未在 GitHub 实际执行成功**，不能声称 APK 已生成。
- 用户 GitHub 账号 `jauiry`，新建仓库 `jauiry/random-recipe-android`（main 分支，创建时为空）。ChatGPT GitHub 连接可读，但创建 README 时返回 `403 Resource not accessible by integration`；请在电脑端通过 Git CLI/浏览器登录上传，不要继续依赖该连接写入。
- 代码未经实际 Android 编译和真机验证。请先检查编译问题、布局溢出与随机菜单数量不足等情况。

## 3. 项目结构
```
random-recipe-android/
  README.md
  HANDOFF.md
  .gitignore
  settings.gradle.kts
  build.gradle.kts
  app/build.gradle.kts
  app/src/main/AndroidManifest.xml
  app/src/main/java/com/example/recipe/MainActivity.kt
  .github/workflows/android.yml
```
- Android Gradle Plugin 8.7.3 / Kotlin 2.0.21 / Compose BOM 2024.12.01 / Java 17 / compileSdk 35 / minSdk 24。
- GitHub Actions 使用 Ubuntu、Java 17、Gradle 8.9，执行 `gradle :app:assembleDebug --no-daemon`，上传 debug APK artifact。
- 当前无 Gradle Wrapper（`gradlew`）；本地需自行安装兼容 Gradle 或优先补齐 Wrapper。

## 4. 用户确认过的需求
- 两人份，三菜一汤。
- 两种模式都要：随机生成、自由搭配。
- 候选菜谱可浏览、选择，能展示详细做法。
- 菜谱量要显著增加，参考网上已有菜谱，但不得未经许可直接批量复制受版权保护的内容。
- 初始食材：后腿肉、鸡胸肉、鸡蛋、西红柿、鱼尾、太湖菜、南瓜、冬瓜、虾、蔬菜肠、豆角、火腿。
- 最终要在手机直接下载 APK 安装；尽量减少用户操作。

## 5. 当前代码与需求差距
1. 仅 10 道示例菜谱，没有联网菜谱库、搜索、收藏、分类、食材用量、份量换算、购物清单或本地持久化。
2. 菜谱中的步骤较简略，尚无用料克数、时间、火候等结构化字段。
3. `options.chunked(3)` + `Row` + `FilterChip` 在窄屏上可能横向溢出，建议 `FlowRow` 或可滚动布局。
4. 随机按钮在可用菜不足时可能产生少于三菜一汤的菜单，应显示缺少食材或不足提示并提供替代方案。
5. 手动选菜使用替换最早同类菜的逻辑，尚无删除、固定菜品、重新随机未锁定项等交互。
6. 目前为 debug APK，不适合作为正式发布签名包；后续需要版本管理、发布签名与 Release。

## 6. 电脑端 ChatGPT / 开发 Agent 的执行任务（按顺序）
**阶段 A：仓库与构建**
1. 解压本 ZIP；检查所有源码文件；先运行 `gradle :app:assembleDebug` 或 Android Studio 构建，修复编译错误。
2. 添加 Gradle Wrapper（8.9）及 `gradle/wrapper/gradle-wrapper.jar`，修改 CI 优先用 `./gradlew :app:assembleDebug`；确认权限和校验。
3. `git init -b main`；`git add .`；`git commit -m "feat: initial recipe Android app"`；`git remote add origin https://github.com/jauiry/random-recipe-android.git`；`git push -u origin main`。**如仓库已有提交，先 fetch/pull 核对，禁止 force push。**
4. 在 GitHub Actions 查看 Build Android APK 是否成功；失败则根据 logs 修复并重新提交。成功后下载 `random-recipe-debug-apk` artifact，确认内部包含 `app-debug.apk`。
5. 如用户手机安装失败，检查 Android 版本、包签名、未知来源安装权限和实际崩溃日志。

**阶段 B：可用性**
6. 修复窄屏布局、空状态、三菜一汤数量保障、选择/移除菜单交互；测试食材筛选与随机生成。
7. 建立结构化 `Recipe` 数据模型（id、名称、菜/汤、原料及克数、两人份、步骤、耗时、口味、标签、来源/许可）；扩充至至少 50 道真实可做的原创改写菜谱并校验食材和做法。
8. 引入 Room 或 DataStore 保存食材、菜单、收藏；支持无网运行。

**阶段 C：增强与发布**
9. 增加搜索、缺失食材提示、购物清单、食材替换、菜谱来源说明、手动锁定与重新随机。
10. 真机验证，发布带版本号的 APK；正式签名密钥不得提交仓库。

## 7. 验收标准
- `assembleDebug` 通过，GitHub Actions 状态 success，能下载真实 APK。
- 安卓手机可安装、启动、操作，无明显崩溃和遮挡。
- 全部食材齐全时，随机菜单恰好 3 道菜 + 1 道汤，且不重复。
- 食材不足时提示明确，不静默返回不完整菜单。
- 手动选菜后可替换或移除，最终保持菜单分类上限。
- 菜谱展示可执行的两人份用量和分步做法。

## 8. 给电脑端 ChatGPT 的可复制任务指令
请读取项目根目录 `HANDOFF.md`、`README.md` 和全部源码。你是 Android 工程师与测试工程师。首先**不要重写项目**，优先构建验证并修复编译错误，补齐 Gradle Wrapper，推送到 GitHub 仓库 `jauiry/random-recipe-android`，运行 GitHub Actions 并提供实际 APK 下载方式。不要声称成功，除非有真实构建日志与产物。每一步汇报已完成/失败/待办；推送前检查远程仓库现状，禁止覆盖已有提交。之后再按交接文档阶段 B/C 迭代功能。
