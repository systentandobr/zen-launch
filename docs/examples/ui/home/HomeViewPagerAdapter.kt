package com.zenlauncher.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zenlauncher.ui.home.pages.ClockPageFragment
import com.zenlauncher.ui.home.pages.PhotosPageFragment
import com.zenlauncher.ui.home.pages.ToolsPageFragment
import com.zenlauncher.ui.home.pages.WidgetsPageFragment

/**
 * Adaptador para gerenciar as páginas do ViewPager na tela inicial
 */
class HomeViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val PAGE_CLOCK = 0
        const val PAGE_WIDGETS = 1
        const val PAGE_PHOTOS = 2
        const val PAGE_TOOLS = 3
        
        const val PAGE_COUNT = 4
    }

    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_CLOCK -> ClockPageFragment.newInstance()
            PAGE_WIDGETS -> WidgetsPageFragment.newInstance()
            PAGE_PHOTOS -> PhotosPageFragment.newInstance()
            PAGE_TOOLS -> ToolsPageFragment.newInstance()
            else -> ClockPageFragment.newInstance()
        }
    }
}