package org.panta.misskey_for_android_v2.network

import android.util.Log
import okhttp3.*
import java.io.File
import java.net.URL

class OkHttpConnection{
    fun postString(url: URL, value: String): String?{
        return try{
            val client = OkHttpClient()
            val mediaType = MediaType.parse("application/json; charset=utf-8")
            val requestBody = RequestBody.create(mediaType, value)
            val request = Request.Builder().url(url).post(requestBody).build()
            val response = client.newCall(request).execute()
            val code = response.code()
            Log.d("OkHttpConnection", "statusCode:$code")
            if(code < 300){
                response.body()?.string()
            }else{
                null
            }

        }catch(e: Exception){
            Log.w("OkHttpConnection", "error", e)
            null
        }

    }

    fun postFile(url: URL,i: String, file: File, force: Boolean, isSensitive: Boolean? = null): String?{
        return try{
            val mime = "image/jpg"
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .addFormDataPart("i", i)
                .addFormDataPart("force", force.toString())
                .addFormDataPart("file", file.name, RequestBody.create(MediaType.parse(mime), file)).build()

            val request = Request.Builder().url(url).post(requestBody).build()
            val response = client.newCall(request).execute()
            val code =response.code()
            if(code < 300){
                response.body()?.string()
            }else{
                null
            }
        }catch(e: Exception){
            Log.w("OkHttpConnection", "post file error", e)
            null
        }

    }

}