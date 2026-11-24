package com.simohypers.lucid.kotlinytmusicscraper.models.body

import com.simohypers.lucid.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class NotificationBody(
    val context: Context,
    val notificationsMenuRequestType: String = "NOTIFICATIONS_MENU_REQUEST_TYPE_INBOX",
)