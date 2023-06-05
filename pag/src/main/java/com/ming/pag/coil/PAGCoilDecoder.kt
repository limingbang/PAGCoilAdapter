package com.ming.pag.coil

import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.decode.ImageSource
import coil.fetch.SourceResult
import coil.request.Options
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.encodeUtf8
import org.libpag.PAGFile


class PAGCoilDecoder(
    private val source: ImageSource,
    private val options: Options,
) : Decoder {
    override suspend fun decode() = runInterruptible {
        val pag = source.source().use { PAGFile.Load(it.readByteArray()) }

        check(pag != null) { "Failed to decode PAG." }

        DecodeResult(
            PAGAdapterDrawable(pag, options),
            false
        )
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            if (!result.source.source().rangeEquals(0, "PAG".encodeUtf8())) return null

            return PAGCoilDecoder(result.source, options)
        }
    }
}