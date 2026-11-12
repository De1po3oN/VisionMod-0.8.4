# Как собрать Vision Mod 0.8.2

**Версия исходников:** 0.8.2. Minecraft Fabric 1.21.11, Java 21, Fabric API 0.141.6+1.21.11, Loader 0.19.3+.

1. Распакуй архив **целиком**. Загрузи **содержимое**, а не сам ZIP, в GitHub-репозиторий.
2. Убедись, что в корне есть `build.gradle`, `settings.gradle`, `src/` и скрытая папка `.github/`.
3. Путь workflow: `.github/workflows/build.yml`.
4. GitHub → Actions → **Build Vision Mod 0.8.2** → Run workflow.
5. Если Gradle-сборка успешна, скачай артефакт `VisionMod-0.8.2` внизу страницы запуска.
6. Используй основной jar (не `sources.jar`, если он создаётся). Помести его в `mods` клиента с соответствующим Fabric Loader и Fabric API.

**Важно:** Java-синтаксис 38 исходных файлов и часть логики биндов проверены, но Minecraft runtime и реальный Gradle build не были запущены. Ошибка Actions означает, что нужен лог, а не изменение `build.yml` наугад.

Инструкция по горячим клавишам: `KEYBINDS_RU.md`.
