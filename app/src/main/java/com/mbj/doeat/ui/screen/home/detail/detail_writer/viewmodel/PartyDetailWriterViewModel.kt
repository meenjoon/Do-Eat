package com.mbj.doeat.ui.screen.home.detail.detail_writer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostIdRequestDto
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.BottomBarScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyDetailWriterViewModel @Inject constructor(private val defaultDBRepository: DefaultDBRepository) : ViewModel() {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    private val _showDeletePartyDialog = MutableStateFlow<Boolean>(false)
    val showDeletePartyDialog: StateFlow<Boolean> = _showDeletePartyDialog

    private val _isLoadingView = MutableStateFlow<Boolean>(false)
    val isLoadingView: StateFlow<Boolean> = _isLoadingView

    fun updateSearchItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }

    fun changeShowDeletePartyDialog(showDialog: Boolean) {
        _showDeletePartyDialog.value = showDialog
    }

    fun deleteParty(navHostController: NavHostController) {
        setLoadingState(true)
        viewModelScope.launch {
            defaultDBRepository.partyDelete(
                PartyPostIdRequestDto(partyItem.value?.postId!!),
                onComplete = { },
                onError = { }
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    navHostController.navigate(BottomBarScreen.Community.route) {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _isLoadingView.value = isLoading
    }
}
