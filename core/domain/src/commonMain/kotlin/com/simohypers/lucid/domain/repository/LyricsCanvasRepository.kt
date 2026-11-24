package com.simohypers.lucid.domain.repository

import com.simohypers.lucid.domain.data.entities.LyricsEntity
import com.simohypers.lucid.domain.data.model.lyrics.LucidException
import com.simohypers.lucid.domain.data.model.lyrics.LucidRating
import com.simohypers.lucid.domain.data.model.lyrics.LyricsSource
import com.simohypers.lucid.domain.data.model.browse.artist.ArtistEditorial
import com.simohypers.lucid.domain.data.model.canvas.CanvasResult
import com.simohypers.lucid.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LyricsCanvasRepository {
    fun getSavedLyrics(videoId: String): Flow<LyricsEntity?>

    suspend fun insertLyrics(lyricsEntity: LyricsEntity)

    /**
     * The song's lyrics as Lucid finds them: cached, else asked down [order] (switched-off sources
     * skipped), the first synced answer winning over any plain one. Null when no source has any.
     */
    suspend fun getLyrics(
        videoId: String,
        title: String,
        artist: String,
        album: String?,
        durationSeconds: Int,
        order: List<Pair<LyricsSource, Boolean>>,
    ): LyricsEntity?

    /**
     * One source alone, uncached: the source picker's preview, which also works for a source
     * switched off. Null when it has nothing; throws when it couldn't be asked. [searched]: title
     * and artist were typed in the picker, not the song's own.
     */
    suspend fun previewLyrics(
        videoId: String,
        source: LyricsSource,
        title: String,
        artist: String,
        album: String?,
        durationSeconds: Int,
        searched: Boolean,
    ): LyricsEntity?

    /**
     * Bring a cached lyric up to date behind the one on screen, as desktop does: Lucid's own is
     * asked once a day whether its revision moved, any other unpinned one once a week whether
     * Lucid has the song by now (only where Lucid would win [order]). True when the cache no
     * longer holds what was shown, so the caller should ask [getLyrics] again.
     */
    suspend fun recheckLyrics(
        videoId: String,
        order: List<Pair<LyricsSource, Boolean>>,
    ): Boolean

    /** The votes on the song's live Lucid lyric, or null when it has none. Throws [LucidException]. */
    suspend fun lucidRating(videoId: String): LucidRating?

    /** Vote on the live lyric (1, -1, or 0 to take it back). Throws [LucidException]. */
    suspend fun lucidVote(
        videoId: String,
        value: Int,
        before: LucidRating,
    ): LucidRating

    /** Report the live lyric for one of `LucidLyrics.REPORT_REASONS`. Throws [LucidException]. */
    suspend fun lucidReport(
        videoId: String,
        reason: String,
        note: String?,
    )

    /** Forget one song's lyrics, a hand-picked source included. */
    suspend fun forgetLyrics(videoId: String)

    /** Drop every cached song except those whose source was picked by hand. */
    suspend fun clearUnpinnedLyrics()

    /**
     * Animated album artwork from the hidden AM catalog, returned as the same [CanvasResult] a
     * Spotify canvas produces and stored in the same columns. Needs no login.
     */
    fun getAMAnimatedArtwork(videoId: String): Flow<Resource<CanvasResult>>

    suspend fun updateCanvasUrl(
        videoId: String,
        canvasUrl: String,
    )

    suspend fun updateCanvasThumbUrl(
        videoId: String,
        canvasThumbUrl: String,
    )

    /**
     * Fetch everything the hidden catalog holds for an artist: the name-logo image with its
     * dominant color, and the animated artist artwork.
     *
     * One call, because both ride the same response — splitting them would double a pair of
     * requests against an API that is not ours. Either half may be null on a success.
     */
    fun getArtistEditorial(artistName: String): Flow<Resource<ArtistEditorial>>

}