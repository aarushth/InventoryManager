package com.leopardseal.inventorymanagerapp.ui

import android.app.Activity
import android.content.Intent

fun<A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also{
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun saveImageTemporarily(context: Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file
}