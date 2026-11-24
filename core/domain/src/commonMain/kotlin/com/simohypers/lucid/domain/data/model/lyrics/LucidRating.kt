package com.simohypers.lucid.domain.data.model.lyrics

/** The votes on the lyric Lucid Lyrics has live for a song, this install's own among them. */
data class LucidRating(
    val revision: Long,
    val up: Int,
    val down: Int,
    /** -1, 0 or 1. */
    val myVote: Int,
) {
    /** The counts as they will read once the service has [value], shown before it answers. */
    fun withVote(value: Int): LucidRating =
        copy(
            up = (up - (if (myVote == 1) 1 else 0) + (if (value == 1) 1 else 0)).coerceAtLeast(0),
            down = (down - (if (myVote == -1) 1 else 0) + (if (value == -1) 1 else 0)).coerceAtLeast(0),
            myVote = value,
        )

    /** What a tap on a thumb asks for: the same thumb again takes the vote back. */
    fun next(clicked: Int): Int = if (myVote == clicked) 0 else clicked
}

/**
 * A refusal from Lucid Lyrics, by the service's error code (`own`, `not_found`, `rate_limited`,
 * `banned`, `unauthorized`...), or `unavailable` when it couldn't be reached.
 */
class LucidException(
    val code: String,
) : Exception(code)

/** The reasons Lucid Lyrics takes a report for, in the order the report form lists them. */
val LUCID_REPORT_REASONS = listOf("timing", "text", "wrong_song", "abuse")

/** The code for "Lucid Lyrics couldn't be reached". */
const val LUCID_UNAVAILABLE = "unavailable"

/** Where a contributor's public profile is, on the website. Handles are `[A-Za-z0-9_]`. */
fun lucidProfileUrl(handle: String) = "https://limusic.app/lyrics/?u=$handle"
