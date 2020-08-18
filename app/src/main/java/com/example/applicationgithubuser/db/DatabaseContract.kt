package com.example.applicationgithubuser.db

import android.provider.BaseColumns

object DatabaseContract {

    internal class UserColumnns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite_user"
            val _ID = "_id"
            val COLUMN_NAME_USERNAME = "username"
            val COLUMN_NAME_AVATAR_URL= "avatar_url"
            val COLUMN_USER_URL= "user_url"
            val COLUMN_NAME_USER_ID= "user_id"



        }
    }
}