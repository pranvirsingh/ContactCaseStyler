# Contact Case Styler

One-tap styles for all your contact names. `Pranvir Singh` → `pranvirSingh`.

![CI](https://github.com/pranvirsingh/ContactCaseStyler/actions/workflows/ci.yml/badge.svg)
![Release](https://img.shields.io/github/v/release/pranvirsingh/ContactCaseStyler)
![License](https://img.shields.io/github/license/pranvirsingh/ContactCaseStyler)

| Before | camelCase | snake_case | Bold |
|---|---|---|---|
| Pranvir Singh | pranvirSingh | pranvir_singh | 𝐏𝐫𝐚𝐧𝐯𝐢𝐫 𝐒𝐢𝐧𝐠𝐡 |
| Anaya Sharma | anayaSharma | anaya_sharma | 𝐀𝐧𝐚𝐲𝐚 𝐒𝐡𝐚𝐫𝐦𝐚 |

## Styles

camelCase · PascalCase · snake_case · kebab-case · UPPER_SNAKE · Title Case · lowercase · UPPERCASE · Bold

## Install

1. Download the APK from [Releases](https://github.com/pranvirsingh/ContactCaseStyler/releases).
2. Open it, grant Contacts permission.
3. Pick a style, preview, **Backup**, then Apply.

Backup first — Apply rewrites real contact names (that's why dialer and WhatsApp show the new style too). Restore brings everything back in one tap.

## Feels like a system tweak, not an app

- Lives in the **Quick Settings** shade as a tile.
- Optional **Hide icon** switch removes it from the launcher (it stays in Settings → Apps — Android allows nothing less without root).

## Build

Android Studio → open this folder → Run. Or with Gradle + Android SDK:

```bash
gradle :app:assembleDebug
```

APK lands in `app/build/outputs/apk/debug/`.

## Privacy

100% on-device. No account, no network, no analytics. Contacts never leave your phone.

## Docs

- [docs/why.md](docs/why.md) — why this exists
- [docs/status.md](docs/status.md) — what's shipped, what's next
- [docs/shipping.md](docs/shipping.md) — how versions ship

## License

MIT — see [LICENSE](LICENSE).
