package com.example.amazonamplify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.amazonamplify.ui.theme.AmazonAmplifyTheme
import com.example.amazonamplify.ui.theme.CameraPermissionWrapperComposable
import com.example.amazonamplify.ui.theme.FaceLivenessScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AmazonAmplifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    CameraPermissionWrapperComposable {
                        FaceLivenessScreen(paddingValues)
                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        AmazonAmplifyTheme {
            Greeting("Android")
        }
    }
}
