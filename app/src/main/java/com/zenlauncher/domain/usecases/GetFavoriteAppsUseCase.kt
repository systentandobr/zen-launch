package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obter aplicativos favoritos.
 */
class GetFavoriteAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Executa o caso de uso para obter aplicativos favoritos.
     * @return Flow com lista de aplicativos favoritos
     */
    operator fun invoke(): Flow<List<App>> {
        return appRepository.getFavoriteApps()
    }
}
