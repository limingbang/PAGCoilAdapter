package com.ming.pag.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Constraints
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.size.Dimension
import coil.size.Precision
import coil.transition.CrossfadeTransition
import coil.transition.TransitionTarget
import com.ming.pag.coil.CoilPAGImage
import com.ming.pag.coil.PAGAdapterDrawable
import com.ming.pag.compose.AsyncPAGImagePainter.Companion.DefaultTransform
import com.ming.pag.compose.AsyncPAGImagePainter.PAGState
import com.ming.pag.onStateOf
import com.ming.pag.requestOf
import com.ming.pag.toPainter
import com.ming.pag.toScale
import com.ming.pag.transformOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.libpag.PAGComposition
import kotlin.math.roundToInt
import coil.size.Size as CoilSize

/**
 * Return an [AsyncPAGImagePainter] that executes an [ImageRequest] asynchronously and renders the result.
 *
 * - [AsyncPAGImagePainter] will not finish loading if [AsyncPAGImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncPAGImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncPAGImagePainter.state] will not transition to [PAGState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [ImageRequest.Builder.size] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param model Either an [ImageRequest] or the [ImageRequest.data] value.
 * @param imageLoader The [ImageLoader] that will be used to execute the request.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param fallback A [Painter] that is displayed when the request's [ImageRequest.data] is null.
 * @param onLoading Called when the image request begins loading.
 * @param onSuccess Called when the image request completes successfully.
 * @param onError Called when the image request completes unsuccessfully.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [model]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncPAGImagePainter(
    model: Any?,
    imageLoader: ImageLoader,
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((PAGState.Loading) -> Unit)? = null,
    onSuccess: ((PAGState.Success) -> Unit)? = null,
    onError: ((PAGState.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
) = rememberAsyncPAGImagePainter(
    model = model,
    imageLoader = imageLoader,
    transform = transformOf(placeholder, error, fallback),
    onState = onStateOf(onLoading, onSuccess, onError),
    contentScale = contentScale,
    filterQuality = filterQuality,
)

/**
 * Return an [AsyncPAGImagePainter] that executes an [ImageRequest] asynchronously and renders the result.
 *
 * ** This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncPAGImagePainter] will not finish loading if [AsyncPAGImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncPAGImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncPAGImagePainter.state] will not transition to [PAGState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [ImageRequest.Builder.size] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param model Either an [ImageRequest] or the [ImageRequest.data] value.
 * @param imageLoader The [ImageLoader] that will be used to execute the request.
 * @param transform A callback to transform a new [PAGState] before it's applied to the
 *  [AsyncPAGImagePainter]. Typically this is used to overwrite the state's [Painter].
 * @param onState Called when the state of this painter changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [model]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncPAGImagePainter(
    model: Any?,
    imageLoader: ImageLoader,
    transform: (PAGState) -> PAGState = DefaultTransform,
    onState: ((PAGState) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): CoilPAGImage {
    val request = requestOf(model)
    validateRequest(request)

    val painter = remember { AsyncPAGImagePainter(request, imageLoader) }
    painter.transform = transform
    painter.onState = onState
    painter.contentScale = contentScale
    painter.filterQuality = filterQuality
    painter.isPreview = LocalInspectionMode.current
    painter.imageLoader = imageLoader
    painter.request = request // Update request last so all other properties are up to date.
    painter.onRemembered() // Invoke this manually so `painter.state` is set to `Loading` immediately.
    return CoilPAGImage(painter, painter.pag)
}

/**
 * A [Painter] and [PAGComposition] that that executes an [ImageRequest] asynchronously and get the result.
 *
 */
