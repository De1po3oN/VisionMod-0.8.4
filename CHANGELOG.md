# 0.8.4 — 2026-11-12 — Low Fire startup crash

- **Fixed crash on game initialization:** `InvalidInjectionException` in `visionmod.mixins.json:LowFireMixin`.
- Corrected both `renderFire` injection callbacks to take `PoseStack`, `MultiBufferSource`, `TextureAtlasSprite`, `CallbackInfo` (Minecraft 1.21.11).
- Preserved low-fire setting, other modules, configs, and 51 keybinds.
- Bumped version and GitHub Actions artifact to 0.8.4.
- **Validation:** source syntax and archive integrity only; full Gradle build/game startup not confirmed.

---

# Changelog

## 0.8.3 — Исправления компиляции Minecraft 1.21.11
- `CompactHandMixin`: проверка `ItemTags.SWORDS` вместо отсутствующего `SwordItem`.
- `VisionExtras`: чтение брони из `EquipmentSlot.HEAD/CHEST/LEGS/FEET` через `getItemBySlot`.
- Сохранены 51 бинд, модули и настройки 0.8.2.
- Проверка Java-синтаксиса выполнена; полноценная сборка Fabric ещё не подтверждена.

## 0.8.2 — 51 назначаемое действие (исходники; запуск не подтверждён)

- Сохранены прежние 33 действия с теми же ID и расширены новые группы: 4 фильтра частиц, 3 пресета MAX FPS / BALANCED / VISUAL, 5 значений производительности, 3 переключателя адаптивной нагрузки, 3 команды для Zoom и цветов.
- Теперь MMB назначает клавишу на всех страницах главного меню, в том числе на двух страницах Performance и третьей странице Extras; в отдельном окне Particles тоже есть MMB-привязки.
- ЛКМ исполняет действие сразу, MMB ждёт новую клавишу/кнопку мыши, ESC во время назначения очищает привязку. При повторном назначении одной клавиши другому действию старая привязка удаляется.
- Free Look и Zoom остаются действиями на удержание, другие действия срабатывают один раз при нажатии.
- Импорт JSON-профилей поддерживает старые массивы из 33 привязок и новые из 51; бинды сохраняются в `config/visionmod/hotkeys.properties`.
- Не привязываются разрушительные/служебные действия Configs (удалить профиль, открыть папку, сохранить файл), а открытие меню / общее включение доступны для назначения в стандартных Controls Minecraft.
- Локальная проверка: 38 Java файлов успешно разобраны Java 21 parser без синтаксических ошибок; система биндов прошла отдельный тест с заменёнными игровыми зависимостями. Полная Gradle-сборка/запуск в Minecraft **не проверены**.

## 0.8.1 — UI overhaul, mouse bind capture, adaptive budget (source preview)

- New unified, neon 2D menu with independent pages drawn inside one window. Cosmetics remains hidden from navigation.
- MMB module key capture and persisted binds; Free Look / Zoom now use hold bindings, other modules toggle. Config JSON includes binds.
- Adaptive local throttling on busy spawns: ore scan, outlines, cosmetic range, remote cosmetics, ambient effects, name-tag hearts.
- Avoid unnecessary health arrays when hearts are disabled.
- Textual Shulker Tooltip with list of stored items (not graphical 27-slot grid).
- Main screens use custom drawing; secondary particle selector and crosshair/color pages use the shared frame.
- Required to finish from 21-task plan: sky, true resolution stretch, totem pop animation scaling, built-in JEI, first-person model. See PLAN_STATUS_RU.md.
- Java syntax checked; Fabric compilation and actual runtime unverified.

# Vision Mod — Changelog

## 0.8.0 — Planned features, part 1 (unverified source)

- Added `EXTRAS` page and 15 separately configurable optional enhancements: CPS, armor/food HUD, custom crosshair, zoom, fast hotbar scrolling, target block outline, snowflakes, floating hearts, shulker item count HUD, arrow-hit phrase test sound, compact sword/shield/totem and low fire overlay.
- Added 9×9 crosshair painter with profile-exportable bitmap and colour palette.
- Added per-feature persistence under `config/visionmod/extras.properties` and profile import/export support.
- Added original synthetic Russian placeholder voice for the arrow hit effect. The original clip has not been provided; packets may also represent hits by other shooters.
- Kept the menu Cosmetics-free, retained ore selection, performance presets, Free Look and existing modules.
- Bumped `build.gradle`, menu version and GitHub Actions workflow to `0.8.0`.
- NOT DONE: genuine custom sky, totem activation animation scaling, stretched resolution, JEI replica, full ShulkerBoxTooltip, native First-person Model; see `PLAN_STATUS_RU.md`.
- Validation: JSON + Java parser + ZIP integrity only. Fabric compilation, runtime, shader and server FPS not verified.

## 0.7.2 — Menu layout fixes

- Earlier release: common menu frame, consistent page positions, visual adjustments.
