# Arquitetura e Modelos de Dados

## Visão Geral

Esta seção descreve as mudanças na arquitetura da aplicação, incluindo novos modelos de dados, repositórios e casos de uso necessários para implementar as funcionalidades do Deep Focus Mode e controle de uso de aplicativos.

## Tarefas de Implementação

### 1. Novos Modelos de Domínio

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 1 dia**

#### `AppUsageInfo.kt`

```kotlin
package com.zenlauncher.domain.model

/**
 * Contém informações sobre o uso de um aplicativo
 */
data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTimeToday: Long, // Em milissegundos
    val usageTimeWeek: Long, // Em milissegundos
    val lastUsed: Long, // Timestamp
    val launchCount: Int // Número de aberturas hoje
)
```

#### `UsageLimit.kt`

```kotlin
package com.zenlauncher.domain.model

/**
 * Define limites de uso para um aplicativo
 */
data class UsageLimit(
    val packageName: String,
    val dailyLimitInMinutes: Int, // Limite diário em minutos
    val enabled: Boolean, // Se o limite está ativo
    val blockAfterLimit: Boolean // Se bloqueia após o limite
)
```

#### `FocusSession.kt`

```kotlin
package com.zenlauncher.domain.model

/**
 * Define uma sessão de foco
 */
data class FocusSession(
    val id: String,
    val startTime: Long,
    val scheduledEndTime: Long,
    val actualEndTime: Long?, // Null se ainda estiver ativa
    val allowedPackages: List<String> // Apps permitidos durante a sessão
)
```

### 2. Repositórios de Domínio

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 2 dias**

#### `UsageStatsRepository.kt`

```kotlin
package com.zenlauncher.domain.repository

import com.zenlauncher.domain.model.AppUsageInfo
import com.zenlauncher.domain.model.UsageLimit
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para estatísticas de uso de aplicativos
 */
interface UsageStatsRepository {
    suspend fun getAppUsageInfo(packageName: String, days: Int): AppUsageInfo
    fun observeAppUsageInfo(packageName: String): Flow<AppUsageInfo>
    suspend fun getAllAppsUsageInfo(days: Int): List<AppUsageInfo>
    suspend fun getAppsUsageFlow(): Flow<List<AppUsageInfo>>
    suspend fun getAppUsageLimit(packageName: String): UsageLimit
    suspend fun setAppUsageLimit(limit: UsageLimit)
    suspend fun getAllUsageLimits(): List<UsageLimit>
    suspend fun isAppBlockedByUsageLimit(packageName: String): Boolean
}
```

#### `FocusModeRepository.kt`

```kotlin
package com.zenlauncher.domain.repository

import com.zenlauncher.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para sessões de foco
 */
interface FocusModeRepository {
    suspend fun startFocusSession(durationMinutes: Int, allowedPackages: List<String>): FocusSession
    suspend fun endFocusSession(sessionId: String): FocusSession
    suspend fun getActiveFocusSession(): FocusSession?
    fun observeActiveFocusSession(): Flow<FocusSession?>
    suspend fun getFocusSessionHistory(limit: Int): List<FocusSession>
    suspend fun isAppAllowedInFocusMode(packageName: String): Boolean
    suspend fun updateAllowedApps(allowedPackages: List<String>)
}
```

### 3. Implementação dos Repositórios

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 3 dias**

#### `UsageStatsRepositoryImpl.kt`

```kotlin
package com.zenlauncher.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.zenlauncher.domain.model.AppUsageInfo
import com.zenlauncher.domain.model.UsageLimit
import com.zenlauncher.domain.repository.UsageStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Implementação do repositório de estatísticas de uso
 */
class UsageStatsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) : UsageStatsRepository {
    
    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    
    // Implemente todos os métodos definidos na interface
    // Não incluí todo o código por questões de espaço
}
```

#### `FocusModeRepositoryImpl.kt`

```kotlin
package com.zenlauncher.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.zenlauncher.domain.model.FocusSession
import com.zenlauncher.domain.repository.FocusModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementação do repositório de modo de foco
 */
class FocusModeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FocusModeRepository {
    
    // Implemente todos os métodos definidos na interface
    // Não incluí todo o código por questões de espaço
}
```

### 4. Casos de Uso para Estatísticas e Limites

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

```kotlin
package com.zenlauncher.domain.usecase

import com.zenlauncher.domain.model.AppUsageInfo
import com.zenlauncher.domain.repository.UsageStatsRepository
import javax.inject.Inject

/**
 * Caso de uso para obter informações de uso de um aplicativo
 */
class GetAppUsageInfoUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    suspend operator fun invoke(packageName: String, days: Int = 1): AppUsageInfo {
        return usageStatsRepository.getAppUsageInfo(packageName, days)
    }
}
```

```kotlin
package com.zenlauncher.domain.usecase

import com.zenlauncher.domain.model.UsageLimit
import com.zenlauncher.domain.repository.UsageStatsRepository
import javax.inject.Inject

/**
 * Caso de uso para definir limite de uso para um aplicativo
 */
class SetAppUsageLimitUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    suspend operator fun invoke(limit: UsageLimit) {
        usageStatsRepository.setAppUsageLimit(limit)
    }
}
```

```kotlin
package com.zenlauncher.domain.usecase

import com.zenlauncher.domain.repository.UsageStatsRepository
import javax.inject.Inject

/**
 * Caso de uso para verificar se um aplicativo está bloqueado por limite de uso
 */
class CheckAppUsageLimitUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    suspend operator fun invoke(packageName: String): Boolean {
        return usageStatsRepository.isAppBlockedByUsageLimit(packageName)
    }
}
```

