package dvp.app.assistant.screenshot.internal

import dvp.app.assistant.screenshot.Result
import dvp.app.assistant.screenshot.ScreenshotResult
import dvp.app.assistant.screenshot.Subscriptions
import dvp.app.assistant.screenshot.internal.Utils.checkOnMainThread

internal class ScreenshotResultImpl : ScreenshotResult {

//    private val subscriptions = ArrayList<SubscriptionImpl>()
    private var resultListener: ((Result) -> Unit)? = null

    fun submit(result: Result){
        checkOnMainThread()
        resultListener?.invoke(result)
    }

    override fun observe(result: (Result) -> Unit) {
        checkOnMainThread()
        resultListener = result
        Subscriptions.disposed()
    }

}