package com.smascaro.trackmixing.player.business.downloadtrack.model

sealed class DownloadEvents {
    data class ProgressUpdate(
        val trackTitle: String,
        val progress: Int,
        val message: String,
        val step: FetchSteps
    ) :
        DownloadEvents()

    class FinishedProcessing : DownloadEvents()
    class FinishedDownloading : DownloadEvents()
    class ErrorOccurred(val message: String) : DownloadEvents()
}

fun DownloadEvents.ProgressUpdate.toNotificationData(): DownloadProgressState {
    return DownloadProgressState(trackTitle, evaluateOverallProgress(), message)
}

fun DownloadEvents.ProgressUpdate.evaluateOverallProgress(): Int {
    return when (step) {
        is FetchSteps.ServerProcessStep -> (this.progress * (this.step.percentage.toFloat() / 100f)).toInt()
        is FetchSteps.DownloadStep -> FetchSteps.ServerProcessStep().percentage + (this.progress * (this.step.percentage.toFloat() / 100f)).toInt()
    }
}