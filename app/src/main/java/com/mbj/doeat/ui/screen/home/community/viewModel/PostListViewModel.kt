package com.mbj.doeat.ui.screen.home.community.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.util.UserDataStore
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

    val userId = UserDataStore.getLoginResponse()?.userId

    val partyList = getAllPartyList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    private val _searchBarText = MutableStateFlow("")
    val searchBarText: StateFlow<String> = _searchBarText

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

    fun updateSearchBarText(newText: String) {
        _searchBarText.value = newText
    }

    fun getFilteredPartyList(partyList: List<Party>, searchText: String): List<Party> {
        val searchTextLower = searchText.lowercase()
        return partyList.filter { party ->
            party.restaurantName.lowercase().contains(searchTextLower)
        }
    }
}
