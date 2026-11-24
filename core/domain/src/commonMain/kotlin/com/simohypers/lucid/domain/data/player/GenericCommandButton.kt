package com.simohypers.lucid.domain.data.player

import com.simohypers.lucid.domain.mediaservice.handler.RepeatState

sealed class GenericCommandButton {
    data class Like(
        val isLiked: Boolean,
    ) : GenericCommandButton()

    data class Shuffle(
        val isShuffled: Boolean,
    ) : GenericCommandButton()

    data class Repeat(
        val repeatState: RepeatState,
    ) : GenericCommandButton()

    data object Radio : GenericCommandButton()
}