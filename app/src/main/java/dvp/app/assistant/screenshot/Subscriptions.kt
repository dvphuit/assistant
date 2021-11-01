package dvp.app.assistant.screenshot

object Subscriptions {

    private val DISPOSED_INSTANCE = object : ScreenshotResult.Subscription {
        override fun dispose() {
            //NOP
        }
    }

    fun disposed(): ScreenshotResult.Subscription = DISPOSED_INSTANCE
}