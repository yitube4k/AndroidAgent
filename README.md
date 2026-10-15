# AndroidAgent

Autonomous Android App Builder Agent.

- **Brain**: DeepSeek-R1-Distill-Qwen-14B (abliterated) running offline on Google Colab T4
- **Builder**: GitHub Actions (JDK 17 + Gradle 8.7 + AGP 8.2.2)
- **Tester**: Android emulator (API 29) install + launch + crash detection via logcat
- **Loop**: generate code -> push -> CI build -> emulator test -> AI fixes errors -> re-push -> final APK

Every push to `main` triggers the full build & test pipeline.
Artifacts: `app-debug-apk`, `build-log`, `test-evidence` (logcat + screenshot).
