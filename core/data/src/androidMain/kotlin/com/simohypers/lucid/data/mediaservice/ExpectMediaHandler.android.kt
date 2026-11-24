package com.simohypers.lucid.data.mediaservice


actual fun createMediaServiceHandler(
    dataStoreManager: com.simohypers.lucid.domain.manager.DataStoreManager,
    songRepository: com.simohypers.lucid.domain.repository.SongRepository,
    streamRepository: com.simohypers.lucid.domain.repository.StreamRepository,
    localPlaylistRepository: com.simohypers.lucid.domain.repository.LocalPlaylistRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): com.simohypers.lucid.domain.mediaservice.handler.MediaPlayerHandler =
    MediaServiceHandlerImpl(
        dataStoreManager,
        songRepository,
        streamRepository,
        localPlaylistRepository,
        coroutineScope,
    )