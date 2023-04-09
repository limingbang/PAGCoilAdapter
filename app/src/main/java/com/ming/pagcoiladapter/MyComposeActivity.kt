package com.ming.pagcoiladapter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.imageLoader
import com.ming.pag.compose.PAGCoilDecoder
import com.ming.pag.compose.PAGImageViewCompose
import com.ming.pag.compose.rememberAsyncPAGImagePainter
import com.ming.pagcoiladapter.ui.theme.PAGCoilAdapterTheme

class MyComposeActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PAGCoilAdapterTheme {
                val imageLoader = LocalContext.current
                    .imageLoader
                    .newBuilder()
                    .components {
                        add(PAGCoilDecoder.Factory())
                    }
                    .build()

                Scaffold {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(it)
                            .background(color = Color(0x4D6F6D6D)),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 34.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = buildPAGData()) {
                            val pagImage = rememberAsyncPAGImagePainter(
                                model = it,
                                imageLoader = imageLoader,
                                placeholder = painterResource(id = R.drawable.pexels_ian_turnell),
                                error = painterResource(id = R.drawable.pexels_ian_turnell)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                                    .aspectRatio(16 / 9F)
                                    .clip(shape = RoundedCornerShape(8.dp))
                            )

                            PAGImageViewCompose(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9F)
                                    .clip(shape = RoundedCornerShape(8.dp)),
                                pag = pagImage.pag,
                                painter = pagImage.painter
                            )
                        }
                    }
                }
            }
        }
    }

}