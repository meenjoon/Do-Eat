package com.mbj.doeat.ui.screen.home.detail.detail_participant.viewmodel

import com.mbj.doeat.data.remote.model.Party
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PartyDetailParticipantViewModel {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    fun updatePartyItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }
}
