package uk.ac.tees.mad.w9642974.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

fun Uri.getFileType(context: Context): String? {
    val resolver: ContentResolver = context.contentResolver
    val mimeType: String? = resolver.getType(this)
    return mimeType?.split("/")?.get(1)
}