package <%= appPackage %>.extensions

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuff.Mode.ADD
import android.graphics.PorterDuff.Mode.CLEAR
import android.graphics.PorterDuff.Mode.DARKEN
import android.graphics.PorterDuff.Mode.DST
import android.graphics.PorterDuff.Mode.DST_ATOP
import android.graphics.PorterDuff.Mode.DST_IN
import android.graphics.PorterDuff.Mode.DST_OUT
import android.graphics.PorterDuff.Mode.DST_OVER
import android.graphics.PorterDuff.Mode.LIGHTEN
import android.graphics.PorterDuff.Mode.MULTIPLY
import android.graphics.PorterDuff.Mode.OVERLAY
import android.graphics.PorterDuff.Mode.SCREEN
import android.graphics.PorterDuff.Mode.SRC
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuff.Mode.SRC_OUT
import android.graphics.PorterDuff.Mode.SRC_OVER
import android.graphics.PorterDuff.Mode.XOR
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Created by esparta on 30/08/16.
 */

 fun View.enableView() {
     enableDisableView(true)
 }

 fun View.disableView() {
     enableDisableView(false)
 }

@Suppress("MagicNumber")
fun View.enableDisableView(enabled: Boolean) {
    this.isEnabled = enabled
    this.isClickable = enabled
    this.alpha = if (enabled) 1f else .5f
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun View.fadeIn() {
    this.animate().alpha(1f).start()
}

fun View.fadeOut() {
    this.animate().alpha(0f).start()
}

fun Context.enableDisableViews(enabled: Boolean, vararg views: View) {
    views.forEach {
        it.enableDisableView(enabled)
    }
}

fun Context.setViewsInvisible(vararg views: View) {
    views.forEach(View::setInvisible)
}

fun Context.setViewsVisible(vararg views: View) {
    views.forEach(View::setVisible)
}

fun Context.setViewsGone(vararg views: View) {
    views.forEach(View::setGone)
}

/**
 * Define color filter no background
 */
fun EditText.setUnderlineColor(@ColorRes color: Int) {
    background.setColorFilterWith(context, color, SRC_ATOP)
}

fun ProgressBar.setProgressColor(@ColorRes color: Int) {
    indeterminateDrawable.setColorFilterWith(context, color, MULTIPLY)
}

@Suppress("DEPRECATION")
fun Drawable.setColorFilterWith(context: Context, @ColorRes color: Int, mode: PorterDuff.Mode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val blendMode = when (mode) {
            CLEAR -> BlendMode.CLEAR
            SRC -> BlendMode.SRC
            DST -> BlendMode.DST
            SRC_OVER -> BlendMode.SRC_OVER
            DST_OVER -> BlendMode.DST_OVER
            SRC_IN -> BlendMode.SRC_IN
            DST_IN -> BlendMode.DST_IN
            SRC_OUT -> BlendMode.SRC_OUT
            DST_OUT -> BlendMode.DST_OUT
            SRC_ATOP -> BlendMode.SRC_ATOP
            DST_ATOP -> BlendMode.DST_ATOP
            XOR -> BlendMode.XOR
            DARKEN -> BlendMode.DARKEN
            LIGHTEN -> BlendMode.LIGHTEN
            MULTIPLY -> BlendMode.MULTIPLY
            SCREEN -> BlendMode.SCREEN
            ADD -> BlendMode.MULTIPLY//I don't have any idea which one is
            OVERLAY -> BlendMode.OVERLAY
        }
        colorFilter = BlendModeColorFilter(color, blendMode)
    } else {
        setColorFilter(ContextCompat.getColor(context, color), mode)
    }
}

fun AppCompatActivity.snackbar(view: View, @StringRes text: Int, length: Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(view, text, length).show()
}

fun AppCompatActivity.snackbar(view: View, text: String, length: Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(view, text, length).show()
}

fun AppCompatActivity.snackbarAndAction(view: View, @StringRes text: Int, @StringRes actionText: Int, actionListener: View.OnClickListener, length: Int = Snackbar.LENGTH_INDEFINITE){
    Snackbar.make(view, text, length)
            .setAction(actionText, actionListener)
            .show()
}

fun AppCompatActivity.changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = color
    }
}

private class VarDelegate<T>(initializer: () -> T) : ReadWriteProperty<Any?, T> {

    private var initializer: (() -> T)? = initializer

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = initializer!!()
        }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

// http://stackoverflow.com/questions/34346966/kotlin-lazy-default-property/34347410#34347410
object DelegatesExt {
    fun <T> lazyVar(initializer: () -> T): ReadWriteProperty<Any?, T> = VarDelegate(initializer)
}

fun <T> lazyUnsafe(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

inline fun <T1: Any, T2: Any, R: Any> doubleLet(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? =
        if (p1 != null && p2 != null) block(p1, p2) else null

inline fun <T1: Any, T2: Any, T3: Any, R: Any> tripleLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3)->R?): R? =
        if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null


fun AppCompatActivity.getStatusBarHeight(): Int {
    val rectangle = Rect()
    val window = window
    window.decorView.getWindowVisibleDisplayFrame(rectangle)
    val statusBarHeight = rectangle.top
    val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
    return statusBarHeight - contentViewTop
}

fun AppCompatActivity.getToolBarHeight(): Int {
    val textSizeAttr = intArrayOf(android.R.attr.actionBarSize)
    val a = applicationContext.obtainStyledAttributes(TypedValue().data, textSizeAttr)
    val toolbarSize = a.getDimensionPixelSize(0, -1)
    a.recycle()
    return toolbarSize
}

fun AppCompatActivity.getDisplayDimensions(): Pair<Int, Int> {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return Pair(size.x, size.y)
}

fun DateTime.toStringFormat(format: String): String {
    val formatter = DateTimeFormat.forPattern(format)
    return formatter.print(this)
}

fun LocalDate.toStringFormat(format: String): String {
    val formatter = DateTimeFormat.forPattern(format)
    return formatter.print(this)
}