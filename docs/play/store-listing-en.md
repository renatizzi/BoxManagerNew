# BoxManager — Play Store listing (English)

**Status:** M0.5 draft for **M3 cutover** (classe I → ufficiale 1.3).  
**Package:** `it.renatizzi.boxmanager`  
**Default language on store:** keep Italian as primary; add English as additional language when uploading.  
**Fonte:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](../famiglia/PROMPT_CONTINUITA_M3_MERGE_PLAY.md) §2.3 (CONVALIDATO 10/09/2026).

---

## Short description (≤ 80 characters)

Organize containers and objects. Find anything with a simple question.

## Full description

BoxManager helps you remember where you put things.

Create containers, assign categories and locations, and list the objects inside each one. Use the camera to scan a QR label on a container and open it immediately—even in a basement or garage.

Simple search filters the current list as you type. Advanced archive search (trial / premium) understands natural-language questions in Italian or English, such as “Where is the drill?” or “Find container box”, without translating the names you already entered in your archive.

Choose the app language in Settings (Italian or English). Your object and container names stay exactly as you write them—BoxManager does not auto-translate your data.

Backup and restore your archive, import and export CSV, and print or share a view. Optional archive sharing (Full Archive) lets people in the same household keep catalogs and inventories aligned. With Full Archive you can also save Backup and CSV files to a network folder (CIFS / mapped drive) when your device can reach it.

Designed for personal and household use.

## Release notes template (English) — M3 / 1.3

What’s new in this release:

- App language: Italian and English (Settings)
- Advanced search understands English questions (same archive pipeline as Italian)
- Shared household archive
- Network disk (CIFS) for Backup / CSV — with Full Archive
- Bug fixes and stability improvements

## Notes for Console

- App name: **BoxManager** (same product on development and Play installs)
- Feature graphic / icon / screenshots: see `docs/play/README.md`
- At least one screenshot should show English UI or Settings → language (no personal data)
- **Do not** use Play Console auto-translate instead of in-app `values-en` / EN search tables (C12)
- **Do not** publish the development (`famiglia` / `.famiglia`) build (C8)
- Tester code if track stays closed: `BOXMANAGER-TESTER`
