# Установка Vision Mod 0.8.0 — исходный код

1. Распаковать ZIP в пустую папку на компьютере.
2. Открыть созданный репозиторий GitHub De1po3oN/Visionmod-0.6.0.
3. Загрузить **содержимое папки** (src, .github, build.gradle, gradle.properties, settings.gradle и документацию) в корень репозитория. Загрузки ZIP как одного файла недостаточно.
4. Проверить `.github/workflows/build.yml` и запустить GitHub Actions. Сборка начнёт компиляцию проекта.
5. Зелёный статус означает успешную компиляцию конкретного коммита; только тогда скачать Artifacts / VisionMod-0.8.0.
6. Установить готовый `.jar` в папку `mods` для Minecraft Fabric 1.21.11; удалить старые `visionmod-*.jar`.
7. В меню игры нажать Right Shift → EXTRAS / HUD / ZOOM. Zoom — клавиша C; Free Look — Left Alt.
8. Конфиги хранятся в `config/visionmod/profiles/`; папку открывает кнопка Configs.

**ВНИМАНИЕ:** код не проходил реальную Gradle-сборку и запуск Minecraft в этой среде. Если GitHub Actions покажет ошибку, нужен лог `:compileJava` для адресного исправления, а не замена build.yml.
