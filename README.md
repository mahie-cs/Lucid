<div align="center">

# Lucid

**A small, native YouTube Music client for Android. Kotlin + Jetpack Compose, ad-free.**

<p align="center">
  <img alt="License" src="https://img.shields.io/github/license/mahie-cs/Lucid?style=for-the-badge&color=1881cc">
  <br>
  <img alt="Android 8.0+" src="https://img.shields.io/badge/Android_8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white">
</p>

</div>

Lucid talks directly to YouTube's internal API and plays through Media3: no backend server of its
own, no ads in the audio. It is built for Android only, and it aims to stay light and quiet: one
player, two themes, and the features you actually reach for.

> **Status:** version 1.0.0 is in development. The first release will appear under
> [Releases](https://github.com/mahie-cs/Lucid/releases). Until then you can build it from source.

Lucid is free software, a fork of [Limusic for Android](https://github.com/SimoHypers/limusic-android)
by SimoHypers, which is itself built on [SimpMusic](https://github.com/maxrave-dev/SimpMusic) by
maxrave-dev and its contributors. See [Credits](#credits) and [Changes from Limusic](#changes-from-limusic).

---

## Features

- **Ad-free playback**: streams come straight from YouTube's API, ads never do
- **Sign in with Google** on a first page of rolling pictures, with your YouTube Music account
- **Search & browse**: songs, albums, artists, playlists and the YouTube Music home feed
- **Download from the player**: a download button sits right beside the song title; tap it to save the song, tap the tick to remove it. Playlists and albums can be downloaded too, and the whole library can be kept downloaded as it grows
- **Your library, on YouTube Music**: likes, follows and saves go to your account, and you can switch between several accounts
- **Remove from playlist**: a song's menu, inside a playlist you own or while it plays from one, has Remove from "playlist name" right under Start radio
- **Backup and restore** of your library and settings, manual or automatic
- **Import playlists** from Spotify through a converted file
- **Queue** with radio and autoplay, shuffle that sticks across queues
- **Synced lyrics** from third-party providers, with a source picker and timing nudge per song
- **One player**: a large cover, wavy seek bar, and a row of controls for queue, lyrics, repeat and shuffle
- **Light, Dark or System**, with your choice of accent colour
- **Sound**: loudness normalization, crossfade (beta), a ten-band equalizer, playback speed and pitch, and a sleep timer
- **Home-screen widget**, **Android Auto**, lock-screen and notification controls, and playback that keeps going in the background

Home, Search, Library and Settings are the four tabs along the bottom.

---

## Building from Source

You need JDK 21 and the Android SDK (Platform 37). Android Studio provides both.

```bash
git clone https://github.com/mahie-cs/Lucid.git
cd Lucid
./gradlew :androidApp:assembleDebug
```

The APKs land in `androidApp/build/outputs/apk/debug/`. A debug build is much larger and slower
than a release build, so judge size and smoothness on `assembleRelease`.

Release builds sign with your own key: point `lucid.signing` in `local.properties` at a properties
file holding `storeFile`, `storePassword`, `keyAlias` and `keyPassword`. Without it, release builds
use the default debug key. Never commit a signing key or that file.

---

## Lyrics

Lucid has no lyrics server of its own. It asks third-party providers in the order you choose:
[Boidu](https://boidu.dev), Unison and LyricsPlus, then [LRCLIB](https://lrclib.net), YouTube
Music's own lyrics, Netease, QQ Music and Kugou. Synced lyrics beat plain text, and matching is
keyed on the track's length so the wrong cut doesn't drift out of time. Results are cached on the
phone. Change the order or switch any source off in **Settings → Lyrics → Lyrics sources**.

---

## Listen Together and Play Anywhere

These two features need a relay server. Lucid keeps the relay address inherited from Limusic, so
by default they use a server run by the Limusic project, not by Lucid. Play Anywhere also expects
the Limusic desktop app on the other end. If you rely on either, consider running your own relay.

---

## How It Works, Briefly

- SimpMusic's Kotlin client speaks YouTube's InnerTube API. Streams are resolved with the web
  client and a PoToken first, then other client identities when one fails.
- The PoToken comes from BotGuard, run in a hidden WebView. The signature cipher and the `n`
  parameter are solved on the phone in QuickJS, and never sent to a third-party server.
- Audio goes through Media3 ExoPlayer with an on-disk cache, inside a foreground service whose
  media session drives the notification, lock screen and Android Auto.
- Your library, downloads and lyrics cache live in a Room database on the phone.

---

## Changes from Limusic

Lucid was forked from Limusic for Android (version 1.1.0-rc.3) in November 2026. Changes so far:

- Renamed the app, package and application ID to Lucid (`com.lucid.android`), and a new logo
- Themes reduced to Light, Dark and System with an accent colour; the preset themes, artwork
  tinting and animated backgrounds were removed
- A single player style (Classic) in place of three
- A download button beside the song title in the player
- Remove from playlist in a song's menu, with the playlist's name on it
- Settings moved from the top of Home to a tab in the bottom bar
- A Google sign-in page for a signed-out app, and a notification permission request after signing in
- A flat top bar and no blurred header on Home, to smooth scrolling
- The Limusic-hosted lyrics provider removed from the lyrics sources
- Last.fm scrobbling and Discord Rich Presence removed
- The Terms and Conditions are read from this repository and shown inside the app

---

## Credits

- [SimpMusic](https://github.com/maxrave-dev/SimpMusic) by maxrave-dev and its contributors: the
  project this app ultimately grew from, and most of its code
- [Limusic for Android](https://github.com/SimoHypers/limusic-android) by SimoHypers: the fork
  Lucid is built on
- [NewPipe](https://github.com/TeamNewPipe/NewPipe): the PoToken minter, ported to this app
- Lucid is made by [mahie-cs](https://github.com/mahie-cs)

---

## Terms, Disclaimer and License

Using Lucid means agreeing to its [Terms and Conditions](TERMS.md). The app shows them from this
file: it fetches `TERMS.md` from this repository's default branch, so a change pushed here is what
users read in the app.

This project is not affiliated with, funded, authorized, endorsed by, or in any way associated
with YouTube, Google LLC, or any of their affiliates and subsidiaries. It is also not endorsed by
the authors of SimpMusic or Limusic. All trademarks, service marks, and intellectual property
rights referenced in this project belong to their respective owners.

Lucid is released under the [GNU General Public License v3.0](LICENSE), the same license as the
projects it is built on. It comes with no warranty. You may share and change it under the terms of
that license, and if you distribute it you must make the source available under the same terms.
See [CONTRIBUTING.md](CONTRIBUTING.md) to help improve it.
