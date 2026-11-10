# Release parameters (release-seal)

The generic steps live in the global `release-seal` skill. This file only records the parameters and exceptions for this project.

## Version

- This is a demo app, not a library: there is no JitPack coordinate and no `maven-publish`.
- Semantic versioning `X.Y.Z`. Bump `versionName` (and `versionCode` by 1) in `app/build.gradle` in the release commit; also update the "download sample APK" link in `README.md` and `README-CN.md`.

## Pre-release checks

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

Also verify on a real device or a large-screen emulator: launching the other page in the adjacent window, cross-window text drag and drop, picture-in-picture, and freeform windows.

## Tag

- Bare version tag without a `v` prefix: `2.0.0`. Annotated, message `MultiWindow X.Y.Z`, created on the release commit and pushed to `origin`.

## GitHub Release

- Title is the version number; notes in English.
- Attachment: `MultiWindow-sample-X.Y.Z.apk`, built with `./gradlew :app:assembleDebug` (output `app/build/outputs/apk/debug/app-debug.apk`) and signed with the local debug certificate so users can install over an older release.

## Afterwards

- No product doc or ROADMAP; the GitHub Release is the source of truth.
