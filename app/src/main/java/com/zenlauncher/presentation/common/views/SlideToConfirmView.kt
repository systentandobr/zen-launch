package com.zenlauncher.presentation.common.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.zenlauncher.R
import kotlin.math.abs

/**
 * View customizada para confirmação deslizante
 * Usada para confirmar ações críticas com um gesto de deslizar
 */
class SlideToConfirmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnSlideCompleteListener {
        fun onSlideComplete()
    }

    private var listener: OnSlideCompleteListener? = null
    
    // Dimensões e posições
    private var sliderPosition = 0f
    private var sliderWidth = 0f
    private var trackHeight = 0f
    private val sliderPadding = 8f
    
    // Cores
    private var trackColor = Color.parseColor("#333333")
    private var sliderColor = Color.WHITE
    private var textColor = Color.parseColor("#999999")
    
    // Textos
    private var confirmText = "DESLIZE PARA CONFIRMAR"
    
    // Paint objects
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sliderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Touch handling
    private var isDragging = false
    private var startX = 0f
    
    // Animação
    private var resetAnimator: ValueAnimator? = null
    
    init {
        setupPaints()
        
        // Configurar texto customizado se fornecido nos atributos
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.SlideToConfirmView)
            confirmText = typedArray.getString(R.styleable.SlideToConfirmView_confirmText) ?: confirmText
            typedArray.recycle()
        }
    }
    
    private fun setupPaints() {
        trackPaint.apply {
            color = trackColor
            style = Paint.Style.FILL
        }
        
        sliderPaint.apply {
            color = sliderColor
            style = Paint.Style.FILL
        }
        
        textPaint.apply {
            color = textColor
            textSize = 14f * resources.displayMetrics.density
            textAlign = Paint.Align.CENTER
        }
    }
    
    fun setOnSlideCompleteListener(listener: OnSlideCompleteListener) {
        this.listener = listener
    }
    
    fun setConfirmText(text: String) {
        confirmText = text
        invalidate()
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 300 * resources.displayMetrics.density.toInt()
        val desiredHeight = 56 * resources.displayMetrics.density.toInt()
        
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        
        setMeasuredDimension(width, height)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        trackHeight = h.toFloat()
        sliderWidth = h.toFloat() - (sliderPadding * 2)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Desenhar track de fundo
        val trackRect = RectF(0f, 0f, width.toFloat(), trackHeight)
        canvas.drawRoundRect(trackRect, trackHeight / 2, trackHeight / 2, trackPaint)
        
        // Desenhar texto
        val textX = width / 2f
        val textY = height / 2f + (textPaint.textSize / 3)
        
        // Fade do texto baseado na posição do slider
        val textAlpha = 255 * (1 - sliderPosition / (width - sliderWidth - sliderPadding * 2))
        textPaint.alpha = textAlpha.toInt()
        canvas.drawText(confirmText, textX, textY, textPaint)
        
        // Desenhar slider
        val sliderLeft = sliderPadding + sliderPosition
        val sliderTop = sliderPadding
        val sliderRight = sliderLeft + sliderWidth
        val sliderBottom = sliderTop + sliderWidth
        
        val sliderRect = RectF(sliderLeft, sliderTop, sliderRight, sliderBottom)
        canvas.drawOval(sliderRect, sliderPaint)
        
        // Desenhar seta no slider
        drawArrow(canvas, sliderLeft + sliderWidth / 2, height / 2f)
    }
    
    private fun drawArrow(canvas: Canvas, centerX: Float, centerY: Float) {
        val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 2f * resources.displayMetrics.density
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        
        val arrowSize = 12f * resources.displayMetrics.density
        
        canvas.drawLine(
            centerX - arrowSize / 2,
            centerY,
            centerX + arrowSize / 2,
            centerY,
            arrowPaint
        )
        
        canvas.drawLine(
            centerX + arrowSize / 4,
            centerY - arrowSize / 3,
            centerX + arrowSize / 2,
            centerY,
            arrowPaint
        )
        
        canvas.drawLine(
            centerX + arrowSize / 4,
            centerY + arrowSize / 3,
            centerX + arrowSize / 2,
            centerY,
            arrowPaint
        )
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchX = event.x
                val sliderLeft = sliderPadding + sliderPosition
                val sliderRight = sliderLeft + sliderWidth
                
                if (touchX >= sliderLeft && touchX <= sliderRight) {
                    isDragging = true
                    startX = touchX - sliderPosition
                    
                    // Cancelar animação de reset se estiver rodando
                    resetAnimator?.cancel()
                    
                    return true
                }
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val newPosition = event.x - startX
                    val maxPosition = width - sliderWidth - sliderPadding * 2
                    
                    sliderPosition = newPosition.coerceIn(0f, maxPosition)
                    invalidate()
                    
                    // Verificar se completou o deslize
                    if (sliderPosition >= maxPosition * 0.95f) {
                        onSlideComplete()
                    }
                    
                    return true
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    
                    val maxPosition = width - sliderWidth - sliderPadding * 2
                    if (sliderPosition < maxPosition * 0.95f) {
                        animateReset()
                    }
                    
                    return true
                }
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    private fun onSlideComplete() {
        listener?.onSlideComplete()
        
        // Opcional: resetar após completar
        postDelayed({
            animateReset()
        }, 500)
    }
    
    private fun animateReset() {
        resetAnimator = ValueAnimator.ofFloat(sliderPosition, 0f).apply {
            duration = 300
            addUpdateListener { animator ->
                sliderPosition = animator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
    
    fun reset() {
        sliderPosition = 0f
        invalidate()
    }
}
