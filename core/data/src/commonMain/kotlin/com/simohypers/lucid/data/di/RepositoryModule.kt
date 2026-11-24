package com.simohypers.lucid.data.di

import com.simohypers.lucid.common.AppIdentity
import com.simohypers.lucid.common.Config.SERVICE_SCOPE
import com.simohypers.lucid.data.io.fileDir
import com.simohypers.lucid.data.repository.AccountRepositoryImpl
import com.simohypers.lucid.data.repository.AlbumRepositoryImpl
import com.simohypers.lucid.data.repository.ArtistRepositoryImpl
import com.simohypers.lucid.data.repository.AutoEqRepositoryImpl
import com.simohypers.lucid.data.lyrics.LyricsRomanizerRepositoryImpl
import com.simohypers.lucid.data.repository.CommonRepositoryImpl
import com.simohypers.lucid.data.repository.HomeRepositoryImpl
import com.simohypers.lucid.data.repository.ImportRepositoryImpl
import com.simohypers.lucid.data.repository.LibrarySync
import com.simohypers.lucid.data.repository.LocalPlaylistRepositoryImpl
import com.simohypers.lucid.data.repository.LyricsCanvasRepositoryImpl
import com.simohypers.lucid.data.repository.PlaylistRepositoryImpl
import com.simohypers.lucid.data.repository.PodcastRepositoryImpl
import com.simohypers.lucid.data.repository.SearchRepositoryImpl
import com.simohypers.lucid.data.repository.SongRepositoryImpl
import com.simohypers.lucid.data.repository.StreamRepositoryImpl
import com.simohypers.lucid.domain.repository.AccountRepository
import com.simohypers.lucid.domain.repository.AlbumRepository
import com.simohypers.lucid.domain.repository.ArtistRepository
import com.simohypers.lucid.domain.repository.AutoEqRepository
import com.simohypers.lucid.domain.repository.LyricsRomanizerRepository
import com.simohypers.lucid.domain.repository.CommonRepository
import com.simohypers.lucid.domain.repository.HomeRepository
import com.simohypers.lucid.domain.repository.ImportRepository
import com.simohypers.lucid.domain.repository.LocalPlaylistRepository
import com.simohypers.lucid.domain.repository.LyricsCanvasRepository
import com.simohypers.lucid.domain.repository.PlaylistRepository
import com.simohypers.lucid.domain.repository.PodcastRepository
import com.simohypers.lucid.domain.repository.SearchRepository
import com.simohypers.lucid.domain.repository.SongRepository
import com.simohypers.lucid.domain.repository.StreamRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AccountRepository>(createdAtStart = true) {
            AccountRepositoryImpl(get(), get())
        }

        single { LibrarySync(get(), get()) }

        single<AlbumRepository>(createdAtStart = true) {
            AlbumRepositoryImpl(get(), get(), get())
        }

        single<ArtistRepository>(createdAtStart = true) {
            ArtistRepositoryImpl(get(), get(), get())
        }

        single<CommonRepository>(createdAtStart = true) {
            CommonRepositoryImpl(get(named(SERVICE_SCOPE)), get(), get(), get(), get()).apply {
                this.init("${fileDir()}/ytdlp-cookie.txt", get(), get<AppIdentity>().isDevBuild)
            }
        }

        // Lazy for the same reason its client is: the picker is the only thing that wants it.
        single<AutoEqRepository> {
            AutoEqRepositoryImpl(get(), get())
        }

        // Lazy: constructing it costs a few File.length() calls (plus, while the pack is missing,
        // one background read of the stored selection), but the kuromoji dictionary behind it is
        // loaded on first Japanese line, or right after a download installs it, and never at
        // startup — so this must NOT be createdAtStart, or every launch pays for a feature most
        // listeners leave off. The path is where Android keeps the downloaded ipadic pack (the
        // APK no longer bundles it); Desktop and iOS ignore it.
        single<LyricsRomanizerRepository> {
            LyricsRomanizerRepositoryImpl("${fileDir()}/kuromoji-ipadic", get())
        }

        single<HomeRepository>(createdAtStart = true) {
            HomeRepositoryImpl(get(), get())
        }

        single<ImportRepository>(createdAtStart = true) {
            ImportRepositoryImpl(get())
        }

        single<LocalPlaylistRepository>(createdAtStart = true) {
            LocalPlaylistRepositoryImpl(get(), get())
        }

        single<LyricsCanvasRepository>(createdAtStart = true) {
            LyricsCanvasRepositoryImpl(get(), get(), get(), get(), get(), get())
        }

        single<PlaylistRepository>(createdAtStart = true) {
            PlaylistRepositoryImpl(get(), get(), get(), get())
        }

        single<PodcastRepository>(createdAtStart = true) {
            PodcastRepositoryImpl(get(), get())
        }

        single<SearchRepository>(createdAtStart = true) {
            SearchRepositoryImpl(get(), get())
        }

        single<SongRepository>(createdAtStart = true) {
            SongRepositoryImpl(get(), get(), get(), get())
        }

        single<StreamRepository>(createdAtStart = true) {
            StreamRepositoryImpl(get(), get())
        }

    }