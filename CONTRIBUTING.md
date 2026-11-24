# Contributing to Lucid

Thanks for wanting to help. Lucid is a small, Android-only YouTube Music client, and the goal is to
keep it light, smooth and simple. Bug reports, fixes and focused improvements are welcome.

## Before you start

- **Search first.** Check the [issues](https://github.com/mahie-cs/Lucid/issues) for your bug or idea.
- **Open an issue for anything big.** A new feature, a new dependency or a change to how the app
  looks is worth a short discussion before you write code.
- **Keep it lean.** New dependencies, native libraries and background work all cost APK size and
  battery. A change that makes the app noticeably bigger or slower needs a good reason.
- **Android only.** Lucid does not target desktop or iOS.

## Reporting bugs

Use the bug report template and include:

- what you did, what you expected and what happened
- your Android version and phone model, and the Lucid version (Settings → About)
- the app log if it crashed (Settings → About → App log). Remove anything private first.

## Building

You need JDK 21 and the Android SDK (Platform 37). Open the project in a recent Android Studio, or:

```bash
./gradlew :androidApp:assembleDebug
```

Debug builds are much slower than release builds. Before reporting a performance problem, check it
on `./gradlew :androidApp:assembleRelease`.

## Making a change

1. Fork the repo and create a branch from the default branch, named for what it does (`fix-queue-crash`).
2. Make one focused change per pull request. Unrelated clean-ups belong in their own PR.
3. Make sure the app builds, and try your change on a real device or emulator.
4. Open a pull request that says what changed and why. Add screenshots for anything visual.

## Code style

The rules are in `.editorconfig`: UTF-8, LF line endings, 4-space indents and a 150-column limit.
Follow the style of the code around your change, and prefer small functions with a short comment
where the reason isn't obvious. Don't add wildcard imports to files that don't already use them.

## Strings and translations

User-facing text lives in `composeApp/src/commonMain/composeResources/values/strings.xml`. Add new
strings there rather than hard-coding them in Kotlin. Translations are welcome as the matching
`values-xx/strings.xml`.

## Licensing of contributions

Lucid is licensed under the [GNU General Public License v3.0](LICENSE). By submitting a pull
request you agree that your contribution is released under the same license, and that you have the
right to contribute it. Please keep existing copyright notices and credits intact, and don't copy in
code that isn't GPLv3-compatible.

## Conduct

Be kind and assume good faith. Disagree about code, not about people.
