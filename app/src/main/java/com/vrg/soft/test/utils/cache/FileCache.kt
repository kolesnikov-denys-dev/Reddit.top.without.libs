package com.vrg.soft.test.utils.cache

import android.content.Context
import android.os.Environment
import com.vrg.soft.test.utils.common.Constants.PATH_TEMP_IMAGES
import java.io.File

class FileCache(context: Context) {
    private var cacheDir: File? = null
    init {
        //Find the dir to save cached images
        cacheDir = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) File(
            Environment.getExternalStorageDirectory(),
            PATH_TEMP_IMAGES
        ) else context.cacheDir
        if (!cacheDir!!.exists()) cacheDir!!.mkdirs()
    }

    fun getFile(url: String): File {
        val filename = url.hashCode().toString()
        return File(cacheDir, filename)
    }

    fun clear() {
        val files = cacheDir!!.listFiles() ?: return
        for (f in files) f.delete()
    }
}