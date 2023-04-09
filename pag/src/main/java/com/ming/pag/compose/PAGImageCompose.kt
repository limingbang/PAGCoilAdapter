package com.ming.pag.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import org.libpag.PAGComposition
import org.libpag.PAGScaleMode
import org.libpag.PAGView

@Composable
fun PAGImageViewCompose(
    modifier: Modifier,
    pag: PAGComposition?,
    painter: Painter,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    pagScaleModel: Int = PAGScaleMode.Zoom,
    repeatCount: Int = 0
) {
    Box(modifier = Modifier.wrapContentSize()) {
        if (pag != null) {
            AndroidView(
                modifier = modifier,
                factory = {
                    PAGView(it).apply {
                        setScaleMode(pagScaleModel)
                        setRepeatCount(repeatCount)
                    }
                },
                update = {
                    it.composition = pag
                    it.play()
                },
                onRelease = {
                    it.freeCache()
                }
            )
        } else {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = colorFilter
            )
        }
    }
}