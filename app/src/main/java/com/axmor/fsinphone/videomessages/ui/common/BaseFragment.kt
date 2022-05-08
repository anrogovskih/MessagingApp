package com.axmor.fsinphone.videomessages.ui.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.checkAuthError
import com.axmor.fsinphone.videomessages.common.extensions.getDefaultSharedPreferences
import com.axmor.fsinphone.videomessages.common.extensions.isNetworkException
import com.axmor.fsinphone.videomessages.core.exceptions.InvalidInputError
import com.axmor.fsinphone.videomessages.ui.BackPressHandler
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.fragmentargs.FragmentArgs
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes private val layoutResourceId: Int) :
    Fragment() {
    private var innerBinding: T? = null
    protected val onDestroy = CompositeDisposable()
    protected val onDestroyView = CompositeDisposable()

    protected val sharedPreferences by lazy { requireContext().getDefaultSharedPreferences() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FragmentArgs.inject(this)
        unlockBackPress()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        innerBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        requireBinding().apply {
            lifecycleOwner = this@BaseFragment
            bindData(this)
            executePendingBindings()
        }
        return requireBinding().root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().apply {
            observe(backPressedEvent) { back() }
        }
    }

    protected abstract fun bindData(binding: T)

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        innerBinding = null
        onDestroyView.clear()
    }

    fun requireBinding(): T = innerBinding!!

    fun getBinding(): T? = innerBinding

    override fun onDestroy() {
        super.onDestroy()
        onDestroy.clear()
    }

    protected fun observeErrorEvents() {
        observe(getViewModel().errorLiveData) {
            val exception = it.getContentIfNotHandled() ?: return@observe
            val activity = (activity as? BaseActivity<*>) ?: return@observe
            onError(exception, activity)
        }
    }

    protected open fun onError(e: Exception, activity: BaseActivity<*>) {
        if (activity.checkAuthError(e)) return
    }

    protected fun <Data> observe(
        liveData: LiveData<Data>,
        onChange: (Data) -> Unit,
    ) {
        liveData.observe(
            viewLifecycleOwner,
            onChange,
        )
    }

    protected fun hideKeyboard() {
        try {
            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    protected fun hideKeyboard(dialog: Dialog?) {
        try {
            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dialog?.currentFocus?.windowToken, 0)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    protected fun handleErrorEventAsToast(event: Event<Exception>, @StringRes description: Int) {
        handleErrorEventAsToast(event, getString(description))
    }

    protected fun handleErrorEventAsToast(event: Event<Exception>, description: String? = null) {
        event.getContentIfNotHandled()?.getErrorText()?.let { text ->
            Toast.makeText(
                requireContext(),
                description ?: text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    protected fun handleErrorEventAsSnackBar(
        event: Event<Exception>,
        @StringRes
        buttonStringRes: Int = R.string.common_update,
        onButtonClick: () -> Unit
    ) {
        event.getContentIfNotHandled()?.getErrorText()?.let { text ->
            view?.let { viewNonNull ->
                Snackbar
                    .make(viewNonNull, text, Snackbar.LENGTH_INDEFINITE)
                    .setAction(buttonStringRes) {
                        onButtonClick()
                    }
                    .show()
            }
        }
    }

    protected fun handleErrorEventAsAlert(
        event: Event<Exception>,
        cancelable: Boolean = true,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null
    ) {
        event.getContentIfNotHandled()?.getErrorText()?.let { text ->
            (activity as? BaseActivity<*>)?.showErrorAlert(
                text,
                cancelable,
                positiveAction,
                negativeAction
            )
        }
    }

    private fun Exception.getErrorText(): String? {
        return when {
            activity.checkAuthError(this) -> null
            isNetworkException() -> getString(R.string.error_no_network_title)
            this is InvalidInputError -> getText(requireContext())
            else -> localizedMessage
        }
    }

    private var lastClickTime: Long = 0
    private val minClickGap: Long = 1500

    protected fun isClickingTooFast(): Boolean {
        if (System.currentTimeMillis() - lastClickTime < minClickGap) {
            lastClickTime = System.currentTimeMillis()
            return true
        }
        lastClickTime = System.currentTimeMillis()
        return false
    }

    @SuppressLint("ApplySharedPref", "CommitPrefEdits")
    protected fun logCameraReleased() {
        sharedPreferences.edit()
            .putLong(Constants.KEY_CAMERA_RELEASE_TIMESTAMP, System.currentTimeMillis()).commit()
    }

    abstract fun getViewModel(): BaseViewModel

    protected open fun back() {
        (activity as? BackPressHandler)?.onBackPressed()
    }

    protected fun backTo(tag: String) {
        (activity as? BackPressHandler)?.backTo(tag)
    }

    protected fun backTo(classTag: Class<*>) = backTo(classTag.name)

    protected fun lockBackPress() {
        (activity as? BackPressHandler)?.setBackPressLocked(true)
    }

    protected fun unlockBackPress() {
        (activity as? BackPressHandler)?.setBackPressLocked(false)
    }

    protected fun sendLogFile() {
        (activity as? BaseActivity<*>)?.sendLogFile()
    }
}