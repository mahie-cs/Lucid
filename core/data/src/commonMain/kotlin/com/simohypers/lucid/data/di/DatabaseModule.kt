package com.simohypers.lucid.data.di

import DatabaseDao
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.simohypers.lucid.data.dataStore.DataStoreManagerImpl
import com.simohypers.lucid.data.dataStore.createDataStoreInstance
import com.simohypers.lucid.data.db.Converters
import com.simohypers.lucid.data.db.MusicDatabase
import com.simohypers.lucid.data.db.datasource.LocalDataSource
import com.simohypers.lucid.data.db.getDatabaseBuilder
import com.simohypers.lucid.domain.manager.DataStoreManager
import com.simohypers.lucid.kotlinytmusicscraper.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import com.simohypers.lucid.common.AppIdentity
import com.simohypers.lucid.lyrics.LucidLyrics
import com.simohypers.lucid.lyrics.LyricsProviders
import com.simohypers.lucid.lyrics.AppleMusicClient
import kotlin.time.ExperimentalTime
import com.simohypers.lucid.autoeq.AutoEq

@OptIn(ExperimentalTime::class)
val databaseModule =
    module {
        single(createdAtStart = true) {
            Converters()
        }
        // Database
        single(createdAtStart = true) {
            getDatabaseBuilder(
                get<Converters>()
            )
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
        // DatabaseDao
        single(createdAtStart = true) {
            get<MusicDatabase>().getDatabaseDao()
        }
        // LocalDataSource
        single(createdAtStart = true) {
            LocalDataSource(get<DatabaseDao>(), get<MusicDatabase>())
        }
        // Datastore
        single(createdAtStart = true) {
            createDataStoreInstance()
        }
        // DatastoreManager
        single<DataStoreManager>(createdAtStart = true) {
            DataStoreManagerImpl(get<DataStore<Preferences>>())
        }

        // Move YouTube from Singleton to Koin DI
        single(createdAtStart = true) {
            YouTube()
        }

        single(createdAtStart = true) {
            AppleMusicClient()
        }

        single {
            LyricsProviders()
        }

        // A debug build can point at a local copy of the server (AppIdentity.lyricsServer).
        single {
            LucidLyrics(get<AppIdentity>().lyricsServer.ifBlank { LucidLyrics.URL })
        }

        // Not created at start, unlike the rest: nothing needs it until someone opens the AutoEq
        // picker, and it holds an HTTP client the vast majority of sessions never use.
        single {
            AutoEq()
        }
    }