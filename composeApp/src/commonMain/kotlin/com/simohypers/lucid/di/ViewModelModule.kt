package com.simohypers.lucid.di

import com.simohypers.lucid.viewModel.DeviceLinkViewModel
import com.simohypers.lucid.viewModel.AlbumViewModel
import com.simohypers.lucid.viewModel.ListenTogetherViewModel
import com.simohypers.lucid.viewModel.ArtistViewModel
import com.simohypers.lucid.viewModel.BrowseViewModel
import com.simohypers.lucid.viewModel.HomeViewModel
import com.simohypers.lucid.viewModel.ImportViewModel
import com.simohypers.lucid.viewModel.LibraryDynamicPlaylistViewModel
import com.simohypers.lucid.viewModel.LibraryViewModel
import com.simohypers.lucid.viewModel.LocalPlaylistViewModel
import com.simohypers.lucid.viewModel.MoodViewModel
import com.simohypers.lucid.viewModel.MoreAlbumsViewModel
import com.simohypers.lucid.viewModel.NotificationViewModel
import com.simohypers.lucid.viewModel.NowPlayingBottomSheetViewModel
import com.simohypers.lucid.viewModel.PlaylistViewModel
import com.simohypers.lucid.viewModel.PodcastViewModel
import com.simohypers.lucid.viewModel.RecentlySongsViewModel
import com.simohypers.lucid.viewModel.SearchViewModel
import com.simohypers.lucid.viewModel.AutoEqViewModel
import com.simohypers.lucid.viewModel.SettingsViewModel
import com.simohypers.lucid.viewModel.SharedViewModel
import com.simohypers.lucid.viewModel.SongSelectionViewModel
import com.simohypers.lucid.viewModel.YouTubeAccounts
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        single { YouTubeAccounts(get(), get(), get()) }
        single {
            SharedViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single {
            SearchViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            SongSelectionViewModel(
                get(),
                get(),
            )
        }
        // Only one of these resolves on a given platform: Desktop binds the host repository, Android the
        // sender. Koin resolves dependencies when a ViewModel is created, not when it is declared.
        viewModel {
            NowPlayingBottomSheetViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryDynamicPlaylistViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            ImportViewModel(
                get(),
            )
        }
        viewModel {
            AlbumViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            HomeViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            AutoEqViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ArtistViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            PlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            PodcastViewModel(
                get(),
            )
        }
        viewModel {
            MoreAlbumsViewModel(
                get(),
            )
        }
        viewModel {
            BrowseViewModel(
                get(),
            )
        }
        viewModel {
            RecentlySongsViewModel(
                get(),
            )
        }
        viewModel {
            LocalPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            NotificationViewModel(
                get(),
            )
        }
        viewModel {
            MoodViewModel(
                get(),
                get(),
            )
        }
        viewModel { DeviceLinkViewModel(repository = get()) }
        viewModel {
            ListenTogetherViewModel(
                repository = get(),
                dataStore = get(),
                bridge = get(),
            )
        }

    }