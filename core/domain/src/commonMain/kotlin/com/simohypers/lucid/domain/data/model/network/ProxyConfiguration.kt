package com.simohypers.lucid.domain.data.model.network

import com.simohypers.lucid.domain.manager.DataStoreManager

data class ProxyConfiguration(
    val host: String,
    val port: Int,
    val type: DataStoreManager.ProxyType,
    val username: String? = null,
    val password: String? = null,
)