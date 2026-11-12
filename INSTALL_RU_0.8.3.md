# Установка и сборка Vision Mod 0.8.3

Версия Minecraft Java: 1.21.11; Java: 21; Fabric Loader >=0.19.3.

1. Открой репозиторий в GitHub.
2. Загрузи содержимое архива в корень репозитория. Не пропусти скрытую папку `.github/workflows/build.yml`.
3. Проверь `build.gradle`: `version = '0.8.3'`.
4. Перейди в GitHub Actions, выбери `Build Vision Mod 0.8.3`, запусти `Run workflow`.
5. После успешной сборки скачай артефакт `VisionMod-0.8.3`, внутри должен быть `.jar`.

Если Gradle снова завершится с ошибкой, пришли первые строки `error:` из блока `compileJava`, а не весь stacktrace.

Проверка компиляции в среде автора пакета не проводилась — не считай JAR собранным до успешного GitHub Actions.
