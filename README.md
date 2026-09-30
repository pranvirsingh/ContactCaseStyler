# Contact Case Styler

One-tap styles for all your contact names. `Alex Morgan` → `alexMorgan`.

![CI](https://github.com/pranvirsingh/ContactCaseStyler/actions/workflows/ci.yml/badge.svg)
![Release](https://img.shields.io/github/v/release/pranvirsingh/ContactCaseStyler)
![License](https://img.shields.io/github/license/pranvirsingh/ContactCaseStyler)

| Before | camelCase | snake_case | Leet (fun) |
|---|---|---|---|
| Alex Morgan | alexMorgan | alex_morgan | 4l3x M0rg4n |
| Jordan Lee | jordanLee | jordan_lee | J0rd4n L33 |

## Styles

**Safe** (search keeps working): camelCase · PascalCase · snake_case · kebab-case · UPPER_SNAKE · Title Case · lowercase · UPPERCASE · Swap · Last, First · Initials · Spaced · Dotted · Alternating · Brackets · Stars

**Fun** (fancy Unicode textures — search may not match): Bold · Leet · Fullwidth · Circled · Fraktur · Script · Strike

Fun styles auto-save each original name as the contact's nickname, so
most dialers still find them. Backup + restore included regardless.

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
