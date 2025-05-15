package com.zenlauncher.ui.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zenlauncher.R
import com.zenlauncher.domain.model.LauncherWidget
import com.zenlauncher.ui.common.ViewModelFactory
import com.zenlauncher.ui.widget.WidgetHostView
import com.zenlauncher.ui.widget.WidgetViewModel

/**
 * Fragment para a página de widgets
 */
class WidgetsPageFragment : Fragment() {
    
    private lateinit var widgetViewModel: WidgetViewModel
    private lateinit var widgetContainer: FrameLayout
    private lateinit var addWidgetButton: FloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_widgets_page, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        widgetContainer = view.findViewById(R.id.widget_container)
        addWidgetButton = view.findViewById(R.id.add_widget_button)
        
        setupViewModel()
        setupListeners()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        val factory = ViewModelFactory(requireActivity().application)
        widgetViewModel = ViewModelProvider(requireActivity(), factory)[WidgetViewModel::class.java]
    }
    
    private fun setupListeners() {
        addWidgetButton.setOnClickListener {
            navigateToWidgetPicker()
        }
    }
    
    private fun observeViewModel() {
        widgetViewModel.widgets.observe(viewLifecycleOwner) { widgets ->
            updateWidgets(widgets)
        }
    }
    
    /**
     * Navega para o seletor de widgets
     */
    private fun navigateToWidgetPicker() {
        findNavController().navigate(R.id.action_homeFragment_to_widgetPickerFragment)
    }
    
    /**
     * Atualiza os widgets na tela
     */
    private fun updateWidgets(widgets: List<LauncherWidget>) {
        // Remove todos os widgets antigos
        for (i in 0 until widgetContainer.childCount) {
            val child = widgetContainer.getChildAt(i)
            if (child is WidgetHostView) {
                widgetContainer.removeView(child)
            }
        }
        
        // Adiciona os widgets atualizados
        widgets.forEach { widget ->
            // Cria a view do widget
            val appWidgetId = widget.appWidgetId
            val widgetView = widgetViewModel.createView(appWidgetId)
            
            // Cria o container para o widget
            val hostView = WidgetHostView(requireContext()).apply {
                // Define a posição e tamanho na grade
                setGridPosition(widget.x, widget.y)
                setGridSize(widget.spanX, widget.spanY)
                
                // Adiciona a view do widget
                addWidgetView(widgetView)
                
                // Define os callbacks
                onWidgetMoved = { x, y ->
                    widgetViewModel.updateWidgetPosition(appWidgetId, x, y)
                }
                
                onWidgetResized = { spanX, spanY ->
                    widgetViewModel.updateWidgetSize(appWidgetId, spanX, spanY)
                }
            }
            
            // Adiciona o container à tela
            widgetContainer.addView(hostView)
        }
    }
    
    override fun onStart() {
        super.onStart()
        widgetViewModel.startListening()
    }
    
    override fun onStop() {
        super.onStop()
        widgetViewModel.stopListening()
    }
    
    companion object {
        fun newInstance() = WidgetsPageFragment()
    }
}