package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.repositories.AppRepository
import javax.inject.Inject

/**
 * Caso de uso para lançar um aplicativo.
 */
class LaunchAppUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Executa o caso de uso para lançar um aplicativo.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @return true se o aplicativo foi lançado com sucesso
     */
    suspend operator fun invoke(packageName: String): Boolean {
        return appRepository.launchApp(packageName)
    }
}
