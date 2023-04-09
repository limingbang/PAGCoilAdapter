package com.ming.pag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.request.NullRequestDataException
import coil.size.Scale
import com.ming.pag.compose.AsyncPAGImagePainter.Companion.DefaultTransform
import com.ming.pag.compose.AsyncPAGImagePainter.PAGState

/** Create an [ImageRequest] from the [model]. */
@Composable
@ReadOnlyComposable
internal fun requestOf(model: Any?): ImageRequest {
    if (model is ImageRequest) {
        return model
    } else {
        return ImageRequest.Builder(LocalContext.current).data(model).build()
    }
}

@Stable
internal fun transformOf(
    placeholder: Painter?,
    error: Painter?,
    fallback: Painter?,
): (PAGState) -> PAGState {
    return if (placeholder != null || error != null || fallback != null) {
        { state ->
            when (state) {
                is PAGState.Loading -> {
                    if (placeholder != null) state.copy(painter = placeholder) else state
                }
                is PAGState.Error -> if (state.result.throwable is NullRequestDataException) {
                    if (fallback != null) state.copy(painter = fallback) else state
                } else {
                    if (error != null) state.copy(painter = error) else state
                }
                else -> state
            }
        }
    } else {
        DefaultTransform
    }
}

@Stable
internal fun onStateOf(
    onLoading: ((PAGState.Loading) -> Unit)?,
    onSuccess: ((PAGState.Success) -> Unit)?,
    onError: ((PAGState.Error) -> Unit)?,
): ((PAGState) -> Unit)? {
    return if (onLoading != null || onSuccess != null || onError != null) {
        { state ->
            when (state) {
                is PAGState.Loading -> onLoading?.invoke(state)
                is PAGState.Success -> onSuccess?.invoke(state)
                is PAGState.Error -> onError?.invoke(state)
                is PAGState.Empty -> {}
            }
        }
    } else {
        null
    }
}

@Stable
internal fun ContentScale.toScale() = when (this) {
    ContentScale.Fit, ContentScale.Inside -> Scale.FIT
    else -> Scale.FILL
}
