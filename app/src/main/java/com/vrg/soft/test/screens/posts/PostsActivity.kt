package com.vrg.soft.test.screens.posts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vrg.soft.test.R
import com.vrg.soft.test.adapter.OnPostClick
import com.vrg.soft.test.adapter.OnReachEndListener
import com.vrg.soft.test.adapter.PostAdapter
import com.vrg.soft.test.database.Database
import com.vrg.soft.test.screens.image.ImageActivity
import com.vrg.soft.test.utils.common.Constants.KEY_IMAGE_URL
import com.vrg.soft.test.utils.common.Constants.POST_ACTIVITY_LOADER_ID
import com.vrg.soft.test.utils.network.JSONUtils
import com.vrg.soft.test.utils.network.NetworkUtils
import com.vrg.soft.test.utils.network.NetworkUtils.KEY_URL
import com.vrg.soft.test.utils.network.NetworkUtils.LOAD_LIMIT
import org.json.JSONObject

class PostsActivity : AppCompatActivity(), OnPostClick, OnReachEndListener,
    LoaderManager.LoaderCallbacks<JSONObject> {

    lateinit var database: Database
    private var loaderManager: LoaderManager? = null
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private var after = ""
    private var firstDatabase = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isStoragePermissionGranted()

        database = Database(this)

        loaderManager = LoaderManager.getInstance(this)
        recyclerView = findViewById(R.id.recyclerViewPosts)
        progressBarLoading = findViewById(R.id.progressBarLoading)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(this, this, this)
        recyclerView.adapter = adapter

        downloadDataFinal(LOAD_LIMIT)
    }

    private fun downloadDataFinal(limit: Int, after: String? = null) {
        val url = NetworkUtils.buildURL(limit, after)
        val bundle = Bundle()
        bundle.putString(KEY_URL, url.toString())
        loaderManager!!.restartLoader(POST_ACTIVITY_LOADER_ID, bundle, this)
    }

    override fun onReachEnd() {
        downloadDataFinal(LOAD_LIMIT, after)
    }

    override fun onImageClick(position: Int) {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra(KEY_IMAGE_URL, adapter.getPostByPosition(position).imageUrl)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): NetworkUtils.JSONLoader {
        val jsonLoader = NetworkUtils.JSONLoader(this, args)
        jsonLoader.setOnStartLoadingListener(object :
            NetworkUtils.JSONLoader.OnStartLoadingListener {
            override fun onStartLoading() {
                progressBarLoading.visibility = View.VISIBLE
            }
        })
        return jsonLoader
    }

    override fun onLoadFinished(loader: Loader<JSONObject>, data: JSONObject?) {
        progressBarLoading.visibility = View.GONE
        if (data != null) {
            val result = data.let { JSONUtils.getPostsFromJson(it) }
            after = result.after
            database.deleteAllDataBase()
            for (item in result.postsList) {
                database.insertPostDatabase(item)
            }
            adapter.addPosts(result.postsList)
        } else {
            if (!firstDatabase) {
                adapter.addPosts(database.getPostsFromDatabase())
                firstDatabase = true
            }
        }
        loaderManager!!.destroyLoader(POST_ACTIVITY_LOADER_ID)
    }

    override fun onLoaderReset(loader: Loader<JSONObject>) {}

    private fun isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        } else {
            Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show()
        }
    }
}