@Stable
class AsyncPAGImagePainter internal constructor(
    request: ImageRequest,
    imageLoader: ImageLoader
) : Painter(), RememberObserver {

    private var rememberScope: CoroutineScope? = null
    private val drawSize = MutableStateFlow(Size.Zero)

    var pag: PAGComposition? by mutableStateOf(null)
        private set

    private var painter: Painter? by mutableStateOf(null)
    private var alpha: Float by mutableStateOf(DefaultAlpha)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    // These fields allow access to the current value
    // instead of the value in the current composition.
    private var _state: PAGState = PAGState.Empty
        set(value) {
            field = value
            state = value
        }
    private var _painter: Painter? = null
        set(value) {
            field = value
            painter = value
        }

    private var _pag: PAGComposition? = null
        set(value) {
            field = value
            pag = value
        }

    internal var transform = DefaultTransform
    internal var onState: ((PAGState) -> Unit)? = null
    internal var contentScale = ContentScale.Fit
    internal var filterQuality = DefaultFilterQuality
    internal var isPreview = false

    /** Avoid obvious flickering when switching graphs. */
    var isInterceptionPlaceholder = false

    /** The current [AsyncPAGImagePainter.PAGState]. */
    var state: PAGState by mutableStateOf(PAGState.Empty)
        private set

    /** The current [ImageRequest]. */
    var request: ImageRequest by mutableStateOf(request)
        internal set

    /** The current [ImageLoader]. */
    var imageLoader: ImageLoader by mutableStateOf(imageLoader)
        internal set

    override val intrinsicSize: Size
        get() = painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
        drawSize.value = size

        // Draw the current painter.
        painter?.apply { draw(size, alpha, colorFilter) }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onRemembered() {
        // Short circuit if we're already remembered.
        if (rememberScope != null) return

        // Create a new scope to observe state and execute requests while we're remembered.
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        rememberScope = scope

        // Manually notify the child painter that we're remembered.
        (_painter as? RememberObserver)?.onRemembered()

        // If we're in inspection mode skip the image request and set the state to loading.
        if (isPreview) {
            val request = request.newBuilder().defaults(imageLoader.defaults).build()
            updateState(PAGState.Loading(request.placeholder?.toPainter(filterQuality = filterQuality)))
            return
        }

        // Observe the current request and execute any emissions.
        scope.launch {
            snapshotFlow { request }
                .mapLatest { imageLoader.execute(updateRequest(request)).toState() }
                .collect(::updateState)
        }
    }

    override fun onForgotten() {
        clear()
        (_painter as? RememberObserver)?.onForgotten()
    }

    override fun onAbandoned() {
        clear()
        (_painter as? RememberObserver)?.onAbandoned()
    }

    private fun clear() {
        rememberScope?.cancel()
        rememberScope = null
    }

    /** Update the [request] to work with [AsyncPAGImagePainter]. */
    private fun updateRequest(request: ImageRequest): ImageRequest {
        return request.newBuilder()
            .target(onStart = { placeholder ->
                if (!isInterceptionPlaceholder) {
                    updateState(PAGState.Loading(placeholder?.toPainter(filterQuality = filterQuality)))
                    isInterceptionPlaceholder = true
                }
            })
            .apply {
                if (request.defined.sizeResolver == null) {
                    // If no other size resolver is set, suspend until the canvas size is positive.
                    size { drawSize.mapNotNull { it.toSizeOrNull() }.first() }
                }
                if (request.defined.scale == null) {
                    // If no other scale resolver is set, use the content scale.
                    scale(contentScale.toScale())
                }
                if (request.defined.precision != Precision.EXACT) {
                    // AsyncImagePainter scales the image to fit the canvas size at draw time.
                    precision(Precision.INEXACT)
                }
            }
            .build()
    }

    private fun updateState(input: PAGState) {
        val previous = _state
        val current = transform(input)
        _state = current
        _painter = maybeNewCrossfadePainter(previous, current) ?: current.painter
        _pag = current.pag

        // Manually forget and remember the old/new painters if we're already remembered.
        if (rememberScope != null && previous.painter !== current.painter) {
            (previous.painter as? RememberObserver)?.onForgotten()
            (current.painter as? RememberObserver)?.onRemembered()
        }

        // Notify the state listener.
        onState?.invoke(current)
    }

    /** Create and return a [CrossfadePainter] if requested. */
    private fun maybeNewCrossfadePainter(previous: PAGState, current: PAGState): CrossfadePainter? {
        // We can only invoke the transition factory if the state is success or error.
        val result = when (current) {
            is PAGState.Success -> current.result
            is PAGState.Error -> current.result
            else -> return null
        }

        // Invoke the transition factory and wrap the painter in a `CrossfadePainter` if it returns
        // a `CrossfadeTransformation`.
        val transition = result.request.transitionFactory.create(FakeTransitionTarget, result)
        if (transition is CrossfadeTransition) {
            return CrossfadePainter(
                start = previous.painter.takeIf { previous is PAGState.Loading },
                end = current.painter,
                contentScale = contentScale,
                durationMillis = transition.durationMillis,
                fadeStart = result !is SuccessResult || !result.isPlaceholderCached,
                preferExactIntrinsicSize = transition.preferExactIntrinsicSize
            )
        } else {
            return null
        }
    }

    private fun ImageResult.toState() = when (this) {
        is SuccessResult -> PAGState.Success(drawable.toPainter(filterQuality = filterQuality), (drawable as? PAGAdapterDrawable)?.pag,this)
        is ErrorResult -> PAGState.Error(drawable?.toPainter(filterQuality = filterQuality), result = this)
    }

    /**
     * The current state of the [AsyncPAGImagePainter].
     */
    sealed class PAGState {

        /** The current painter being drawn by [AsyncPAGImagePainter]. */
        abstract val painter: Painter?

        abstract val pag: PAGComposition?

        /** The request has not been started. */
        object Empty : PAGState() {
            override val painter: Painter? get() = null
            override val pag: PAGComposition? get() = null
        }

        /** The request is in-progress. */
        data class Loading(
            override val painter: Painter?,
            override val pag: PAGComposition? = null
        ) : PAGState()

        /** The request was successful. */
        data class Success(
            override val painter: Painter,
            override val pag: PAGComposition?,
            val result: SuccessResult,
        ) : PAGState()

        /** The request failed due to [ErrorResult.throwable]. */
        data class Error(
            override val painter: Painter?,
            override val pag: PAGComposition? = null,
            val result: ErrorResult,
        ) : PAGState()
    }

    companion object {
        /**
         * A state transform that does not modify the state.
         */
        val DefaultTransform: (PAGState) -> PAGState = { it }
    }
}

private fun validateRequest(request: ImageRequest) {
    when (request.data) {
        is ImageRequest.Builder -> unsupportedData(
            name = "ImageRequest.Builder",
            description = "Did you forget to call ImageRequest.Builder.build()?"
        )

        is ImageBitmap -> unsupportedData("ImageBitmap")
        is ImageVector -> unsupportedData("ImageVector")
        is Painter -> unsupportedData("Painter")
    }
    require(request.target == null) { "request.target must be null." }
}

private fun unsupportedData(
    name: String,
    description: String = "If you wish to display this $name, use androidx.compose.foundation.Image."
): Nothing = throw IllegalArgumentException("Unsupported type: $name. $description")

private val Size.isPositive get() = width >= 0.5 && height >= 0.5

private fun Size.toSizeOrNull() = when {
    isUnspecified -> CoilSize.ORIGINAL
    isPositive -> CoilSize(
        width = if (width.isFinite()) Dimension(width.roundToInt()) else Dimension.Undefined,
        height = if (height.isFinite()) Dimension(height.roundToInt()) else Dimension.Undefined
    )

    else -> null
}

private val FakeTransitionTarget = object : TransitionTarget {
    override val view get() = throw UnsupportedOperationException()
    override val drawable: Drawable? get() = null
}
