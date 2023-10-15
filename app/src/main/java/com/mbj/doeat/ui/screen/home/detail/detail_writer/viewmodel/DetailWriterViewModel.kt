package com.mbj.doeat.ui.screen.home.detail.detail_writer.viewmodel

import androidx.lifecycle.ViewModel
import com.mbj.doeat.data.remote.model.Party
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class DetailWriterViewModel : ViewModel() {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    fun updateSearchItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }
}
