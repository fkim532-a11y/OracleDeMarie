package com.kim.oraclemarie.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

internal fun cardImageUri(context: Context, imageResId: Int, fileName: String): Uri? {
    if (imageResId == 0) return null
    return try {
        val drawable = ContextCompat.getDrawable(context, imageResId) ?: return null
        val bitmap = (drawable as? BitmapDrawable)?.bitmap
            ?: Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).also {
                val canvas = android.graphics.Canvas(it)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        val dir = File(context.cacheDir, "shared_images").also { it.mkdirs() }
        val file = File(dir, "$fileName.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 95, it) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (e: Exception) { null }
}

internal fun themeLabel(theme: String): String {
    val isFr = Locale.getDefault().language == "fr"
    return when (theme) {
        "spirituel" -> if (isFr) "Spirituel" else "Spiritual"
        "amour"     -> if (isFr) "Amour" else "Love"
        "travail"   -> if (isFr) "Travail" else "Work"
        "sante"     -> if (isFr) "Santé" else "Health"
        "famille"   -> if (isFr) "Famille" else "Family"
        else        -> theme.replaceFirstChar { it.uppercase() }
    }
}
