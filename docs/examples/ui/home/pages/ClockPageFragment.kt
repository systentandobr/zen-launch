package com.zenlauncher.ui.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextClock
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zenlauncher.R
import com.zenlauncher.ui.common.ViewModelFactory
import com.zenlauncher.ui.home.HomeViewModel

/**
 * Fragment para a página do relógio
 */
class ClockPageFragment : Fragment() {
    
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainClock: TextClock
    private lateinit var dateDisplay: TextClock
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clock_page, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mainClock = view.findViewById(R.id.main_clock)
        dateDisplay = view.findViewById(R.id.date_display)
        
        setupViewModel()
        setupClockClickListener()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        val factory = ViewModelFactory(requireActivity().application)
        homeViewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
    }
    
    private fun setupClockClickListener() {
        mainClock.setOnClickListener {
            homeViewModel.toggleClockFormat()
        }
    }
    
    private fun observeViewModel() {
        homeViewModel.use24HourFormat.observe(viewLifecycleOwner) { use24Hour ->
            updateClockFormat(use24Hour)
        }
    }
    
    private fun updateClockFormat(use24Hour: Boolean) {
        if (use24Hour) {
            mainClock.format12Hour = null
            mainClock.format24Hour = "HH:mm"
            dateDisplay.format12Hour = null
            dateDisplay.format24Hour = "dd 'de' MMMM 'de' yyyy"
        } else {
            mainClock.format24Hour = null
            mainClock.format12Hour = "hh:mm"
            dateDisplay.format24Hour = null
            dateDisplay.format12Hour = "dd 'de' MMMM 'de' yyyy"
        }
    }
    
    companion object {
        fun newInstance() = ClockPageFragment()
    }
}