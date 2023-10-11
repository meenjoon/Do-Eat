package com.mbj.doeat.ui.screen.home.detail.viewmodel

import androidx.lifecycle.ViewModel
import com.mbj.doeat.data.remote.model.SearchItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailViewModel : ViewModel() {

    private val _searchItem = MutableStateFlow<SearchItem?>(null)
    val searchItem: StateFlow<SearchItem?> = _searchItem

    fun updateSearchItem(inputSearchItem: SearchItem) {
        _searchItem.value = inputSearchItem
    }
}
