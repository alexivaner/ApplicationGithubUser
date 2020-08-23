package com.example.applicationgithubuser.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.example.applicationgithubuser.R
import com.example.applicationgithubuser.adapter.UserGithub
import com.example.applicationgithubuser.db.DatabaseContract.UserColumnns.Companion.CONTENT_URI
import com.example.applicationgithubuser.db.FavoritesHelper
import com.example.applicationgithubuser.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private var favorites = arrayListOf<UserGithub>()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        val identityToken = Binder.clearCallingIdentity()

        val cursor =  mContext.contentResolver.query(CONTENT_URI, null, null, null, null)
        favorites = MappingHelper.mapCursorToArrayList(cursor)

        for (i in favorites) {
            mWidgetItems.add(getBitmapFromURL(i.avatar)!!)
        }

        Binder.restoreCallingIdentity(identityToken)

    }


    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }


    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            FavoritesWidget.EXTRA_ITEM to position

        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}