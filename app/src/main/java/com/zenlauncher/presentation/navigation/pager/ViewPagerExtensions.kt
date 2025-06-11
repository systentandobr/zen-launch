package com.zenlauncher.presentation.navigation.pager

import androidx.viewpager2.widget.ViewPager2
import java.lang.reflect.Field

/**
 * Extensões para ViewPager2 para customizar comportamentos e configurações.
 */
object ViewPagerExtensions {

    /**
     * Define o valor de touchSlop para o ViewPager2.
     * 
     * O touchSlop é a distância mínima necessária para reconhecer
     * um gesto de deslize. Um valor menor torna o deslize mais sensível.
     * 
     * Esta função usa reflexão para acessar as classes internas do ViewPager2
     * e pode não funcionar em todas as versões do Android ou versões do ViewPager2.
     * 
     * @param slop Valor de touchSlop para configurar
     */
    fun ViewPager2.setTouchSlop(slop: Int) {
        try {
            // Obter a referência para o RecyclerView dentro do ViewPager2
            val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(this)

            // Obter a referência para o TouchSlop
            val touchSlopField: Field = recyclerView.javaClass.superclass.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            
            // Definir o novo valor
            touchSlopField.set(recyclerView, slop)
        } catch (e: Exception) {
            // Se ocorrer algum erro, registramos e continuamos
            android.util.Log.e("ViewPagerExt", "Error setting touch slop: ${e.message}")
        }
    }
}
