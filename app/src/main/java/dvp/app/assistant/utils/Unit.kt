package dvp.app.assistant.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import dvp.app.assistant.base.ext.getWindowManager


@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
fun Dp.toIntPx() = this.toPx().toInt()

@Composable
fun Float.px2dp() = with(LocalDensity.current) { Dp(this@px2dp / this.density) }

@Composable
fun Float.dp() = with(LocalDensity.current) { Dp(this@dp).toSp() }

val Int.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

fun Context.screenSizeCompat(): Size {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        screenSizeR()
    } else {
        screenSize()
    }
}

@Suppress("DEPRECATION")
private fun Context.screenSize(): Size {
    val metrics = DisplayMetrics()
    getWindowManager().defaultDisplay.getMetrics(metrics)
    return Size(
        metrics.widthPixels,
        metrics.heightPixels
    )
}

@RequiresApi(Build.VERSION_CODES.R)
private fun Context.screenSizeR(): Size {
    val metrics: WindowMetrics = getWindowManager().currentWindowMetrics

    val windowInsets = metrics.windowInsets
    val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
        WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
    )
    val insetsWidth = insets.right + insets.left
    val insetsHeight = insets.top + insets.bottom
    val bounds = metrics.bounds
    return Size(
        bounds.width() - insetsWidth,
        bounds.height() - insetsHeight
    )
}