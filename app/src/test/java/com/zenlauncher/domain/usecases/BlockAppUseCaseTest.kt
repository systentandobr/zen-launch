package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.repositories.AppBlockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Testes unitários para o caso de uso BlockAppUseCase.
 */
class BlockAppUseCaseTest {
    
    // Mocks
    private lateinit var appBlockRepository: AppBlockRepository
    
    // Caso de uso a ser testado
    private lateinit var blockAppUseCase: BlockAppUseCase
    
    @Before
    fun setup() {
        // Inicializar mocks
        appBlockRepository = mockk()
        
        // Inicializar caso de uso
        blockAppUseCase = BlockAppUseCase(appBlockRepository)
    }
    
    @Test
    fun `should block app for specified hours`() = runBlocking {
        // Dados de teste
        val packageName = "com.example.app"
        val hours = 2.0f
        val level = AppBlock.BlockLevel.MEDIUM
        
        // Configurar mock
        coEvery { 
            appBlockRepository.blockApp(
                packageName = any(), 
                blockedUntil = any(), 
                blockLevel = any()
            ) 
        } returns true
        
        // Executar caso de uso
        val result = blockAppUseCase(packageName, hours, level)
        
        // Verificar resultado
        assertTrue(result)
        
        // Verificar se o repositório foi chamado com os parâmetros corretos
        coVerify { 
            appBlockRepository.blockApp(
                packageName = packageName,
                blockedUntil = any(),
                blockLevel = level
            )
        }
    }
    
    @Test
    fun `should block app for specified minutes`() = runBlocking {
        // Dados de teste
        val packageName = "com.example.app"
        val minutes = 30
        val level = AppBlock.BlockLevel.SOFT
        
        // Configurar mock
        coEvery { 
            appBlockRepository.blockApp(
                packageName = any(), 
                blockedUntil = any(), 
                blockLevel = any()
            ) 
        } returns true
        
        // Executar caso de uso
        val result = blockAppUseCase.blockForMinutes(packageName, minutes, level)
        
        // Verificar resultado
        assertTrue(result)
        
        // Verificar se o repositório foi chamado com os parâmetros corretos
        coVerify { 
            appBlockRepository.blockApp(
                packageName = packageName,
                blockedUntil = any(),
                blockLevel = level
            )
        }
    }
    
    @Test
    fun `should handle repository failure`() = runBlocking {
        // Dados de teste
        val packageName = "com.example.app"
        val hours = 2.0f
        
        // Configurar mock para falhar
        coEvery { 
            appBlockRepository.blockApp(
                packageName = any(), 
                blockedUntil = any(), 
                blockLevel = any()
            ) 
        } returns false
        
        // Executar caso de uso
        val result = blockAppUseCase(packageName, hours)
        
        // Verificar resultado
        assertFalse(result)
    }
}
