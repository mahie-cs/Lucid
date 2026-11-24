package com.simohypers.lucid.data.mediaservice

import com.simohypers.lucid.domain.manager.DataStoreManager
import com.simohypers.lucid.domain.mediaservice.handler.MediaPlayerHandler
import com.simohypers.lucid.domain.repository.LocalPlaylistRepository
import com.simohypers.lucid.domain.repository.SongRepository
import com.simohypers.lucid.domain.repository.StreamRepository
import kotlinx.coroutines.CoroutineScope

expect fun createMediaServiceHandler(
    dataStoreManager: DataStoreManager,
    songRepository: SongRepository,
    streamRepository: StreamRepository,
    localPlaylistRepository: LocalPlaylistRepository,
    coroutineScope: CoroutineScope,
): MediaPlayerHandler