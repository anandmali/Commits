package com.anandmali.commits.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.anandmali.commits.R
import com.anandmali.commits.util.dpToPx
import com.anandmali.commits.util.spToPx
import kotlin.math.abs
import kotlin.math.min

class BarChartViewClean(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var barAnimatePercentList: ArrayList<Float> = ArrayList()
    private var barToFillPercentList: ArrayList<Float> = ArrayList()
    private var mMaxValue: Int = 0
    private var mMonthTextList: ArrayList<String> = ArrayList()
    private var currentPercentage = 0

    private val mBarBackgroundPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.bar_background)
    }

    private val mBarForegroundPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.teal_200)
    }

    private val mMonthTextPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.month)
        textSize = 10.spToPx.toFloat()
        textAlign = Paint.Align.CENTER
    }

    fun setBottomTextList(bottomStringList: ArrayList<String>) {
        mMonthTextList.addAll(bottomStringList)
        val r = Rect()
        bottomTextDescent = 0 //Can be cleaned up

        for (s in mMonthTextList) {

            mMonthTextPaint.getTextBounds(s, 0, s.length, r)

            //Below evaluation is to make month text and bar rect align together when drawn
            //Set month text height to the max value
            if (bottomTextHeight < r.height()) {
                bottomTextHeight = r.height()
            }

            //Set bar rect width to the max value,
            if (barWidth < r.width()) {
                barWidth = r.width()
            }

            ///Making sure month text bottom (y-axis) align to bottom of the view
            if (bottomTextDescent < abs(r.bottom)) {
                bottomTextDescent = abs(r.bottom)
            }
        }

        minimumWidth = 2
        postInvalidate()
    }

    fun setDataList(list: ArrayList<Int>, max: Int) {
        mMaxValue = max

        if (mMaxValue == 0)
            mMaxValue = 1

        for (integer in list) {
            barToFillPercentList.add(1 - integer.toFloat() / mMaxValue.toFloat())
        }

        if (barAnimatePercentList.isEmpty() || barAnimatePercentList.size < barToFillPercentList.size) {
            val temp = barToFillPercentList.size - barAnimatePercentList.size
            for (i in 0 until temp) {
                barAnimatePercentList.add(1f)
            }
        } else if (barAnimatePercentList.size > barToFillPercentList.size) {
            val temp = barAnimatePercentList.size - barToFillPercentList.size
            for (i in 0 until temp) {
                barAnimatePercentList.removeAt(barToFillPercentList.size - 1)
            }
        }

        minimumWidth = 2
        removeCallbacks(animator)
        post(animator)
    }

    override fun onDraw(canvas: Canvas) {
        if (barAnimatePercentList.isNotEmpty()) {
            barAnimatePercentList.forEachIndexed { index, barToFillValue ->
                drawBackgroundBar(canvas, index)
                drawForegroundBar(canvas, index, barToFillValue)
            }

            if (mMonthTextList.isNotEmpty()) {
                mMonthTextList.forEachIndexed { index, month ->
                    drawMonthText(canvas, index, month)
                }
            }
        }
    }

    private fun drawMonthText(canvas: Canvas, index: Int, month: String) {
        canvas.drawText(
            "$month commits",
            ((BAR_SIDE_MARGIN * (index + 1)) + (barWidth * (index)) + barWidth / 2).toFloat(),
            (height - bottomTextDescent).toFloat(),
            mMonthTextPaint
        )
    }

    private fun drawForegroundBar(canvas: Canvas, index: Int, barToFillValue: Float) {
        canvas.drawRect(
            ((BAR_SIDE_MARGIN * (index + 1)) + (barWidth * (index))).toFloat(),
            (topMargin + ((height - topMargin - bottomTextHeight - TEXT_TOP_MARGIN) * barToFillValue).toInt()).toFloat(),
            ((BAR_SIDE_MARGIN + barWidth) * (index + 1)).toFloat(),
            (height - bottomTextHeight - TEXT_TOP_MARGIN).toFloat(),
            mBarForegroundPaint
        )
    }

    private fun drawBackgroundBar(canvas: Canvas, index: Int) {
        canvas.drawRect(
            ((BAR_SIDE_MARGIN * (index + 1)) + (barWidth * (index))).toFloat(),
            (topMargin + TEXT_TOP_MARGIN + bottomTextHeight).toFloat(),
            ((BAR_SIDE_MARGIN + barWidth) * (index + 1)).toFloat(),
            (height - bottomTextHeight - TEXT_TOP_MARGIN).toFloat(),
            mBarBackgroundPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mViewWidth = measureWidth(widthMeasureSpec)
        val mViewHeight = measureHeight(heightMeasureSpec)
        setMeasuredDimension(mViewWidth, mViewHeight)
    }

    private fun measureWidth(measureSpec: Int): Int {
        var preferred = 0
        if (mMonthTextList.isNotEmpty()) {
            preferred = mMonthTextList.size * (barWidth + BAR_SIDE_MARGIN)
        }
        return getMeasurement(measureSpec, preferred)
    }

    private fun measureHeight(measureSpec: Int): Int {
        val preferred = 222
        return getMeasurement(measureSpec, preferred)
    }

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> min(preferred, specSize)
            else -> preferred
        }
    }

    private val animator: Runnable = object : Runnable {
        override fun run() {
            var needNewFrame = false

            for (i in barToFillPercentList.indices) {

                if (barAnimatePercentList[i] < barToFillPercentList[i]) {
                    barAnimatePercentList[i] = barAnimatePercentList[i] + 0.02f
                    needNewFrame = true
                } else if (barAnimatePercentList[i] > barToFillPercentList[i]) {
                    barAnimatePercentList[i] = barAnimatePercentList[i] - 0.02f
                    needNewFrame = true
                }

                if (abs(barToFillPercentList[i] - barAnimatePercentList[i]) < 0.02f) {
                    barAnimatePercentList[i] = barToFillPercentList[i]
                }
            }

            if (needNewFrame) {
                postDelayed(this, 20)
            }
            invalidate()
        }
    }


    companion object {
        const val PERCENTAGE_VALUE_HOLDER = "percentage"
        val topMargin = 5.dpToPx
        var barWidth = 22.dpToPx
        val BAR_SIDE_MARGIN = 22.dpToPx
        val TEXT_TOP_MARGIN = 5.dpToPx
        var bottomTextHeight = 0
        var bottomTextDescent = 0
    }
}












