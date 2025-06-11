package com.zenlauncher.presentation.common.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.repositories.AppBlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para o dialog de informações de app bloqueado.
 */
@HiltViewModel
class BlockedAppInfoViewModel @Inject constructor(
    private val appBlockRepository: AppBlockRepository
) : ViewModel() {
    
    private val _blockInfo = MutableStateFlow<BlockInfo?>(null)
    val blockInfo: StateFlow<BlockInfo?> = _blockInfo.asStateFlow()
    
    fun loadBlockInfo(packageName: String) {
        viewModelScope.launch {
            try {
                val appBlock = appBlockRepository.getAppBlock(packageName).firstOrNull()
                
                if (appBlock != null && appBlock.isActive()) {
                    _blockInfo.value = BlockInfo(
                        level = appBlock.blockLevel.name,
                        remainingTimeMs = appBlock.remainingTimeMs(),
                        reason = "Bloqueio ${appBlock.blockLevel.name.lowercase()}"
                    )
                } else {
                    _blockInfo.value = null
                }
            } catch (e: Exception) {
                _blockInfo.value = null
            }
        }
    }
    
    data class BlockInfo(
        val level: String,
        val remainingTimeMs: Long,
        val reason: String?
    )
}
