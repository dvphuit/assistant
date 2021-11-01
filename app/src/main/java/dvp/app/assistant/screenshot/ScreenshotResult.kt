package dvp.app.assistant.screenshot

interface ScreenshotResult {

    fun observe(result: (Result) -> Unit)

    interface Subscription {
        fun dispose()
    }
}