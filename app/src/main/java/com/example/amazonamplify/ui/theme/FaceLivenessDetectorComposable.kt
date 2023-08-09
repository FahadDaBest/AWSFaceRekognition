package com.example.amazonamplify.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.example.amazonamplify.models.CreateSessionResponse
import com.google.gson.JsonObject
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class ScreenState {
    object StartScreenState: ScreenState()
    data class FaceLivenessScreenState(val sessionID: String, val region: String): ScreenState()
    data class ResultsScreenState(val results: String): ScreenState()
}

@Composable
fun FaceLivenessScreen(paddingValues: PaddingValues){
    var state: ScreenState by remember { mutableStateOf(ScreenState.StartScreenState) }
    Box(
      modifier = Modifier.padding(paddingValues)
    ){
        when(state){
            is ScreenState.StartScreenState -> {
                StartFaceLivenessFlow { result ->
                    state = ScreenState.FaceLivenessScreenState(result.SessionId, result.region)
                }
            }
            is ScreenState.FaceLivenessScreenState ->
                (state as ScreenState.FaceLivenessScreenState).let {
                    FaceSessionLivenessScreen(it.sessionID, it.region)
                }
            is ScreenState.ResultsScreenState -> TODO()
        }
    }
}

@Composable
fun StartFaceLivenessFlow(
    goToFaceLivenessState: (CreateSessionResponse) -> Unit,
){

    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0),
        onClick = {
            scope.launch {
                val request = RestOptions.builder()
                    .addPath("/createFaceLivenessSessionAug")
                    .addBody("".toByteArray())
                    .build()
                Amplify.API.post(
                    request,
                    { response ->
                        val jsonResponse = String(response.data.rawBytes)
                        val response = JSONObject(jsonResponse)
                        val createSessionResponse = CreateSessionResponse(
                            SessionId = response.getString("session_id"),
                            region = response.getString("region"),
                        )
                        goToFaceLivenessState(createSessionResponse)
                    },
                    { error -> errorMessage = error.message ?: "" }
                )
            }
        }) {
        Text(
            modifier = Modifier.height(20.dp),
            text = "Start Face Liveness Flow"
        )
    }
    Text(
        modifier = Modifier.height(30.dp),
        color = Color.Red,
        text = errorMessage,
    )
}

@Composable
fun FaceSessionLivenessScreen(
    sessionID: String,
    region: String
){
    val scope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        FaceLivenessDetector(
            sessionId = sessionID,
            region = region,
            onComplete = {
                Log.i("MyApp", "Face Liveness flow is complete")
                scope.launch {
                    val request = RestOptions.builder()
                        .addPath("/getLivenessSessionAug")
                        .addBody("{\"SessionId\":\"$sessionID\"}".toByteArray())
                        .build()
                    Amplify.API.post(
                        request,
                        { response -> Log.e("Fahad","MyAmplifyApp - POST succeeded: $response") },
                        { error ->    Log.e("Fahad", "MyAmplifyApp - POST failed $error.message") }
                    )
                }
            },
            onError = { error ->
                Log.e("MyApp", "Error during Face Liveness flow", Throwable(error.message))
            })
    }
}
