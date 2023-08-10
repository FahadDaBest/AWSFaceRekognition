package com.example.amazonamplify.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.example.amazonamplify.models.CreateSessionResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.NumberFormat

sealed class ScreenState {
    data class StartScreenState(val errorMessage: String? = null): ScreenState()
    data class FaceLivenessScreenState(val sessionID: String, val region: String): ScreenState()
    data class ResultsScreenState(val results: String): ScreenState()
}

@Composable
fun FaceLivenessScreen(paddingValues: PaddingValues){
    var state: ScreenState by remember { mutableStateOf(ScreenState.StartScreenState()) }
    Box(
      modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
    ){
        when(state){
            is ScreenState.StartScreenState -> {
                StartFaceLivenessFlow(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    buttonText = "Start Face Liveness Flow",
                    goToFaceLivenessState =  { result ->
                        state = ScreenState.FaceLivenessScreenState(result.SessionId, result.region)
                    }
                )
            }
            is ScreenState.FaceLivenessScreenState ->
                (state as ScreenState.FaceLivenessScreenState).let {
                    FaceSessionLivenessScreen(
                        sessionID = it.sessionID,
                        region = it.region,
                        onErrorResult = { errorMessage ->
                            state = ScreenState.StartScreenState(errorMessage)
                        },
                        onSuccessResult = { response ->
                            state = ScreenState.ResultsScreenState(response)
                        }
                    )
                }
            is ScreenState.ResultsScreenState -> {
                ResponseLivenessSessionScreen(
                    (state as ScreenState.ResultsScreenState).results,
                    goToFaceLivenessState =  { result ->
                        state = ScreenState.FaceLivenessScreenState(
                            result.SessionId,
                            result.region
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StartFaceLivenessFlow(
    modifier: Modifier,
    buttonText: String,
    goToFaceLivenessState: (CreateSessionResponse) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    var currentButtonText by remember { mutableStateOf(buttonText) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(0),
            onClick = {
                scope.launch {
                    currentButtonText = "loading ..."
                    val request = RestOptions.builder()
                        .addPath("/createFaceLivenessSessionAug")
                        .addBody("".toByteArray())
                        .build()
                    Amplify.API.post(
                        request,
                        { response ->
                            currentButtonText = buttonText
                            val jsonResponse = String(response.data.rawBytes)
                            val response = JSONObject(jsonResponse)
                            val createSessionResponse = CreateSessionResponse(
                                SessionId = response.getString("session_id"),
                                region = response.getString("region"),
                            )
                            goToFaceLivenessState(createSessionResponse)
                        },
                        { error ->
                            errorMessage = error.message ?: ""
                            currentButtonText = buttonText
                        }
                    )
                }
            }) {
            Text(
                modifier = Modifier.height(20.dp),
                text = currentButtonText
            )
        }
        Text(
            modifier = Modifier.height(30.dp),
            color = Color.Red,
            text = errorMessage,
        )
    }
}

@Composable
fun FaceSessionLivenessScreen(
    sessionID: String,
    region: String,
    onErrorResult: (String?) -> Unit,
    onSuccessResult: (String) -> Unit,
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
                        .addPath("/getFaceLivenessSessionResultsAug")
                        .addBody("{\"SessionId\":\"$sessionID\"}".toByteArray())
                        .build()
                    Amplify.API.post(
                        request,
                        { response ->
                            val jsonResponse = String(response.data.rawBytes)
                            onSuccessResult(jsonResponse)
                        },
                        { error ->
                            onErrorResult(error.message)
                        }
                    )
                }
            },
            onError = { error ->
                Log.e("MyApp", "Error during Face Liveness flow", Throwable(error.message))
            })
    }
}

@Composable
fun ResponseLivenessSessionScreen(
    successResponse: String,
    goToFaceLivenessState: (CreateSessionResponse) -> Unit
){
    Log.d("Response is", successResponse)
    val response = JSONObject(successResponse)
    val confidence = response.getDouble("Confidence") / 100

    val percentageFormat: NumberFormat = NumberFormat.getPercentInstance()
    percentageFormat.minimumFractionDigits = 2

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            text = "Face Rekognition Results",
        )
        RowTextAndValue("SessionId", response.getString("SessionId"))
        RowTextAndValue("Status", response.getString("Status"))
        RowTextAndValue("Confidence", percentageFormat.format(confidence))

        StartFaceLivenessFlow(
            modifier = Modifier,
            buttonText = "Start A New Face Liveness Flow",
            goToFaceLivenessState =  { result -> goToFaceLivenessState(result) },
        )
    }
}

@Composable
fun RowTextAndValue(key: String, value: String){
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = "$key:",
        )
        Text(
            text = value,
            fontSize = 15.sp,
        )
    }
}
