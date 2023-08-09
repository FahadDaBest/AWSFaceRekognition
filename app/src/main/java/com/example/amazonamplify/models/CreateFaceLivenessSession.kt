package com.example.amazonamplify.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateFaceLivenessSessionRequest(
    val ClientRequestToken: String? = null ,
    val Settings: SessionSettings? = null,
    val AuditImagesLimit: Int? = null,
){
    data class SessionSettings(
        val ouputConfig: OutputConfig,
    )

    data class OutputConfig(
        val S3Bucket: String,
        val S3Prefix: String,
    )
}

@Keep
@JsonClass(generateAdapter = true)
data class CreateSessionResponse(
    val SessionId: String,
    val region: String,
)
