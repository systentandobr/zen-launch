package com.zenlauncher.presentation.navigation.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zenlauncher.presentation.apps.AppsFragment
import com.zenlauncher.presentation.focus.FocusFragment
import com.zenlauncher.presentation.home.HomeFragment
import com.zenlauncher.presentation.stats.StatsFragment

/**
 * Adaptador para o ViewPager2 que gerencia os fragmentos principais do MindfulLauncher.
 * 
 * Este adaptador é responsável por criar, gerenciar e reutilizar os fragmentos
 * exibidos no ViewPager2, permitindo navegação por deslize entre as páginas.
 */
class MainPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {
    
    /**
     * Define os tipos de páginas disponíveis no ViewPager.
     */
    enum class Page {
        HOME,
        APPS,
        FOCUS,
        STATS;
        
        companion object {
            /**
             * Obtém a página a partir do índice.
             */
            fun fromPosition(position: Int): Page {
                return values()[position]
            }
        }
    }
    
    /**
     * Retorna o número total de páginas no ViewPager.
     */
    override fun getItemCount(): Int = Page.values().size
    
    /**
     * Cria um novo fragmento para a posição especificada.
     */
    override fun createFragment(position: Int): Fragment {
        return when (Page.fromPosition(position)) {
            Page.HOME -> HomeFragment()
            Page.APPS -> AppsFragment()
            Page.FOCUS -> FocusFragment()
            Page.STATS -> StatsFragment()
        }
    }
    
    /**
     * Obtém o índice de uma página específica.
     */
    fun getPositionForPage(page: Page): Int {
        return page.ordinal
    }
}
