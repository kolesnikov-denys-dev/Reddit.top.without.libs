package com.vrg.soft.test.database

class NotesContract {
    object NotesEntry {
        const val ID = "id"
        const val TABLE_NAME = "posts"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DATE_ADD = "dateAdded"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val COLUMN_COMMENTS_COUNTER = "commentsCounter"
        private const val TYPE_TEXT = "TEXT"
        private const val TYPE_INTEGER = "INTEGER"
        const val CREATE_COMMAND = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" + ID + " " + TYPE_INTEGER + " PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_AUTHOR + " " + TYPE_TEXT + ", "
                + COLUMN_TITLE + " " + TYPE_TEXT + ", "
                + COLUMN_DATE_ADD + " " + TYPE_INTEGER + ", "
                + COLUMN_THUMBNAIL + " " + TYPE_TEXT + ", "
                + COLUMN_IMAGE_URL + " " + TYPE_TEXT + ", "
                + COLUMN_COMMENTS_COUNTER + " " + TYPE_TEXT + ")")
        const val DROP_COMMAND = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val DELETE_ALL_COMMAND = "delete from $TABLE_NAME"
    }
}