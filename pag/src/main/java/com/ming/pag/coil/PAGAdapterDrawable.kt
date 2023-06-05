package com.ming.pag.coil

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import coil.request.Options
import org.libpag.PAGComposition
import org.libpag.PAGDecoder

class PAGAdapterDrawable(
    val pag: PAGComposition,
    private val options: Options
) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    /**
     * 这里可以考虑通过[PAGDecoder.Make]去获取某一帧的bitmap，比如首帧，但需要考虑其大小等参数的适配
     *
     * 一般，画一帧会比整体渲染要快一丢丢，所以有可能会导致闪烁问题。若上述问题适配好，应该是看不出来，而且还可以填补整体渲染前的空白期🤪
     */
    override fun draw(canvas: Canvas) = Unit

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}