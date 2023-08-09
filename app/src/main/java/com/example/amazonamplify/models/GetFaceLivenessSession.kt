import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetFaceLivenessSessionRequest(
    val SessionId: String? = null ,
)

@JsonClass(generateAdapter = true)
data class GetFaceLivenessSessionResponse(
    val SessionId: String? = null,
    val Confidence: Double? = null,
    val Status: String? = null,
)
