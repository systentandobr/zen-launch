package com.zenlauncher.data.extensions

import android.app.usage.UsageEvents

/**
 * Extensões para a classe UsageEvents.Event
 */
object UsageEventsExtensions {
    
    /**
     * Copia os dados de um evento para outro.
     * Esta é uma implementação alternativa, já que algumas versões do Android
     * podem não ter o método copyFrom() implementado na classe UsageEvents.Event.
     * 
     * @param source Evento de origem
     */
    fun UsageEvents.Event.copyFromEvent(source: UsageEvents.Event) {
        // Não podemos acessar diretamente os campos privados da classe UsageEvents.Event
        // então vamos usar a reflexão para copiar os valores
        try {
            // Usando reflexão para obter e definir os campos
            UsageEvents.Event::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                val value = field.get(source)
                field.set(this, value)
                field.isAccessible = false
            }
        } catch (e: Exception) {
            // Em caso de erro, apenas registramos o erro e continuamos
            android.util.Log.e("UsageEventsExt", "Failed to copy event: ${e.message}")
        }
    }
}
