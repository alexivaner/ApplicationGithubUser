package com.example.applicationgithubuser.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.applicationgithubuser.db.DatabaseContract.UserColumnns.Companion.TABLE_NAME
import com.example.applicationgithubuser.db.DatabaseContract.UserColumnns.Companion._ID
import java.sql.SQLException

class FavoritesHelper(context: Context) {
    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var dataBaseHelper: DatabaseHelper
        private var INSTANCE: FavoritesHelper? = null
        fun getInstance(context: Context): FavoritesHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesHelper(context)
            }


    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        Companion.dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC"
        )
    }


    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }


    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }

    init {
        dataBaseHelper = DatabaseHelper(context)
    }
}