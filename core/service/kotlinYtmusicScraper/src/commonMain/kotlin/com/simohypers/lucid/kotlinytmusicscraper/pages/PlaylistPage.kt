package com.simohypers.lucid.kotlinytmusicscraper.pages

import com.simohypers.lucid.kotlinytmusicscraper.models.Album
import com.simohypers.lucid.kotlinytmusicscraper.models.Artist
import com.simohypers.lucid.kotlinytmusicscraper.models.MusicResponsiveListItemRenderer
import com.simohypers.lucid.kotlinytmusicscraper.models.PlaylistItem
import com.simohypers.lucid.kotlinytmusicscraper.models.SongItem
import com.simohypers.lucid.kotlinytmusicscraper.models.oddElements
import com.simohypers.lucid.kotlinytmusicscraper.models.splitBySeparator
import com.simohypers.lucid.kotlinytmusicscraper.utils.parseTime

data class PlaylistPage(
    val playlist: PlaylistItem,
    val songs: List<SongItem>,
    val songsContinuation: String?,
    val continuation: String?,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer?): SongItem? {
            if (renderer == null) {
                return null
            } else {
                return SongItem(
                    id = renderer.videoId ?: return null,
                    title =
                        renderer.flexColumns
                            .firstOrNull()
                            ?.musicResponsiveListItemFlexColumnRenderer
                            ?.text
                            ?.runs
                            ?.firstOrNull()
                            ?.text ?: return null,
                    artists =
                        renderer.flexColumns
                            .getOrNull(1)
                            ?.musicResponsiveListItemFlexColumnRenderer
                            ?.text
                            ?.runs
                            // The column reads "Artist • Album • 13M plays"; only the first group
                            // is artists, so everything after the first " • " is dropped.
                            ?.splitBySeparator()
                            ?.firstOrNull()
                            ?.oddElements()
                            ?.map {
                                Artist(
                                    name = it.text,
                                    id = it.navigationEndpoint?.browseEndpoint?.browseId,
                                )
                            } ?: return null,
                    album =
                        renderer.flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()?.let {
                            Album(
                                name = it.text,
                                id = it.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                            )
                        },
                    duration =
                        renderer.fixedColumns
                            ?.firstOrNull()
                            ?.musicResponsiveListItemFlexColumnRenderer
                            ?.text
                            ?.runs
                            ?.firstOrNull()
                            ?.text
                            ?.parseTime(),
                    thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                    explicit =
                        renderer.badges?.find {
                            it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                        } != null,
                    endpoint =
                        renderer.overlay
                            ?.musicItemThumbnailOverlayRenderer
                            ?.content
                            ?.musicPlayButtonRenderer
                            ?.playNavigationEndpoint
                            ?.watchEndpoint,
                    thumbnails = renderer.thumbnail.musicThumbnailRenderer.thumbnail,
                    musicVideoType = renderer.musicVideoType,
                )
            }
        }
    }
}