package com.axmor.fsinphone.videomessages.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.TextPaint
import android.util.Base64
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.axmor.fsinphone.videomessages.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.random.Random

object ImagesUtils {
    const val JPEG = "jpeg"
    const val EXT_JPG = ".jpg"
    const val EXT_JPEG = ".jpeg"
    const val EXT_PNG = ".png"
    const val IMAGE_PREFIX = "image_"

    fun loadUrl(
        url: String?,
        imageView: ImageView,
        onError: () -> Unit = {},
        onSuccess: (resource: Bitmap) -> Unit = {}
    ) {
        url ?: return
        imageView.post {
            Glide.with(imageView)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.placehoder_image)
                .error(R.drawable.placehoder_image_error)
                .addListener(SimpleListener(onError, onSuccess))
                .into(imageView)
        }
    }

    fun loadLocalFile(
        uri: Uri,
        view: ImageView,
        onError: () -> Unit = {},
        onSuccess: (resource: Bitmap) -> Unit = {}
    ) {

        Glide.with(view)
            .asBitmap()
            .load(uri.path)
            .placeholder(R.drawable.placehoder_image)
            .error(R.drawable.placehoder_image_error)
            .centerCrop()
            .addListener(SimpleListener(onError, onSuccess))
            .into(view)
    }


    fun loadImageFromBase64(view: ImageView, base64String: String) {
        val bitmap = base64ToBitmap(base64String)

        view.post {
            if (view.canLoadImage())
                Glide.with(view)
                    .asBitmap()
                    .load(bitmap)
                    .error(R.drawable.placehoder_image_error)
                    .addListener(SimpleListener({}, {}))
                    .into(view)
        }
    }

    private class SimpleListener(
        private val onError: () -> Unit, private val onSuccess
        : (resource: Bitmap) -> Unit
    ) : RequestListener<Bitmap> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean
        ): Boolean {
            e?.printStackTrace()
            Timber.e("onLoadFailed: ${e?.localizedMessage}; isFirstResource = $isFirstResource; model = $model")
            onError()
            return false
        }

        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            Timber.d("onResourceReady; model = $model")
            if (resource != null) {
                onSuccess(resource)
            }
            return false
        }
    }

    fun drawForegroundIcon(resource: Bitmap, @DrawableRes iconRes: Int, context: Context) {
        val canvas = Canvas(resource)
        val drawable = ContextCompat.getDrawable(context, iconRes)
        if (drawable != null) {
            val drawableWidth = drawable.intrinsicWidth
            val drawableHeight = drawable.intrinsicHeight
            val left = resource.width / 2 - drawableWidth / 2
            val top = resource.height / 2 - drawableHeight / 2
            val right = left + drawableWidth
            val bottom = top + drawableHeight
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)
        }
    }

    fun getDefaultAvatarBase64(name: String): String {
        val avatarBitmap = drawDefaultAvatar(name)
        return bitmapToBase64(avatarBitmap)
    }

    // Аватар без картинки - инициалы на монотонном фоне
    private fun drawDefaultAvatar(name: String): Bitmap {
        val avatarSize = Constants.AVATAR_SIZE
        val bitmap = Bitmap.createBitmap(avatarSize, avatarSize, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val rgb = arrayOf(Random.nextInt(60, 230), Random.nextInt(60, 230), Random.nextInt(60, 230))

        // Фон
        canvas.drawARGB(255, rgb[0], rgb[1], rgb[2])

        if (name.isNotBlank()) {
            val textPaint = TextPaint()

            textPaint.apply {
                textAlign = Paint.Align.CENTER
                textSize = Constants.AVATAR_SIZE / 2.17f
                isAntiAlias = true
                color = Color.argb(255, rgb[0] - 50, rgb[1] - 50, rgb[2] - 50)
            }

            // Положение текста
            val x = canvas.width / 2f
            val y = ((canvas.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2))

            // Первая и, если есть через пробел, вторая буквы имени
            var initials =
                name.first().toString() + (name.substringAfter(" ", "").firstOrNull() ?: "")
            initials = initials.uppercase(Locale.ROOT)
            Timber.d("name = $name; initials = $initials")

            canvas.drawText(initials, x, y, textPaint)
        }

        return bitmap
    }

    @Throws(Exception::class)
    fun getExifRotatedFile(file: File, newImageFile: File? = null): File {
        val bitmap = getExifRotatedBitmap(file.path)
        return newImageFile?.let { bitmapToFile(bitmap, newImageFile) } ?: bitmapToFile(bitmap, file)
    }

    @Throws(Exception::class)
    fun getExifRotatedBitmap(filepath: String): Bitmap {
        Timber.i("setAppropriateImageRotation for $filepath")
        val pathWithFormat =
            if (filepath.endsWith(EXT_JPG) || filepath.endsWith(EXT_JPEG) || filepath.endsWith(EXT_PNG)) filepath else filepath + EXT_JPEG

        val exif = ExifInterface(pathWithFormat)
        val attributeInt = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (attributeInt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImageFile(
                pathWithFormat,
                90,
                Bitmap.Config.RGB_565
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImageFile(
                pathWithFormat,
                180,
                Bitmap.Config.RGB_565
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImageFile(
                pathWithFormat,
                270,
                Bitmap.Config.RGB_565
            )
            else -> rotateImageFile(
                pathWithFormat,
                0,
                Bitmap.Config.RGB_565
            )
        }
    }

    @Throws(Exception::class)
    fun rotateImageFile(filepath: String, degrees: Int, inPreferredConfig: Bitmap.Config): Bitmap {
        val readOptions = BitmapFactory.Options()
        readOptions.inPreferredConfig = inPreferredConfig

        val originalBitmap = fileToBitmap(filepath, readOptions)

        if (degrees == 0) return originalBitmap

        val matrix = Matrix()
        matrix.setRotate(degrees.toFloat())
        return Bitmap.createBitmap(
            originalBitmap, 0, 0,
            originalBitmap.width, originalBitmap.height, matrix, false
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun resizeImageProportionally(bitmap: Bitmap, biggestSide: Int)
            : Bitmap = withContext(Dispatchers.IO) {
        if (bitmap.width < biggestSide && bitmap.height < biggestSide) {
            return@withContext bitmap
        }

        // proportions of new size
        val widthBigger = bitmap.width > bitmap.height
        val width =
            if (widthBigger) biggestSide else bitmap.width * biggestSide / bitmap.height
        val height =
            if (!widthBigger) biggestSide else bitmap.height * biggestSide / bitmap.width

        return@withContext Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    /**
     * Уменьшает вес изображения.
     * @param context контекст
     * @param image изображение
     * @param maxFileSize требуемый размер в байтах
     * @return сжатое изображение в новом файле
     */
    suspend fun compressToDesiredFileSize(context: Context, image: File, maxFileSize: Int): File? =
        withContext(Dispatchers.IO) {
            if (image.length() <= maxFileSize) return@withContext image

            val bitmap = fileToBitmap(image.path)

            Timber.i("Compressing image... Init. size: ${image.length()} bytes, desired size $maxFileSize bytes")

            val bmpStream = ByteArrayOutputStream()
            var compressQuality = 100
            var resultSize = maxFileSize

            while (resultSize >= maxFileSize && compressQuality >= 5) {
                try {
                    bmpStream.flush()
                    bmpStream.reset()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                resultSize = bmpStream.size()

                Timber.i("Compressing image... Quality: $compressQuality, size: ${bmpStream.size()}")

                compressQuality -= 5
            }

            bmpStream.close()

            val newImageFile = createImageFile(context)
            val outputStream: FileOutputStream?

            try {
                outputStream = FileOutputStream(newImageFile)
                outputStream.write(bmpStream.toByteArray())
                outputStream.flush()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Timber.i("Compressing image... Result size: ${newImageFile.length()} bytes")

            return@withContext if (newImageFile.length() <= maxFileSize) newImageFile else null
        }

    fun createImageFile(context: Context, extension: String = ".$JPEG"): File {
        val outputDirectory = Utils.getOutputDirectory(context)
        return Utils.createFile(outputDirectory, prefix = IMAGE_PREFIX, extension = extension)
    }

    //Converters
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 100): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        val byteArray = base64ToByteArray(base64String)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun base64ToByteArray(base64String: String): ByteArray =
        Base64.decode(base64String, Base64.DEFAULT)

    fun fileToBitmap(filepath: String, options: BitmapFactory.Options? = null): Bitmap =
        BitmapFactory.decodeFile(filepath, options)

    @Throws(Exception::class)
    fun bitmapToFile(bitmap: Bitmap, file: File): File {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()

        return file
    }

    private fun ImageView.canLoadImage(): Boolean {
        val activity = (context as? ContextWrapper)?.baseContext as? Activity
        return activity == null || (!activity.isDestroyed && !activity.isFinishing)
    }
}