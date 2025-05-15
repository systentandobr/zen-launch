package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.repositories.AppRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para o caso de uso GetFavoriteAppsUseCase.
 */
class GetFavoriteAppsUseCaseTest {
    
    // Mocks
    private lateinit var appRepository: AppRepository
    
    // Caso de uso a ser testado
    private lateinit var getFavoriteAppsUseCase: GetFavoriteAppsUseCase
    
    @Before
    fun setup() {
        // Inicializar mocks
        appRepository = mockk()
        
        // Inicializar caso de uso
        getFavoriteAppsUseCase = GetFavoriteAppsUseCase(appRepository)
    }
    
    @Test
    fun `should return favorite apps from repository`() = runBlocking {
        // Dados de teste
        val favoriteApps = listOf(
            App(
                packageName = "com.example.app1",
                appName = "App 1",
                isSystemApp = false,
                isFavorite = true
            ),
            App(
                packageName = "com.example.app2",
                appName = "App 2",
                isSystemApp = false,
                isFavorite = true
            )
        )
        
        // Configurar mock
        coEvery { appRepository.getFavoriteApps() } returns flowOf(favoriteApps)
        
        // Executar caso de uso
        val result = getFavoriteAppsUseCase().first()
        
        // Verificar resultado
        assertEquals(favoriteApps, result)
        assertEquals(2, result.size)
        assertEquals("com.example.app1", result[0].packageName)
        assertEquals("com.example.app2", result[1].packageName)
    }
    
    @Test
    fun `should return empty list when no favorites exist`() = runBlocking {
        // Configurar mock
        coEvery { appRepository.getFavoriteApps() } returns flowOf(emptyList())
        
        // Executar caso de uso
        val result = getFavoriteAppsUseCase().first()
        
        // Verificar resultado
        assertEquals(0, result.size)
    }
}
