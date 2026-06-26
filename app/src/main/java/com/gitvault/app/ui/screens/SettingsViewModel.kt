package com.gitvault.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitvault.app.data.model.AccessTokenEntity
import com.gitvault.app.data.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val tokens: StateFlow<List<AccessTokenEntity>> = tokenRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToken(label: String, token: String) {
        viewModelScope.launch {
            tokenRepository.insert(
                AccessTokenEntity(label = label, token = token)
            )
        }
    }

    fun deleteToken(token: AccessTokenEntity) {
        viewModelScope.launch {
            tokenRepository.delete(token)
        }
    }
}
