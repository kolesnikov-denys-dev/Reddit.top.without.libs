package com.vrg.soft.test.utils.cache

import com.vrg.soft.test.utils.common.Constants.BUFFER_SIZE
import java.io.InputStream
import java.io.OutputStream

object Utils {
    fun copyStream(iStream: InputStream, oStream: OutputStream) {
        try {
            val bytes = ByteArray(BUFFER_SIZE)
            while (true) {
                val count = iStream.read(bytes, 0, BUFFER_SIZE)
                if (count == -1) break
                oStream.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
        }
    }
}