package com.vrg.soft.test.utils.network

import com.vrg.soft.test.model.Post
import com.vrg.soft.test.model.PostsResponse
import com.vrg.soft.test.utils.time.HumanDateUtils
import org.json.JSONObject

object JSONUtils {
    private const val KEY_DATA = "data"
    private const val KEY_AFTER = "after"
    private const val KEY_CHILDREN = "children"
    private const val KEY_AUTHOR = "author"
    private const val KEY_TITLE = "title"
    private const val KEY_NUM_COMMENTS = "num_comments"
    private const val KEY_THUMBNAIL = "thumbnail"
    private const val KEY_URL = "url"
    private const val KEY_CREATED_UTC = "created_utc"

    fun getPostsFromJson(json: JSONObject): PostsResponse {
        val list = mutableListOf<Post>()
        val data = json.getJSONObject(KEY_DATA)
        val childrenArray = data.getJSONArray(KEY_CHILDREN)
        for (index in 0 until childrenArray.length()) {
            val objData = childrenArray.getJSONObject(index).getJSONObject(KEY_DATA)
            val author = objData.getString(KEY_AUTHOR)
            val title = objData.getString(KEY_TITLE)
            val numComments = objData.getInt(KEY_NUM_COMMENTS)
            val thumbnail = objData.getString(KEY_THUMBNAIL)
            val imageUrl = objData.getString(KEY_URL) //url_overridden_by_dest
            val dateAdded = HumanDateUtils.durationFromNow(objData.getLong(KEY_CREATED_UTC) * 1000)
            list.add(
                Post(
                    0,
                    author,
                    title,
                    dateAdded,
                    thumbnail,
                    imageUrl,
                    numComments.toString()
                )
            )
        }
        return PostsResponse(data.getString(KEY_AFTER), list)
    }
}