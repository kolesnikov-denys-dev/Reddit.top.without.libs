package com.vrg.soft.test.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.vrg.soft.test.database.NotesContract.NotesEntry.COLUMN_COMMENTS_COUNTER
import com.vrg.soft.test.database.NotesContract.NotesEntry.DELETE_ALL_COMMAND
import com.vrg.soft.test.model.Post

class Database(context: Context) {
    private var dbHelper: NotesDBHelper? = null
    private var database: SQLiteDatabase? = null

    init {
        dbHelper = NotesDBHelper(context)
        database = dbHelper!!.writableDatabase
    }

    fun getPostsFromDatabase() :MutableList<Post>{
        val list = mutableListOf<Post>()
        val cursor = database!!.query(
            NotesContract.NotesEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.ID))
            val author = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_AUTHOR))
            val tittle = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_TITLE))
            val dateAdded = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DATE_ADD))
            val thumbnail = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_THUMBNAIL))
            val imageUrl = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_IMAGE_URL))
            val commentsCounter =
                cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_COMMENTS_COUNTER))
            val post = Post(id, author, tittle, dateAdded, thumbnail, imageUrl, commentsCounter.toString())
            list.add(post)
        }
        cursor.close()
        return list
    }

    fun deleteAllDataBase() {
        database?.execSQL(DELETE_ALL_COMMAND);
    }

     fun insertPostDatabase(post: Post) {
        val contentValues = ContentValues()
        contentValues.put(NotesContract.NotesEntry.COLUMN_AUTHOR, post.author)
        contentValues.put(NotesContract.NotesEntry.COLUMN_TITLE, post.title)
        contentValues.put(NotesContract.NotesEntry.COLUMN_DATE_ADD, post.dateAdded)
        contentValues.put(NotesContract.NotesEntry.COLUMN_THUMBNAIL, post.thumbnail)
        contentValues.put(NotesContract.NotesEntry.COLUMN_IMAGE_URL, post.imageUrl)
        contentValues.put(COLUMN_COMMENTS_COUNTER, post.commentsCounter)
        database!!.insert(NotesContract.NotesEntry.TABLE_NAME, null, contentValues)
    }

}