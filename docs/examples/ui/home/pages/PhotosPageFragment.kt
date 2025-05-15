package com.zenlauncher.ui.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zenlauncher.R

/**
 * Fragment para a página de fotos
 */
class PhotosPageFragment : Fragment() {
    
    private lateinit var addPhotoButton: FloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photos_page, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        addPhotoButton = view.findViewById(R.id.add_photo_button)
        
        setupListeners()
    }
    
    private fun setupListeners() {
        addPhotoButton.setOnClickListener {
            // Implementação futura
        }
    }
    
    companion object {
        fun newInstance() = PhotosPageFragment()
    }
}