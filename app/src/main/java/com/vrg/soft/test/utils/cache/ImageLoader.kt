package com.vrg.soft.test.utils.cache

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.vrg.soft.test.R
import com.vrg.soft.test.utils.common.Constants.REQUIRED_SIZE
import com.vrg.soft.test.utils.common.Constants.TIME_OUT
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoader(context: Context) {
    private var memoryCache = MemoryCache()
    private var fileCache: FileCache = FileCache(context)
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    private var executorService: ExecutorService = Executors.newFixedThreadPool(5)
    private var placeHolder = R.drawable.ic_reddit

    fun displayImage(url: String, loader: Int, imageView: ImageView) {
        placeHolder = loader
        imageViews[imageView] = url
        val bitmap = memoryCache[url]
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            queuePhoto(url, imageView)
            imageView.setImageResource(loader)
        }
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        val p = PhotoToLoad(url, imageView)
        executorService.submit(PhotosLoader(p))
    }

    fun getBitmap(url: String): Bitmap? {
        val f = fileCache.getFile(url)
        val b = decodeFile(f)
        return b ?: try {
            var bitmap: Bitmap? = null
            val imageUrl = URL(url)
            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = TIME_OUT
            conn.readTimeout = TIME_OUT
            conn.instanceFollowRedirects = true
            val iStream = conn.inputStream
            val oStream: OutputStream = FileOutputStream(f)
            Utils.copyStream(iStream, oStream)
            oStream.close()
            bitmap = decodeFile(f)
            bitmap
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    //decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File): Bitmap? {
        try {
            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            //Find the correct scale value. It should be the power of 2.

            var widthTmp = o.outWidth
            var heightTmp = o.outHeight
            var scale = 1
            while (true) {
                if (widthTmp / 2 < REQUIRED_SIZE || heightTmp / 2 < REQUIRED_SIZE) break
                widthTmp /= 2
                heightTmp /= 2
                scale *= 2
            }

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

    //Task for the queue
    inner class PhotoToLoad(var url: String, var imageView: ImageView)
    internal inner class PhotosLoader(var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad)) return
            val bmp = getBitmap(photoToLoad.url)
            memoryCache.put(photoToLoad.url, bmp!!)
            if (imageViewReused(photoToLoad)) return
            val bd = BitmapDisplay(bmp, photoToLoad)
            val a = photoToLoad.imageView.context as Activity
            a.runOnUiThread(bd)
        }
    }

    fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        return tag == null || tag != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    internal inner class BitmapDisplay(var bitmap: Bitmap?, var photoToLoad: PhotoToLoad) :
        Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad)) return
            if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap) else photoToLoad.imageView.setImageResource(
                placeHolder
            )
        }
    }

    fun clearCache() {
        memoryCache.clear()
        fileCache.clear()
    }
}