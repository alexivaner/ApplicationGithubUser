package com.example.applicationgithubuser.helper

import android.database.Cursor
import com.example.applicationgithubuser.adapter.UserGithub
import com.example.applicationgithubuser.db.DatabaseContract
import java.util.ArrayList

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<UserGithub> {
        val users = ArrayList<UserGithub>()

        notesCursor?.apply {
            while (moveToNext()) {
                val user =
                    UserGithub()
                user.username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_NAME_USERNAME))
                user.userid = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_NAME_USER_ID))
                user.avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_NAME_AVATAR_URL))
                user.url = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_USER_URL))
                users.add(user)
            }
        }
        return users
    }

    fun mapCursorToObject(notesCursor: Cursor?): UserGithub {
        val users = ArrayList<UserGithub>()

        var user = UserGithub()
        notesCursor?.apply {
            moveToFirst()
            val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_NAME_USERNAME))
            val userid = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_NAME_USER_ID))
            val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_NAME_AVATAR_URL))
            val url = getString(getColumnIndexOrThrow(DatabaseContract.UserColumnns.COLUMN_USER_URL))
            user = UserGithub(username, userid, avatar, url)

        }
        return user
    }
}