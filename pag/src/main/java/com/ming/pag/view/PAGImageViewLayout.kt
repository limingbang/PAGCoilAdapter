package com.ming.pag.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.ming.pag.R
import org.libpag.PAGView

class PAGImageViewLayout : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var mPAGView: PAGView
        private set

    var mImageView: ImageView
        private set

    init {
        LayoutInflater.from(context).inflate(R.layout.pag_image_view_fl, this)
        mPAGView = findViewById(R.id.pag_view)
        mImageView = findViewById(R.id.image_view)
    }

}