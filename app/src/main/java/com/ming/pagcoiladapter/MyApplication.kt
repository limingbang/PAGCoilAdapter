package com.ming.pagcoiladapter

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ming.pag.coil.PAGCoilDecoder

class MyApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context = applicationContext)
            .components {
                add(PAGCoilDecoder.Factory())
            }
            .build()
    }
}