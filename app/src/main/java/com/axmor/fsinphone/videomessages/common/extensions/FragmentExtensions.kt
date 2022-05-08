package com.axmor.fsinphone.videomessages.common.extensions

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.ui.common.BaseActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber


fun Fragment.pickImageFromGallery(requestCode: Int) {
    Dexter
        .withContext(requireContext())
        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report?.areAllPermissionsGranted() == true) {
                    sendPickImageIntent(requestCode)
                } else {
                    if (report != null) {
                        report.deniedPermissionResponses.forEach {
                            Timber.w("${it.permissionName} was denied")
                        }
                    } else
                        Timber.w("not all permissions were granted")
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                Timber.w("onPermissionRationaleShouldBeShown")
                token?.continuePermissionRequest()
            }
        })
        .check()
}

private fun Fragment.sendPickImageIntent(requestCode: Int){
    try {
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/*"

        startActivityForResult(
            pickIntent,
            requestCode
        )
    }
    catch (e: ActivityNotFoundException){
        showAlertGalleryNotFound()
    }
}

private fun Fragment.showAlertGalleryNotFound(){
    context?.let {
        AlertDialog.Builder(it)
            .setMessage(R.string.error_no_gallery_found_message)
            .setNegativeButton(R.string.common_close) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.common_search) { _, _ -> sendSearchGalleryAppIntent() }
            .create()
            .show()
    }
}

private fun Fragment.sendSearchGalleryAppIntent(){
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://search?q=галерея")
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/search?q=галерея&c=apps")
            )
        )
    }
}