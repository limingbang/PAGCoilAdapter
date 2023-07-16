package com.ming.pag.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.ColorPainter
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.ming.pag.coil.CoilPAGImage
import com.ming.pag.coil.PAGAdapterDrawable
import com.ming.pag.toPainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
fun loadPAGImageFile(
    loader: ImageLoader,
    request: ImageRequest,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    defaultColor: Color = Color.Transparent
): State<CoilPAGImage> {
    return produceState(
        key1 = request,
        initialValue =  CoilPAGImage(
            painter = request.placeholder?.toPainter(filterQuality = filterQuality) ?: ColorPainter(color = defaultColor),
            pag = null
        )
    ) {
        withContext(Dispatchers.IO) {
            val realResult = when (val imageResult = loader.execute(request = request)) {
                is SuccessResult -> CoilPAGImage(
                    painter = imageResult.drawable.toPainter(filterQuality = filterQuality),
                    pag = (imageResult.drawable as? PAGAdapterDrawable)?.pag
                )

                is ErrorResult -> CoilPAGImage(
                    painter = imageResult.drawable?.toPainter(filterQuality = filterQuality) ?: ColorPainter(color = defaultColor),
                    pag = null
                )
            }

            val pre = value.painter

            withContext(Dispatchers.Main) {
                value = realResult
                if (pre !== value.painter) {
                    (pre as? RememberObserver)?.onForgotten()
                    (value.painter as? RememberObserver)?.onRemembered()
                }
            }
        }
    }
}