/**
 *   Listener to protect app crashes by fast clicks on button *
 *
 *   Source: source: https://medium.com/@masaaki.iwaguchi/android-how-to-prevent-multiple-view-clicks-using-databinding-and-kotlin-extensions-5d5859071b4b
 */


import android.view.View
import androidx.databinding.BindingAdapter
import java.util.concurrent.atomic.AtomicBoolean

@BindingAdapter("onSingleClick")
fun View.setOnSingleClickListener(clickListener: View.OnClickListener?) {
    clickListener?.also {
        setOnClickListener(OnSingleClickListener(it))
    } ?: setOnClickListener(null)
}

class OnSingleClickListener(
    private val clickListener: View.OnClickListener,
    private val intervalMs: Long = 600
) : View.OnClickListener {
    private var canClick = AtomicBoolean(true)

    override fun onClick(v: View?) {
        if (canClick.getAndSet(false)) {
            v?.run {
                postDelayed({
                    canClick.set(true)
                }, intervalMs)
                clickListener.onClick(v)
            }
        }
    }
}