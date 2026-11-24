package com.simohypers.lucid.domain.data.model.home

import com.simohypers.lucid.domain.data.model.home.chart.Chart
import com.simohypers.lucid.domain.data.model.mood.Mood
import com.simohypers.lucid.domain.utils.Resource

data class HomeDataCombine(
    val home: Resource<Pair<String?, List<HomeItem>>>,
    val mood: Resource<Mood>,
    val chart: Resource<Chart>,
    val newRelease: Resource<List<HomeItem>>,
)