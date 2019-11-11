package com.luza.indicatorseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.ContextCompat
import kotlin.math.abs

class IndicatorSeekbar @JvmOverloads constructor(
    context: Context, var attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "LuzaIndicatorSeekbar"
    }

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var viewWidth = 0
    private var viewHeight = 0
    private var realSeekbarWidth = 0
    private var realLeft = 0f
    private var realRight = 0f

    private var drawableThumb: Drawable? = null
    private var drawableIndicator: Drawable? = null
    private var drawableIndicatorProgress: Drawable? = null

    private var sizeSeekBar = 0
    private var sizeTextIndicator = 0
    private var sizeTextValue = 0
    private var sizeThumb = 0
    private var sizeTouchExtraArea = 0

    private var indicatorWidth = 0
    private var indicatorHeight = 0
    private var spaceBetweenIndicator = 0f
    private var spaceBetweenTextValueToBar = 0
    private var spaceBetweenTextIndicatorToBar = 0

    private var seekBarCorners = 0

    private var paintTextValue = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var paintTextIndicator = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var paintSeekbar = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintSeekbarProgress = Paint(Paint.ANTI_ALIAS_FLAG)

    private var rectThumb = RectF()
    private var rectIndicator = RectF()
    private var rectSeekbar = RectF()
    private var rectSeekbarProgress = RectF()
    private var rectText = Rect()

    private var max = 100
    private var min = 0
    private var progress = 50
    private var numberIndicator = 5
    private var isShowProgressValue = false
    private var isShowIndicatorText = false

    var isSeekByUser = false
    private var isSeeking = false
    private val pointDown = PointF(0f, 0f)

    private var listener: ISeekbarListener? = null
    private var textIndicatorUnit = ""

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.IndicatorSeekbar)
            drawableThumb =
                ta.getDrawable(R.styleable.IndicatorSeekbar_is_thumb) ?: ContextCompat.getDrawable(
                    context,
                    R.drawable.thumb_default
                )
            drawableIndicator = ta.getDrawable(R.styleable.IndicatorSeekbar_is_indicator)
                ?: ContextCompat.getDrawable(context, R.drawable.indicator_default)
            drawableIndicatorProgress =
                ta.getDrawable(R.styleable.IndicatorSeekbar_is_indicator_progress)
                    ?: ContextCompat.getDrawable(context, R.drawable.indicator_progress)

            sizeThumb = ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_thumb_size, 10)
            indicatorWidth =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_indicator_width, 10)
            indicatorHeight =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_indicator_height, 20)
            sizeSeekBar =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_seekbar_height, 20)
            seekBarCorners =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_seekbar_corners, 0)
            sizeTouchExtraArea =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_thumb_touch_extra_area, 0)

            paintTextValue.textSize =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_text_value_size, 20)
                    .toFloat()
            paintTextIndicator.textSize =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_text_indicator_size, 20)
                    .toFloat()

            paintSeekbar.color =
                ta.getColor(R.styleable.IndicatorSeekbar_is_seekbar_color, Color.GREEN)
            paintSeekbarProgress.color =
                ta.getColor(R.styleable.IndicatorSeekbar_is_seekbar_progress_color, Color.RED)
            paintTextIndicator.color = ta.getColor(
                R.styleable.IndicatorSeekbar_is_text_indicator_color,
                Color.BLACK
            )
            paintTextValue.color = ta.getColor(
                R.styleable.IndicatorSeekbar_is_text_value_color,
                Color.BLACK
            )

            spaceBetweenTextIndicatorToBar = ta.getDimensionPixelSize(
                R.styleable.IndicatorSeekbar_is_space_text_indicator_to_bar,
                0
            )
            spaceBetweenTextValueToBar =
                ta.getDimensionPixelSize(R.styleable.IndicatorSeekbar_is_space_text_value_to_bar, 0)

            isShowIndicatorText =
                ta.getBoolean(R.styleable.IndicatorSeekbar_is_show_indicator_text, false)
            isShowProgressValue =
                ta.getBoolean(R.styleable.IndicatorSeekbar_is_show_progress_value, false)

            textIndicatorUnit =
                ta.getString(R.styleable.IndicatorSeekbar_is_text_indicator_unit) ?: ""
            min = ta.getInt(R.styleable.IndicatorSeekbar_is_min, 0)
            max = ta.getInt(R.styleable.IndicatorSeekbar_is_max, 100)
            progress = ta.getInt(R.styleable.IndicatorSeekbar_is_progress, 0)
            numberIndicator = ta.getInt(R.styleable.IndicatorSeekbar_is_number_indicator, 5)
            if (numberIndicator < 0)
                numberIndicator = 5
            ta.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        realLeft = 0f + paddingLeft
        realRight = viewWidth.toFloat() - paddingRight
        if (viewWidth != 0 && viewHeight != 0) {
            val centerView = viewHeight / 2f
            realSeekbarWidth = viewWidth - paddingLeft - paddingRight - sizeThumb
            spaceBetweenIndicator =
                ((realSeekbarWidth - (numberIndicator - 1) * indicatorWidth) / (numberIndicator - 1)).toFloat()


            rectSeekbar.left = realLeft + sizeThumb / 2
            rectSeekbar.right = rectSeekbar.left + realSeekbarWidth
            rectSeekbar.top = centerView - sizeSeekBar / 2f
            rectSeekbar.bottom = centerView + sizeSeekBar / 2f

            rectSeekbarProgress.left = realLeft + sizeThumb / 2
            rectSeekbarProgress.top = centerView - sizeSeekBar / 2f
            rectSeekbarProgress.bottom = centerView + sizeSeekBar / 2f

            rectIndicator.top = centerView - indicatorHeight / 2f
            rectIndicator.bottom = centerView + indicatorHeight / 2f

            rectThumb.set(
                realLeft,
                centerView - sizeThumb / 2,
                realLeft + sizeThumb,
                centerView + sizeThumb / 2
            )
            invalidateWithCurrentDataSeekbar()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun Drawable.drawAt(rectf: RectF, canvas: Canvas) {
        this.setBounds(
            rectf.left.toInt(),
            rectf.top.toInt(),
            rectf.right.toInt(),
            rectf.bottom.toInt()
        )
        draw(canvas)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas?.let { canvas ->
            canvas.drawRoundRect(
                rectSeekbar,
                seekBarCorners.toFloat(), seekBarCorners.toFloat(), paintSeekbar
            )
            canvas.drawRoundRect(
                rectSeekbarProgress,
                seekBarCorners.toFloat(), seekBarCorners.toFloat(), paintSeekbarProgress
            )
            var offset = rectSeekbar.left - indicatorWidth / 2
            for (i in 0 until numberIndicator) {
                val right = offset + indicatorWidth
                val drawable = if (offset + indicatorWidth / 2 < rectThumb.left + sizeThumb / 2)
                    drawableIndicatorProgress
                else
                    drawableIndicator
                drawable?.drawAt(
                    RectF(
                        offset,
                        rectIndicator.top,
                        right,
                        rectIndicator.bottom
                    ), canvas
                )
                if (isShowIndicatorText) {
                    val text =
                        (i.toFloat() / (numberIndicator - 1) * (max - min) + min).toInt().toString() + textIndicatorUnit
                    paintTextIndicator.getTextBounds(text, 0, text.length, rectText)
                    val xText = offset + indicatorWidth / 2 - rectText.width() / 2
                    val yText =
                        rectThumb.bottom + spaceBetweenTextIndicatorToBar + rectText.height()
                    canvas.drawText(text, xText, yText, paintTextIndicator)
                }
                offset += spaceBetweenIndicator + indicatorWidth
            }
            drawableThumb?.drawAt(rectThumb, canvas)

            if (isShowProgressValue) {
                val sProgress = progress.toString()
                paintTextValue.getTextBounds(sProgress, 0, sProgress.length, rectText)
                val xText = rectThumb.left + sizeThumb / 2 - rectText.width() / 2
                val yText = rectThumb.top - spaceBetweenTextValueToBar
                canvas.drawText(sProgress, xText, yText, paintTextValue)
            }
        }
    }

    fun setListener(listener: ISeekbarListener) {
        this.listener = listener
    }

    fun setProgress(progress: Int) {
        var newProgress = progress
        if (newProgress < min)
            newProgress = min
        if (newProgress > max)
            newProgress = max
        this.progress = newProgress
        listener?.onSeeking(progress)
        listener?.onStopSeeking(progress)
        invalidateWithCurrentDataSeekbar()
        invalidate()
    }

    fun setRange(min: Int, max: Int) {
        this.min = min
        this.max = max
        if (min >= max) {
            this.min = 0
            this.max = 100
        }
        if (progress !in this.min..this.max) {
            progress = 0
            listener?.onSeeking(progress)
            listener?.onStopSeeking(progress)
        }
        invalidateWithCurrentDataSeekbar()
        invalidate()
    }

    fun getMax() = max
    fun getMin() = min
    fun getProgress() = progress

    /**
     * Progress to dimen
     */
    private fun Int.ToDimension() =
        (this - min).toFloat() / (max - min) * realSeekbarWidth + realLeft

    private fun Float.ToProgress() =
        ((this - realLeft) / realSeekbarWidth * (max - min) + min).toInt()

    private fun invalidateWithCurrentDataSeekbar() {
        invalidateThumbWithProgress()
        invalidateProgressTrack()
    }

    private fun invalidateThumbWithProgress() {
        rectThumb.left = progress.ToDimension()
        rectThumb.right = rectThumb.left + sizeThumb
    }

    private fun invalidateProgressTrack() {
        rectSeekbarProgress.right = rectThumb.left + sizeThumb / 2
    }

    private fun setThumbPosition(posLeft: Float) {
        var left = posLeft
        if (posLeft < realLeft)
            left = realLeft
        else if (posLeft > realRight - sizeThumb)
            left = realRight - sizeThumb
        rectThumb.left = left
        rectThumb.right = left + sizeThumb
        invalidateProgressTrack()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return super.onTouchEvent(event)
        return when (event.actionMasked) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                listener?.onStopSeeking(progress)
                isSeeking = false
                isSeekByUser = false
                false
            }
            MotionEvent.ACTION_DOWN -> {
                pointDown.x = event.x
                isSeekByUser = true
                if (pointDown.x !in rectThumb.left - sizeTouchExtraArea..rectThumb.right + sizeTouchExtraArea) {
                    onSeek((pointDown.x - rectThumb.left).toInt())
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val xDiff = (event.x - pointDown.x).toInt()
                pointDown.x = event.x
                if (isSeeking) {
                    if (isSeekByUser) {
                        onSeek(xDiff)
                    }
                    true
                } else {
                    if (abs(xDiff) >= touchSlop) {
                        isSeeking = true
                        true
                    } else
                        false
                }
            }
            else -> {
                false
            }
        }
    }

    private fun onSeek(distance: Int) {
        setThumbPosition(rectThumb.left + distance)
        this.progress = rectThumb.left.ToProgress()
        invalidate()
        listener?.onSeeking(progress)
    }

    interface ISeekbarListener {
        fun onSeeking(progress: Int) {}
        fun onStopSeeking(progress: Int) {}
    }
}