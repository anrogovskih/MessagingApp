package com.axmor.fsinphone.videomessages.ui.common

import android.app.Activity
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.axmor.fsinphone.videomessages.App
import com.axmor.fsinphone.videomessages.BuildConfig
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.HttpException
import timber.log.Timber


abstract class BaseActivity<T : ViewDataBinding>(
    @LayoutRes private var layoutResource: Int
) : AppCompatActivity(), BaseInteractor {

    protected val compositeDisposable = CompositeDisposable()
    protected val onStopDisposable = CompositeDisposable()
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        if (getType() == ActivityType.FULLSCREEN) {
            setTheme(R.style.ActivityFullScreen)
        }
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResource)
        binding.lifecycleOwner = this
    }

    override fun onStop() {
        super.onStop()
        onStopDisposable.clear()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    open fun getType(): ActivityType {
        return ActivityType.SIMPLE
    }

    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus

        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun handleError(exception: HttpException){
        Timber.w("${javaClass.simpleName}: we received HttpException with code ${exception.code()} and message ${exception.localizedMessage}")
        when (exception.code()){
            500, 503 -> showErrorAlert(getString(R.string.error_500))
            else -> showErrorAlert(null)
        }
    }

    fun showErrorAlert(
        message: String?,
        cancelable: Boolean = true,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.common_error)
            .setMessage(message ?: getString(R.string.errors_unknown))
            .setNegativeButton(R.string.common_close) { dialog, _ ->
                negativeAction?.invoke()
                dialog.dismiss()
            }
            .setCancelable(cancelable)

        if (positiveAction != null) {
            builder.setPositiveButton(R.string.common_repeat) { _, _ -> positiveAction() }
        }

        builder.create().show()
    }

    fun showNoNetworkAlert(
        cancelable: Boolean,
        hasExit: Boolean,
        repeatAction: (() -> Unit)? = null,
        onExitAction: (() -> Unit)? = null
        ) {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.error_no_network_title)
            .setMessage(R.string.error_no_network_message)
            .setCancelable(cancelable)

        if (hasExit)
            builder.setNegativeButton(R.string.common_exit) { _, _ ->
                onExitAction?.invoke()
                finish()
            }

        if (repeatAction != null)
            builder.setPositiveButton(R.string.common_repeat) { _, _ -> repeatAction() }
        else
            builder.setPositiveButton(R.string.common_close) { _, _ -> }

        builder.create().show()
    }

    override fun showHelp(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.base_activity_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_help -> sendLogFile()
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    //todo: получать logFile из наследника
    fun sendLogFile() {
        (application as? App)?.logFile?.let {
            val path = if (it.exists())
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", it)
            else
                null

            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                val to = arrayOf(Constants.SUPPORT_EMAIL)
                putExtra(Intent.EXTRA_EMAIL, to)
                if (path != null)
                    putExtra(Intent.EXTRA_STREAM, path)
                putExtra(Intent.EXTRA_SUBJECT, "Запрос разработчикам")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Проблема на устройстве ${Build.MANUFACTURER} ${Build.MODEL} (SDK ${Build.VERSION.SDK_INT}, версия приложения ${BuildConfig.VERSION_NAME}):\n"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            try {
                val chooser = Intent.createChooser(emailIntent, "Отправить сообщение...")
                if (path != null) {
                    val resInfoList = packageManager.queryIntentActivities(
                        emailIntent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )

                    for (resolveInfo in resInfoList) {
                        val packageName: String = resolveInfo.activityInfo.packageName

                        grantUriPermission(
                            packageName,
                            path,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                }
                startActivity(chooser)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, R.string.error_no_mail_client, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Add this in onCreate for activities that are launched from background
     */
    protected fun allowLaunchFromBackground() {
        window.apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
//                setTurnScreenOn(true)
//                setShowWhenLocked(true)
//                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//                (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
//                    .requestDismissKeyguard(this@BaseActivity, null)
//            }
//            else{
                addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED.or(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD))
                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.or(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON))
//            }
        }
    }

    enum class ActivityType {
        SIMPLE,
        FULLSCREEN
    }
}