package com.mbj.doeat.ui.screen.home.community.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(private val defaultDBRepository: DefaultDBRepository) : ViewModel() {

    val partyList = getAllPartyList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    private fun getAllPartyList(): Flow<List<Party>> {
        return defaultDBRepository.getAllPartyList(
            onComplete = { },
            onError = { }
        ).map { response ->
            when (response) {
                is ApiResultSuccess -> response.data
                else -> {
                    emptyList()
                }

            }
        }
    }
}
