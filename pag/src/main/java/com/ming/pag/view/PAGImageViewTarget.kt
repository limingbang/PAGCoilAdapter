package com.ming.pag.view

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import coil.target.ImageViewTarget
import com.ming.pag.coil.PAGAdapterDrawable
import org.libpag.PAGView

class PAGImageViewTarget(
    val pagView: PAGView,
    override val view: ImageView
): ImageViewTarget(view) {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        pagView.play()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        pagView.stop()
    }

    override fun onStart(placeholder: Drawable?) {
        updatePAGView(false)
        super.onStart(placeholder)
    }

    override fun onSuccess(result: Drawable) {
        if (result is PAGAdapterDrawable) {
            updatePAGView(true)
            pagView.composition = result.pag
        } else {
            updatePAGView(false)
            super.onSuccess(result)
        }
    }

    override fun onError(error: Drawable?) {
        updatePAGView(false)
        super.onError(error)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is PAGImageViewTarget && view == other.view && pagView == other.view
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + view.hashCode()
        result = 31 * result + pagView.hashCode()
        return result
    }

    private fun updatePAGView(isVisibility: Boolean) {
        pagView.isVisible = isVisibility
        view.isVisible = !isVisibility
    }

}