package com.smascaro.trackmixing.networking.requestTrack

data class RequestTrackResponseSchema(
    val body: Body,
    val status: Status
) {
    data class Status(
        val code: Int,
        val message: String
    )

    class Body

}

