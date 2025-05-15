package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obter todos os aplicativos instalados.
 */
class GetAllAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Executa o caso de uso para obter todos os aplicativos.
     * @return Flow com lista de todos os aplicativos instalados
     */
    operator fun invoke(): Flow<List<App>> {
        return appRepository.getAllApps()
    }
}
