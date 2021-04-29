package com.anandmali.commits.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.anandmali.commits.R
import com.anandmali.commits.util.dpToPx
import com.anandmali.commits.util.spToPx
import kotlin.math.abs
import kotlin.math.min

class BarChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var barAnimatePercentList: ArrayList<Float> = ArrayList()
    private var barToFillPercentList: ArrayList<Float> = ArrayList()
    private var monthTextList: ArrayList<String> = ArrayList()
    private var countTextList: ArrayList<Int> = ArrayList()

    private var barWidth = 22.dpToPx
    private var barSideMargin = 12.dpToPx
    private var textMargin = 5.dpToPx

    private var monthTextHeight = 0
    private var countTextHeight = 0

    private val barBackgroundPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.bar_background)
    }

    private val barForegroundPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.teal_200)
    }

    private val monthTextPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.month)
        textSize = 12.spToPx.toFloat()
        textAlign = Paint.Align.CENTER
    }

    private val countTextPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.month)
        textSize = 10.spToPx.toFloat()
        textAlign = Paint.Align.CENTER
    }

    private val animator: Runnable = object : Runnable {
        override fun run() {
            var needNewFrame = false

            //For every iteration, calculating next set of animation list values for respective bar to fill values.
            //This is repeated until respective animation values are equal to the to fill values
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

    fun setBottomTextList(monthList: ArrayList<String>) {
        monthTextList.addAll(monthList)

        //Create a rect for mont text
        val r = Rect()

        for (s in monthTextList) {
            //Create month text bonds
            monthTextPaint.getTextBounds(s, 0, s.length, r)

            //Considering highest height for the text
            if (monthTextHeight < r.height()) {
                monthTextHeight = r.height()
            }

            //Making sure text rect width and bar width are aligned equally, to make look good
            if (barWidth < r.width()) {
                barWidth = r.width()
            }
        }
        minimumWidth = 2
        postInvalidate()
    }

    fun setTopTextList(countList: ArrayList<Int>) {
        countTextList.addAll(countList)

        //Create rect for count text
        val r = Rect()
        for (s in countTextList) {
            countTextPaint.getTextBounds(s.toString(), 0, s.toString().length, r)

            //Considering highest height for the text
            if (countTextHeight < r.height()) {
                countTextHeight = r.height()
            }
        }

        minimumWidth = 2
        postInvalidate()
    }

    fun setDataList(list: ArrayList<Int>, max: Int) {
        Log.e("set data", list.size.toString())
        //Adding '2' just make graph view distinctive extra space at the top
        val maxValue = max + 5

        //Create list of percent to be filled wrt maxValue
        for (value in list) {
            barToFillPercentList.add(1 - value.toFloat() / maxValue.toFloat())
        }

        //Total values in both list should be same, ie '.size' should be same
        //This require to do animation evaluation
        if (barAnimatePercentList.isEmpty() || barAnimatePercentList.size < barToFillPercentList.size) {
            val temp = barToFillPercentList.size - barAnimatePercentList.size

            for (i in 0 until temp) {
                barAnimatePercentList.add(1f)
            }

        } else if (barAnimatePercentList.size > barToFillPercentList.size) {
            val temp = barAnimatePercentList.size - barToFillPercentList.size

            for (i in 0 until temp) {
                barAnimatePercentList.removeAt(barAnimatePercentList.size - 1)
            }
        }

        removeCallbacks(animator)
        post(animator)

    }

    override fun onDraw(canvas: Canvas) {
        if (barAnimatePercentList.isNotEmpty()) {

            //For given lists of animation and toFill values, draw rect
            barAnimatePercentList.forEachIndexed { index, barToFillValue ->
                drawBackgroundBar(canvas, index)
                drawForegroundBar(canvas, index, barToFillValue)
            }

            //Drawing months texts at each bar bottom
            if (monthTextList.isNotEmpty()) {
                monthTextList.forEachIndexed { index, month ->
                    drawMonthText(canvas, index, month)
                }
            }

            //Drawing commits count text each bar top
            if (countTextList.isNotEmpty()) {
                countTextList.forEachIndexed { index, count ->
                    drawCountText(canvas, index, count)
                }
            }
        }
    }

    private fun drawCountText(canvas: Canvas, index: Int, count: Int) {
        canvas.drawText(
            "$count",
            ((barSideMargin * (index + 1)) + (barWidth * (index)) + barWidth / 2).toFloat(),
            (textMargin + countTextHeight).toFloat(),
            monthTextPaint
        )
    }

    private fun drawMonthText(canvas: Canvas, index: Int, month: String) {
        canvas.drawText(
            month,
            ((barSideMargin * (index + 1)) + (barWidth * (index)) + barWidth / 2).toFloat(),
            (height - textMargin).toFloat(),
            monthTextPaint
        )
    }

    private fun drawForegroundBar(canvas: Canvas, index: Int, barToFillValue: Float) {
        canvas.drawRect(
            ((barSideMargin * (index + 1)) + (barWidth * (index))).toFloat(),
            (textMargin + ((height - monthTextHeight - (textMargin * 2)) * barToFillValue).toInt()).toFloat(),
            ((barSideMargin + barWidth) * (index + 1)).toFloat(),
            (height - monthTextHeight - (textMargin * 2)).toFloat(),
            barForegroundPaint
        )
    }

    private fun drawBackgroundBar(canvas: Canvas, index: Int) {
        canvas.drawRect(
            ((barSideMargin * (index + 1)) + (barWidth * (index))).toFloat(),
            ((textMargin * 2) + monthTextHeight).toFloat(),
            ((barSideMargin + barWidth) * (index + 1)).toFloat(),
            (height - monthTextHeight - (textMargin * 2)).toFloat(),
            barBackgroundPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mViewWidth = measureWidth(widthMeasureSpec)
        val mViewHeight = measureHeight(heightMeasureSpec)
        setMeasuredDimension(mViewWidth, mViewHeight)
    }

    private fun measureWidth(measureSpec: Int): Int {
        val preferred = monthTextList.size * (barWidth + barSideMargin)
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
}
















