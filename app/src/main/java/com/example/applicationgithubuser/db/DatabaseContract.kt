package com.example.applicationgithubuser.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class UserColumnns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite_user"
            val _ID = "_id"
            val COLUMN_NAME_USERNAME = "username"
            val COLUMN_NAME_FULLNAME = "fullname"
            val COLUMN_NAME_AVATAR_URL= "avatar_url"
            val COLUMN_NAME_COMPANY="company"
            val COLUMN_NAME_LOCATION="location"
            val COLUMN_NAME_TOTALREPOSITORIES="total_repositories"
            val COLUMN_NAME_FOLLOWERS="followers"
            val COLUMN_NAME_FOLLOWINGS="followings"


        }
    }
}