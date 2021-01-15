package com.vrg.soft.test.utils.network

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.loader.content.AsyncTaskLoader
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object NetworkUtils {
    const val BASE_URL = "https://www.reddit.com/top.json"
    private const val PARAMS_AFTER = "after"
    private const val PARAMS_LIMIT = "limit"
    const val KEY_URL = "url"
    const val LOAD_LIMIT = 20

    class JSONLoader(context: Context, private val bundle: Bundle?) : AsyncTaskLoader<JSONObject?>(context) {
        private var onStartLoadingListener: OnStartLoadingListener? = null

        interface OnStartLoadingListener {
            fun onStartLoading()
        }

        fun setOnStartLoadingListener(onStartLoadingListener: OnStartLoadingListener?) {
            this.onStartLoadingListener = onStartLoadingListener
        }

        override fun onStartLoading() {
            super.onStartLoading()
            if (onStartLoadingListener != null) {
                onStartLoadingListener!!.onStartLoading()
            }
            forceLoad()
        }

        override fun loadInBackground(): JSONObject? {
            if (bundle == null) {
                return null
            }
            val urlAsString = bundle.getString(KEY_URL)
            var url: URL? = null
            try {
                url = URL(urlAsString)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            var result: JSONObject? = null
            if (url == null) {
                return null
            }
            var connection: HttpURLConnection? = null
            try {
                connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val inputStreamReader = InputStreamReader(inputStream)
                val reader = BufferedReader(inputStreamReader)
                val builder = StringBuilder()
                var line = reader.readLine()
                while (line != null) {
                    builder.append(line)
                    line = reader.readLine()
                }
                result = JSONObject(builder.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
            return result
        }
    }

    fun buildURL(limit: Int, after: String?): URL? {
        var result: URL? = null
        val uri: Uri = if (after == null) {
            Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_LIMIT, limit.toString())
                .build()
        } else {
            Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_LIMIT, limit.toString())
                .appendQueryParameter(PARAMS_AFTER, after)
                .build()
        }
        try {
            result = URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return result
    }
}