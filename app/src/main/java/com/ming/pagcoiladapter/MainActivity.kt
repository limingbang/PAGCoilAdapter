package com.ming.pagcoiladapter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ming.pagcoiladapter.ui.theme.PAGCoilAdapterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PAGCoilAdapterTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = {
                            startActivity(Intent(this@MainActivity, MyViewActivity::class.java))
                        }) {
                        Text(text = "原生view")
                    }

                    TextButton(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = {
                            startActivity(Intent(this@MainActivity, MyComposeActivity::class.java))
                        }) {
                        Text(text = "compose")
                    }
                }
            }
        }
    }
}