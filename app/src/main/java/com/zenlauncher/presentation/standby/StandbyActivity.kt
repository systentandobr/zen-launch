package com.zenlauncher.presentation.standby

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zenlauncher.R
import com.zenlauncher.databinding.ActivityStandbyBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade exibida quando o dispositivo está carregando.
 * 
 * Esta tela apresenta um modo de espera (standby) minimalista
 * que é exibido quando o dispositivo está conectado ao carregador.
 */
@AndroidEntryPoint
class StandbyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStandbyBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStandbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Carregar o fragmento apenas se for a primeira criação
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StandbyFragment())
                .commit()
        }
        
        setupUI()
    }
    
    private fun setupUI() {
        // Configurar tela completa
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        
        // Configurar ação de retorno ao launcher
        binding.root.setOnClickListener {
            finish()
        }
    }


    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // Retornar ao launcher
        finish()
    }
}