### 5. Casos de Uso para o Modo de Foco

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

```kotlin
package com.zenlauncher.domain.usecase

import com.zenlauncher.domain.model.FocusSession
import com.zenlauncher.domain.repository.FocusModeRepository
import javax.inject.Inject

/**
 * Caso de uso para iniciar uma sessão de foco
 */
class StartFocusSessionUseCase @Inject constructor(
    private val focusModeRepository: FocusModeRepository
) {
    suspend operator fun invoke(durationMinutes: Int, allowedPackages: List<String>): FocusSession {
        return focusModeRepository.startFocusSession(durationMinutes, allowedPackages)
    }
}
```

```kotlin
package com.zenlauncher.domain.usecase

import com.zenlauncher.domain.model.FocusSession
import com.zenlauncher.domain.repository.FocusModeRepository
import javax.inject.Inject

/**
 * Caso de uso para encerrar uma sessão de foco
 */
class EndFocusSessionUseCase @Inject constructor(
    private val focusModeRepository: FocusModeRepository
) {
    suspend operator fun invoke(): FocusSession? {
        val activeSession = focusModeRepository.getActiveFocusSession() ?: return null
        return focusModeRepository.endFocusSession(activeSession.id)
    }
}
```

```kotlin
package com.zenlauncher.domain.usecase

import com.zenlauncher.domain.repository.FocusModeRepository
import javax.inject.Inject

/**
 * Caso de uso para verificar se um aplicativo pode ser usado durante o modo de foco
 */
class CheckAppInFocusModeUseCase @Inject constructor(
    private val focusModeRepository: FocusModeRepository
) {
    suspend operator fun invoke(packageName: String): Boolean {
        val activeSession = focusModeRepository.getActiveFocusSession() ?: return true
        return focusModeRepository.isAppAllowedInFocusMode(packageName)
    }
}
```

### 6. Atualizações no ServiceLocator

**Prioridade: Alta** | **Complexidade: Baixa** | **Estimativa: 1 dia**

```kotlin
package com.zenlauncher.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.zenlauncher.data.repository.FocusModeRepositoryImpl
import com.zenlauncher.data.repository.UsageStatsRepositoryImpl
import com.zenlauncher.domain.repository.FocusModeRepository
import com.zenlauncher.domain.repository.UsageStatsRepository
import com.zenlauncher.domain.usecase.CheckAppInFocusModeUseCase
import com.zenlauncher.domain.usecase.CheckAppUsageLimitUseCase
import com.zenlauncher.domain.usecase.EndFocusSessionUseCase
import com.zenlauncher.domain.usecase.GetAppUsageInfoUseCase
import com.zenlauncher.domain.usecase.StartFocusSessionUseCase

/**
 * Extensão do ServiceLocator para fornecer dependências relacionadas ao uso de apps e foco
 */
object ServiceLocatorExtensions {
    
    private var usageStatsRepository: UsageStatsRepository? = null
    private var focusModeRepository: FocusModeRepository? = null
    
    fun provideUsageStatsRepository(context: Context, dataStore: DataStore<Preferences>): UsageStatsRepository {
        return usageStatsRepository ?: synchronized(this) {
            usageStatsRepository ?: UsageStatsRepositoryImpl(context, dataStore).also {
                usageStatsRepository = it
            }
        }
    }
    
    fun provideFocusModeRepository(dataStore: DataStore<Preferences>): FocusModeRepository {
        return focusModeRepository ?: synchronized(this) {
            focusModeRepository ?: FocusModeRepositoryImpl(dataStore).also {
                focusModeRepository = it
            }
        }
    }
    
    fun provideCheckAppUsageLimitUseCase(context: Context, dataStore: DataStore<Preferences>): CheckAppUsageLimitUseCase {
        return CheckAppUsageLimitUseCase(provideUsageStatsRepository(context, dataStore))
    }
    
    fun provideGetAppUsageInfoUseCase(context: Context, dataStore: DataStore<Preferences>): GetAppUsageInfoUseCase {
        return GetAppUsageInfoUseCase(provideUsageStatsRepository(context, dataStore))
    }
    
    fun provideStartFocusSessionUseCase(dataStore: DataStore<Preferences>): StartFocusSessionUseCase {
        return StartFocusSessionUseCase(provideFocusModeRepository(dataStore))
    }
    
    fun provideEndFocusSessionUseCase(dataStore: DataStore<Preferences>): EndFocusSessionUseCase {
        return EndFocusSessionUseCase(provideFocusModeRepository(dataStore))
    }
    
    fun provideCheckAppInFocusModeUseCase(dataStore: DataStore<Preferences>): CheckAppInFocusModeUseCase {
        return CheckAppInFocusModeUseCase(provideFocusModeRepository(dataStore))
    }
}
```

## Permissões Necessárias

Para acessar as estatísticas de uso, adicione ao `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
```

## Estratégia de Implementação

1. Primeiro, implemente os modelos de domínio e interfaces de repositório
2. Em seguida, implemente as implementações de repositório
3. Depois, implemente os casos de uso
4. Por fim, estenda o ServiceLocator para fornecer as novas dependências

## Observações Importantes

- A permissão PACKAGE_USAGE_STATS exige que o usuário a conceda manualmente nas configurações do Android. Será necessário implementar um fluxo para guiar o usuário a habilitá-la.
- Considere usar DataStore em vez de SharedPreferences para persistência de dados.
- Implemente testes unitários para garantir que os casos de uso funcionem conforme esperado.
