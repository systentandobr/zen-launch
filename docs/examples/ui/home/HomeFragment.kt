package com.zenlauncher.ui.home

import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zenlauncher.R
import com.zenlauncher.data.model.AppInfo
import com.zenlauncher.ui.common.ViewModelFactory

/**
 * Fragment da tela inicial do launcher
 */
class HomeFragment : Fragment() {
    
    private lateinit var homeViewModel: HomeViewModel
    
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var dockLayout: LinearLayout
    private lateinit var appDrawerButton: ImageView
    private lateinit var swipeIndicator: View
    private lateinit var viewPager: ViewPager2
    private lateinit var topMenuChips: ChipGroup
    
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var quickSettingsSheet: BottomSheetBehavior<View>
    private lateinit var quickSettingsView: View
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        coordinatorLayout = view as CoordinatorLayout
        dockLayout = view.findViewById(R.id.dock_apps)
        appDrawerButton = view.findViewById(R.id.app_drawer_button)
        swipeIndicator = view.findViewById(R.id.swipe_indicator)
        viewPager = view.findViewById(R.id.view_pager)
        topMenuChips = view.findViewById(R.id.top_menu_chips)
        
        setupViewModels()
        setupGestureDetector()
        setupQuickSettings()
        setupViewPager()
        setupListeners()
        observeViewModels()
    }
    
    private fun setupViewModels() {
        val factory = ViewModelFactory(requireActivity().application)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }
    
    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            private val swipeThreshold = 100
            private val swipeVelocityThreshold = 100
            
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
            
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                
                // Swipe vertical para cima (abrir menu rápido)
                if (Math.abs(diffY) > Math.abs(diffX) && 
                    Math.abs(diffY) > swipeThreshold && 
                    Math.abs(velocityY) > swipeVelocityThreshold &&
                    diffY < 0) {
                    
                    openQuickSettings()
                    return true
                }
                
                // Swipe vertical para baixo (abrir gaveta de apps)
                if (Math.abs(diffY) > Math.abs(diffX) && 
                    Math.abs(diffY) > swipeThreshold && 
                    Math.abs(velocityY) > swipeVelocityThreshold &&
                    diffY > 0) {
                    
                    navigateToAppDrawer()
                    return true
                }
                
                return false
            }
        })
        
        coordinatorLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }
    
    private fun setupQuickSettings() {
        // Obtém a referência para o Bottom Sheet já incluído no layout
        val quickSettingsView = view?.findViewById<View>(R.id.quick_settings) ?: return
        this.quickSettingsView = quickSettingsView
        
        // Inicializa o BottomSheetBehavior
        quickSettingsSheet = BottomSheetBehavior.from(quickSettingsView)
        
        // Configuração explícita para garantir que o Bottom Sheet seja escondível
        quickSettingsSheet.isHideable = true
        quickSettingsSheet.state = BottomSheetBehavior.STATE_HIDDEN
        quickSettingsSheet.isDraggable = true
        quickSettingsSheet.peekHeight = 0 // Não mostrar nenhuma parte quando estiver escondido
        
        // Configurar o drag handle para permitir arrastar o painel
        val dragHandle = quickSettingsView.findViewById<View>(R.id.drag_handle)
        dragHandle?.setOnClickListener {
            if (quickSettingsSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                closeQuickSettings()
            } else {
                openQuickSettings()
            }
        }
        
        // Configurar comportamento de toque no quickSettingsView para evitar cliques passarem para a view abaixo
        quickSettingsView.setOnClickListener { /* Intercepta cliques */ }
        
        quickSettingsSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    swipeIndicator.visibility = View.VISIBLE
                } else {
                    swipeIndicator.visibility = View.GONE
                }
            }
            
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Efeito de fade no indicador
                if (slideOffset > -0.9) {
                    swipeIndicator.alpha = 1 - ((slideOffset + 0.9f) / 0.9f).coerceIn(0f, 1f)
                }
            }
        })
        
        // Configurar listeners do menu rápido
        setupQuickSettingsListeners()
    }
    
    private fun setupViewPager() {
        // Configura o adaptador do ViewPager
        val adapter = HomeViewPagerAdapter(requireActivity())
        viewPager.adapter = adapter
        
        // Carrega os apps favoritos para o dock
        homeViewModel.loadFavoriteApps()
        
        // Configura a sincronização entre o menu superior e o ViewPager
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val chipId = when (position) {
                    HomeViewPagerAdapter.PAGE_CLOCK -> R.id.menu_clock
                    HomeViewPagerAdapter.PAGE_WIDGETS -> R.id.menu_widgets
                    HomeViewPagerAdapter.PAGE_PHOTOS -> R.id.menu_photos
                    HomeViewPagerAdapter.PAGE_TOOLS -> R.id.menu_tools
                    else -> R.id.menu_clock
                }
                topMenuChips.check(chipId)
            }
        })
        
        // Define a página inicial
        viewPager.setCurrentItem(HomeViewPagerAdapter.PAGE_CLOCK, false)
    }
    
    /**
     * Configura os listeners para os elementos do menu rápido
     */
    private fun setupQuickSettingsListeners() {
        // Chips de navegação
        quickSettingsView.findViewById<Chip>(R.id.clock_chip)?.setOnClickListener {
            closeQuickSettings()
            navigateToClockPicker()
        }
        
        quickSettingsView.findViewById<Chip>(R.id.widgets_chip)?.setOnClickListener {
            closeQuickSettings()
            navigateToWidgetPicker()
        }
        
        quickSettingsView.findViewById<Chip>(R.id.settings_chip)?.setOnClickListener {
            closeQuickSettings()
            navigateToSettings()
        }
        
        quickSettingsView.findViewById<Chip>(R.id.wallpaper_chip)?.setOnClickListener {
            closeQuickSettings()
            openWallpaperPicker()
        }
        
        // Switches de configurações
        quickSettingsView.findViewById<SwitchMaterial>(R.id.clock_format_switch)?.apply {
            isChecked = homeViewModel.use24HourFormat.value ?: true
            setOnCheckedChangeListener { _, isChecked ->
                homeViewModel.toggleClockFormat()
            }
        }
        
        quickSettingsView.findViewById<SwitchMaterial>(R.id.dark_mode_switch)?.apply {
            isChecked = homeViewModel.darkThemeEnabled.value ?: true
            setOnCheckedChangeListener { _, isChecked ->
                homeViewModel.toggleDarkTheme()
            }
        }
    }
    
    private fun setupListeners() {
        // Abre a gaveta de aplicativos ao clicar no botão
        appDrawerButton.setOnClickListener {
            navigateToAppDrawer()
        }
        
        // Toque no indicador para abrir as configurações rápidas
        swipeIndicator.setOnClickListener {
            openQuickSettings()
        }
        
        // Configurar os listeners dos chips do menu superior
        view?.findViewById<Chip>(R.id.menu_clock)?.setOnClickListener {
            viewPager.currentItem = HomeViewPagerAdapter.PAGE_CLOCK
        }
        
        view?.findViewById<Chip>(R.id.menu_widgets)?.setOnClickListener {
            viewPager.currentItem = HomeViewPagerAdapter.PAGE_WIDGETS
        }
        
        view?.findViewById<Chip>(R.id.menu_photos)?.setOnClickListener {
            viewPager.currentItem = HomeViewPagerAdapter.PAGE_PHOTOS
        }
        
        view?.findViewById<Chip>(R.id.menu_tools)?.setOnClickListener {
            viewPager.currentItem = HomeViewPagerAdapter.PAGE_TOOLS
        }
    }
    
    private fun observeViewModels() {
        // Observa os aplicativos favoritos
        homeViewModel.favoriteApps.observe(viewLifecycleOwner) { favoriteApps ->
            updateDockApps(favoriteApps)
        }
        
        // Observa o formato do relógio
        homeViewModel.use24HourFormat.observe(viewLifecycleOwner) { use24Hour ->
            // Atualiza o switch no menu rápido
            quickSettingsView.findViewById<SwitchMaterial>(R.id.clock_format_switch)?.isChecked = use24Hour
        }
        
        // Observa o tema escuro
        homeViewModel.darkThemeEnabled.observe(viewLifecycleOwner) { darkEnabled ->
            // Atualiza o switch no menu rápido
            quickSettingsView.findViewById<SwitchMaterial>(R.id.dark_mode_switch)?.isChecked = darkEnabled
        }
    }
    
    /**
     * Atualiza os ícones do dock
     */
    private fun updateDockApps(apps: List<AppInfo>) {
        dockLayout.removeAllViews()
        
        // Adiciona cada aplicativo como um ícone
        apps.forEach { app ->
            val iconSize = resources.getDimensionPixelSize(R.dimen.dock_icon_size)
            val iconMargin = resources.getDimensionPixelSize(R.dimen.dock_icon_margin)
            
            val iconView = ImageView(requireContext()).apply {
                setImageDrawable(app.icon)
                layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                    marginStart = iconMargin
                    marginEnd = iconMargin
                }
                contentDescription = app.label
                // Aplicar tint branco para manter consistência com o tema escuro
                setColorFilter(ContextCompat.getColor(requireContext(), R.color.icon_light))
                setOnClickListener {
                    homeViewModel.launchApp(app.packageName)
                }
            }
            
            dockLayout.addView(iconView)
        }
    }
    
    /**
     * Abre o menu de configurações rápidas
     */
    private fun openQuickSettings() {
        quickSettingsSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }
    
    /**
     * Fecha o menu de configurações rápidas
     */
    private fun closeQuickSettings() {
        quickSettingsSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }
    
    /**
     * Navega para a gaveta de aplicativos
     */
    private fun navigateToAppDrawer() {
        findNavController().navigate(R.id.action_homeFragment_to_appListFragment)
    }
    
    /**
     * Navega para o seletor de widgets
     */
    private fun navigateToWidgetPicker() {
        findNavController().navigate(R.id.action_homeFragment_to_widgetPickerFragment)
    }
    
    /**
     * Navega para o seletor de relógios
     */
    private fun navigateToClockPicker() {
        findNavController().navigate(R.id.action_homeFragment_to_clockPickerFragment)
    }
    
    /**
     * Navega para as configurações
     */
    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
    }
    
    /**
     * Abre o seletor de papel de parede do sistema
     */
    private fun openWallpaperPicker() {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        try {
            val intent = wallpaperManager.getCropAndSetWallpaperIntent(null)
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback para o seletor de papel de parede padrão
            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
            startActivity(Intent.createChooser(intent, getString(R.string.wallpaper)))
        }
    }
    
    override fun onResume() {
        super.onResume()
        homeViewModel.loadFavoriteApps()
    }
    
    companion object {
        fun newInstance() = HomeFragment()
    }
}