# 今天吃什么 🍳

Android Kotlin + Jetpack Compose 随机菜谱原型。默认两人份、三菜一汤。

请先阅读 [HANDOFF.md](HANDOFF.md) 了解现状、构建、GitHub 上传和验收要求。

## 构建

本地使用 Java 17 和 Android SDK 35：

```bash
./gradlew :app:assembleDebug
```

推送到 `main` 后，GitHub Actions 会构建并上传名为 `random-recipe-debug-apk` 的 APK artifact。
