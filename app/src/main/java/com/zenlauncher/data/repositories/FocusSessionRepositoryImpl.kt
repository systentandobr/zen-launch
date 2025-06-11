package com.zenlauncher.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.zenlauncher.domain.entities.FocusSession
import com.zenlauncher.domain.repositories.FocusSessionRepository
import com.zenlauncher.domain.repositories.FocusSessionStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de sessões de foco usando SharedPreferences.
 * Para uma implementação completa, seria recomendado usar Room Database.
 */
@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FocusSessionRepository {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "focus_sessions", 
        Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    override suspend fun saveFocusSession(session: FocusSession): Result<FocusSession> {
        return try {
            val sessionJson = json.encodeToString(session.toSerializable())
            prefs.edit()
                .putString("active_session", sessionJson)
                .putString("session_${session.id}", sessionJson)
                .apply()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateFocusSession(session: FocusSession): Result<FocusSession> {
        return try {
            val sessionJson = json.encodeToString(session.toSerializable())
            val editor = prefs.edit()
                .putString("session_${session.id}", sessionJson)
            
            // Se a sessão está completa, remover da sessão ativa
            if (session.isCompleted || session.endTime != null) {
                editor.remove("active_session")
            } else {
                editor.putString("active_session", sessionJson)
            }
            
            editor.apply()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveFocusSession(): FocusSession? {
        return try {
            val sessionJson = prefs.getString("active_session", null)
            sessionJson?.let { 
                val serializable = json.decodeFromString<SerializableFocusSession>(it)
                serializable.toDomain()
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override fun getAllFocusSessions(): Flow<List<FocusSession>> = flow {
        try {
            val sessions = mutableListOf<FocusSession>()
            val allKeys = prefs.all.keys
            
            for (key in allKeys) {
                if (key.startsWith("session_")) {
                    val sessionJson = prefs.getString(key, null)
                    sessionJson?.let {
                        try {
                            val serializable = json.decodeFromString<SerializableFocusSession>(it)
                            sessions.add(serializable.toDomain())
                        } catch (e: Exception) {
                            // Ignorar sessões corrompidas
                        }
                    }
                }
            }
            
            emit(sessions.sortedByDescending { it.startTime })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun getFocusSessionsInPeriod(
        startTime: Long, 
        endTime: Long
    ): List<FocusSession> {
        return try {
            val sessions = mutableListOf<FocusSession>()
            val allKeys = prefs.all.keys
            
            for (key in allKeys) {
                if (key.startsWith("session_")) {
                    val sessionJson = prefs.getString(key, null)
                    sessionJson?.let {
                        try {
                            val serializable = json.decodeFromString<SerializableFocusSession>(it)
                            val session = serializable.toDomain()
                            
                            // Verificar se a sessão está no período especificado
                            val sessionStartEpoch = session.startTime.toEpochSecond(
                                java.time.ZoneOffset.systemDefault().rules.getOffset(session.startTime)
                            )
                            
                            if (sessionStartEpoch >= startTime && sessionStartEpoch <= endTime) {
                                sessions.add(session)
                            }
                        } catch (e: Exception) {
                            // Ignorar sessões corrompidas
                        }
                    }
                }
            }
            
            sessions.sortedByDescending { it.startTime }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun deleteFocusSession(sessionId: String): Result<Unit> {
        return try {
            prefs.edit()
                .remove("session_$sessionId")
                .apply()
            
            // Se esta era a sessão ativa, removê-la também
            val activeSession = getActiveFocusSession()
            if (activeSession?.id == sessionId) {
                prefs.edit().remove("active_session").apply()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFocusSessionStats(days: Int): FocusSessionStats {
        return try {
            val endTime = System.currentTimeMillis() / 1000
            val startTime = endTime - (days * 24 * 60 * 60) // days em segundos
            
            val sessions = getFocusSessionsInPeriod(startTime, endTime)
            val completedSessions = sessions.filter { it.isCompleted }
            
            val totalFocusTime = completedSessions.sumOf { 
                it.actualDurationMinutes ?: 0 
            }
            
            val averageDuration = if (completedSessions.isNotEmpty()) {
                totalFocusTime.toDouble() / completedSessions.size
            } else {
                0.0
            }
            
            val successRate = if (sessions.isNotEmpty()) {
                completedSessions.size.toDouble() / sessions.size
            } else {
                0.0
            }
            
            FocusSessionStats(
                totalSessions = sessions.size,
                completedSessions = completedSessions.size,
                totalFocusTimeMinutes = totalFocusTime,
                averageSessionDurationMinutes = averageDuration,
                successRate = successRate
            )
        } catch (e: Exception) {
            FocusSessionStats(0, 0, 0, 0.0, 0.0)
        }
    }
}

/**
 * Versão serializável da FocusSession para armazenamento.
 */
@kotlinx.serialization.Serializable
data class SerializableFocusSession(
    val id: String,
    val startTime: String,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int? = null,
    val endTime: String? = null,
    val isCompleted: Boolean = false,
    val blockedApps: List<String> = emptyList(),
    val sessionType: String = "DEEP_FOCUS"
)

/**
 * Extensões para conversão entre FocusSession e SerializableFocusSession.
 */
private fun FocusSession.toSerializable(): SerializableFocusSession {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    return SerializableFocusSession(
        id = id,
        startTime = startTime.format(formatter),
        plannedDurationMinutes = plannedDurationMinutes,
        actualDurationMinutes = actualDurationMinutes,
        endTime = endTime?.format(formatter),
        isCompleted = isCompleted,
        blockedApps = blockedApps,
        sessionType = sessionType.name
    )
}

private fun SerializableFocusSession.toDomain(): FocusSession {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    return FocusSession(
        id = id,
        startTime = LocalDateTime.parse(startTime, formatter),
        plannedDurationMinutes = plannedDurationMinutes,
        actualDurationMinutes = actualDurationMinutes,
        endTime = endTime?.let { LocalDateTime.parse(it, formatter) },
        isCompleted = isCompleted,
        blockedApps = blockedApps,
        sessionType = try {
            com.zenlauncher.domain.entities.FocusSessionType.valueOf(sessionType)
        } catch (e: Exception) {
            com.zenlauncher.domain.entities.FocusSessionType.DEEP_FOCUS
        }
    )
}
