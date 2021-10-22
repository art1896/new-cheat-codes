package com.cheatart.newcheatcodes.other

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import com.cheatart.newcheatcodes.model.Rating


class CustomSeekBar : androidx.appcompat.widget.AppCompatSeekBar {
    private var mProgressItemsList: List<Rating>? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    fun initData(progressItemsList: List<Rating>?) {
        mProgressItemsList = progressItemsList
    }

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        try {
            if (mProgressItemsList!!.isNotEmpty()) {
                val progressBarWidth = width
                val progressBarHeight = height
                val thumboffset = thumbOffset
                var lastProgressX = 0
                var progressItemWidth: Int
                var progressItemRight: Int
                for (i in mProgressItemsList!!.indices) {
                    val progressItem: Rating = mProgressItemsList!![i]
                    val progressPaint = Paint()
                    progressPaint.color = progressItem.color!!
                    progressItemWidth = (progressItem.percent!!.toInt()
                            * progressBarWidth / 100)
                    progressItemRight = lastProgressX + progressItemWidth

                    if (i == mProgressItemsList!!.size - 1
                        && progressItemRight != progressBarWidth
                    ) {
                        progressItemRight = progressBarWidth
                    }
                    val progressRect = Rect()
                    progressRect[lastProgressX, thumboffset / 2, progressItemRight] =
                        progressBarHeight - thumboffset / 2
                    canvas.drawRect(progressRect, progressPaint)
                    lastProgressX = progressItemRight
                }
                super.onDraw(canvas)
            }
        } catch (e: NullPointerException) {

        }
    }
}