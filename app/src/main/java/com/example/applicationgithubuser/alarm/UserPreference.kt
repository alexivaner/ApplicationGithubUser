package com.example.applicationgithubuser.alarm

import android.content.Context

internal class UserPreference(context: Context) {
    companion object {
        private const val DAILY_REMINDER = "isOn"
    }

    private val preferences = context.getSharedPreferences(DAILY_REMINDER, Context.MODE_PRIVATE)

    fun setReminder(value:Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(DAILY_REMINDER, value)
        editor.apply()
    }

    fun getReminder(): Boolean {
        var value = preferences.getBoolean(DAILY_REMINDER, false)
        return value
    }
}