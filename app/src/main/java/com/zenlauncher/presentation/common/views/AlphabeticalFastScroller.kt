package com.zenlauncher.presentation.common.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

/**
 * View customizada para rolagem rápida alfabética em RecyclerView
 * Exibe letras de A-Z no lado direito para navegação rápida
 */
class AlphabeticalFastScroller @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val ALPHABET = ('A'..'Z').toList()
        private const val ITEM_HEIGHT_DP = 20f
        private const val ITEM_WIDTH_DP = 30f
        private const val TEXT_SIZE_SP = 12f
    }
    
    interface OnLetterSelectedListener {
        fun onLetterSelected(letter: Char)
    }
    
    private var listener: OnLetterSelectedListener? = null
    private var recyclerView: RecyclerView? = null
    private var selectedLetter: Char? = null
    private var touchedIndex = -1
    
    // Paint objects
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#666666")
        textSize = TEXT_SIZE_SP * resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
    }
    
    private val selectedTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = TEXT_SIZE_SP * resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
    }
    
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1AFFFFFF")
        style = Paint.Style.FILL
    }
    
    // Dimensions
    private val itemHeight = ITEM_HEIGHT_DP * resources.displayMetrics.density
    private val itemWidth = ITEM_WIDTH_DP * resources.displayMetrics.density
    
    fun setOnLetterSelectedListener(listener: OnLetterSelectedListener) {
        this.listener = listener
    }
    
    fun attachToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = itemWidth.toInt()
        val desiredHeight = (ALPHABET.size * itemHeight).toInt()
        
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        
        setMeasuredDimension(width, height)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val availableHeight = height.toFloat()
        val alphabetHeight = ALPHABET.size * itemHeight
        val startY = (availableHeight - alphabetHeight) / 2
        
        ALPHABET.forEachIndexed { index, letter ->
            val y = startY + (index * itemHeight) + itemHeight / 2
            val x = width / 2f
            
            // Desenhar background se tocado
            if (index == touchedIndex) {
                val rect = RectF(
                    0f,
                    y - itemHeight / 2,
                    width.toFloat(),
                    y + itemHeight / 2
                )
                canvas.drawRoundRect(rect, 4f, 4f, backgroundPaint)
            }
            
            // Desenhar letra
            val paint = if (letter == selectedLetter) selectedTextPaint else textPaint
            val textY = y + (paint.textSize / 3)
            canvas.drawText(letter.toString(), x, textY, paint)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val y = event.y
                val availableHeight = height.toFloat()
                val alphabetHeight = ALPHABET.size * itemHeight
                val startY = (availableHeight - alphabetHeight) / 2
                
                if (y >= startY && y <= startY + alphabetHeight) {
                    val index = ((y - startY) / itemHeight).toInt()
                    if (index >= 0 && index < ALPHABET.size) {
                        touchedIndex = index
                        val letter = ALPHABET[index]
                        selectedLetter = letter
                        listener?.onLetterSelected(letter)
                        invalidate()
                        
                        // Scroll RecyclerView to position
                        scrollToLetter(letter)
                    }
                }
                return true
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchedIndex = -1
                invalidate()
                return true
            }
        }
        
        return false
    }
    
    private fun scrollToLetter(letter: Char) {
        recyclerView?.let { rv ->
            val layoutManager = rv.layoutManager as? LinearLayoutManager ?: return
            val adapter = rv.adapter ?: return
            
            // Encontrar primeira posição que começa com a letra
            for (i in 0 until adapter.itemCount) {
                // Esta é uma implementação simplificada
                // Você precisaria de um método no adapter para obter o texto do item
                // ou usar uma interface para isso
                
                // Por enquanto, apenas scroll para uma posição proporcional
                val position = (adapter.itemCount * ALPHABET.indexOf(letter)) / ALPHABET.size
                layoutManager.scrollToPositionWithOffset(position, 0)
                break
            }
        }
    }
    
    fun updateSelectedLetter(letter: Char?) {
        selectedLetter = letter
        invalidate()
    }
}
