package com.simohypers.lucid.viewModel

import androidx.lifecycle.viewModelScope
import com.simohypers.lucid.common.SELECTED_LANGUAGE
import com.simohypers.lucid.domain.data.model.mood.moodmoments.MoodsMomentObject
import com.simohypers.lucid.domain.manager.DataStoreManager
import com.simohypers.lucid.domain.repository.HomeRepository
import com.simohypers.lucid.domain.utils.Resource
import com.simohypers.lucid.logger.Logger
import com.simohypers.lucid.viewModel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MoodViewModel(
    dataStoreManager: DataStoreManager,
    private val homeRepository: HomeRepository,
) : BaseViewModel() {
    private val _moodsMomentObject: MutableStateFlow<MoodsMomentObject?> = MutableStateFlow(null)
    var moodsMomentObject: StateFlow<MoodsMomentObject?> = _moodsMomentObject
    var loading = MutableStateFlow<Boolean>(false)

    private var regionCode: String? = null
    private var language: String? = null

    /**
     * Params whose result is already in [_moodsMomentObject]. MoodScreen calls [getMood] from a
     * `LaunchedEffect(params)`, which fires again every time the screen re-enters composition —
     * i.e. on every back navigation — so without this the shelf was refetched each time.
     */
    private var loadedParams: String? = null

    init {
        regionCode = runBlocking { dataStoreManager.location.first() }
        language = runBlocking { dataStoreManager.getString(SELECTED_LANGUAGE).first() }
    }

    fun getMood(params: String) {
        if (params == loadedParams && _moodsMomentObject.value != null) return
        loading.value = true
        viewModelScope.launch {
//            mainRepository.getMood(params, regionCode!!, SUPPORTED_LANGUAGE.serverCodes[SUPPORTED_LANGUAGE.codes.indexOf(language!!)]).collect{ values ->
//                _moodsMomentObject.value = values
//            }
            homeRepository.getMoodData(params).collect { values ->
                Logger.d("MoodViewModel", "getMood: $values")
                when (values) {
                    is Resource.Success -> {
                        _moodsMomentObject.value = values.data
                        // Only remember it once it actually succeeded, so a failed load retries.
                        loadedParams = params
                    }

                    is Resource.Error -> {
                        _moodsMomentObject.value = null
                        loadedParams = null
                        reloadWhenOnline({ loadedParams == null }) { getMood(params) }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                loading.value = false
            }
        }
    }
}