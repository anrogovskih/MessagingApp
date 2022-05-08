package com.axmor.fsinphone.videomessages.ui.common.binding_adapters

import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import com.axmor.fsinphone.videomessages.R
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.model.Notation
import timber.log.Timber

@BindingAdapter("onDone")
fun onDone(view: EditText, action: (() -> Unit)?) {
    action ?: return

    view.setOnEditorActionListener(
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                action.invoke()
                return@OnEditorActionListener true
            }

            false
        }
    )
}

@BindingAdapter("onDoneToggle")
fun onDoneToggle(view: EditText, booleanObservable: ObservableBoolean?) {
    if (booleanObservable == null) {
        return
    }

    view.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            booleanObservable.set(!booleanObservable.get())
            return@OnEditorActionListener true
        }

        false
    })
}

@BindingAdapter("requestFocusIf")
fun requestFocusIf(view: EditText, isRequesting: Boolean){
    if (isRequesting) view.requestFocus()
}

@BindingAdapter(value = ["mask", "listener"], requireAll = false)
fun setMask(editText: EditText, mask: String?, listener: TextWatcher?) {
    mask?.let {
        val textWatcher = MaskedTextChangedListener(
            primaryFormat = mask,
            field = editText,
            listener = listener,
            autocomplete = true,
            customNotations = listOf(Notation('1', "123456789", false))
        )
        editText.apply {
            clearOldFormatting()
            addTextChangedListener(textWatcher)
            setTag(R.id.id_mask_text_watcher, textWatcher)
        }
    }
}

private fun EditText.clearOldFormatting() {
    (getTag(R.id.id_mask_text_watcher) as? TextWatcher)?.let {
        removeTextChangedListener(it)
    }
}
