package com.example.ch12.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.ch12.util.PROFILE_PHOTO_QUALITY
import com.example.ch12.util.PROFILE_PHOTO_SIZE
import com.example.ch12.util.calculateInSampleSize
import com.example.ch12.util.centerSquare
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

// Foto 4000x3000 -> JPEG persegi 400x400, sekitar 30-60 KB
class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {

    // Decode dan kompres JPEG adalah kerja CPU + I/O: jangan di main thread
    override suspend fun compress(uri: String): CompressedImage = withContext(Dispatchers.Default) {
        val source = Uri.parse(uri)

        // 1. Dekode batas (bounds)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(source).use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw ImageDecodeException("Berkas yang dipilih bukan gambar.")
        }

        // 2. Dekode dengan downsampling
        val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, PROFILE_PHOTO_SIZE)
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val sampled = open(source).use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        } ?: throw ImageDecodeException("Berkas yang dipilih bukan gambar.")

        // 3. Potong persegi (crop) & ubah skala + rotasi EXIF
        val crop = centerSquare(sampled.width, sampled.height)
        val rotation = readRotation(source)
        val matrix = Matrix().apply {
            val scale = minOf(1f, PROFILE_PHOTO_SIZE.toFloat() / crop.size)
            postScale(scale, scale)
            if (rotation != 0) {
                postRotate(rotation.toFloat())
            }
        }

        val transformed = Bitmap.createBitmap(
            sampled,
            crop.left,
            crop.top,
            crop.size,
            crop.size,
            matrix,
            true
        )
        if (transformed != sampled) {
            sampled.recycle()
        }

        // 4. Kompres ke format JPEG
        val out = ByteArrayOutputStream()
        transformed.compress(Bitmap.CompressFormat.JPEG, PROFILE_PHOTO_QUALITY, out)
        val bytes = out.toByteArray()
        val finalWidth = transformed.width
        val finalHeight = transformed.height
        transformed.recycle()

        // 5. Kembalikan hasil
        CompressedImage(
            bytes = bytes,
            originalWidth = bounds.outWidth,
            originalHeight = bounds.outHeight,
            width = finalWidth,
            height = finalHeight
        )
    }

    // content:// (Photo Picker) maupun file:// (CameraX) dibuka lewat ContentResolver
    private fun open(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri)
            ?: throw ImageDecodeException("Gambar tidak dapat dibuka.")

    // Kamera menyimpan piksel apa adanya + tag orientasi. BitmapFactory
    // mengabaikan tag itu, sehingga tanpa ini foto potret bisa tampil miring.
    private fun readRotation(uri: Uri): Int = runCatching {
        open(uri).use { ExifInterface(it).rotationDegrees }
    }.getOrDefault(0)
}
