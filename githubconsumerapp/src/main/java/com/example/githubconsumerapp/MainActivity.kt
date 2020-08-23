package com.example.githubconsumerapp


import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubconsumerapp.adapter.MyAdapter
import com.example.githubconsumerapp.adapter.UserGithub

import com.example.githubconsumerapp.db.DatabaseContract.UserColumnns.Companion.CONTENT_URI
import com.example.githubconsumerapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var rvUser: RecyclerView

    private var defaultText = ""
    private var users = arrayListOf<UserGithub>()
    private lateinit var listGithubUser: MyAdapter

    companion object {
        private const val STATE_RESULT = "state_result"


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = getString(R.string.consumer_app_statusbar)
        }

        rvUser = findViewById(R.id.favorites_layout)
        rvUser.setHasFixedSize(true)

        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                prepare(defaultText)
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)


        if (savedInstanceState == null) {
            prepare(defaultText)
        } else {
            val list = savedInstanceState.getParcelableArrayList<UserGithub>(STATE_RESULT)
            if (list != null) {
                listGithubUser.listDataFilter = list
                progressBar.visibility = View.INVISIBLE

            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(STATE_RESULT, listGithubUser.listDataFilter)
    }

    override fun onResume() {
        super.onResume()
        prepare(defaultText)

    }



    private fun prepare(newText: String) {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressBar.visibility = View.INVISIBLE

            users = deferredNotes.await()
            if (users.size > 0) {
                showRecyclerList()
                welcome_message.visibility=View.INVISIBLE
                splash_image.visibility=View.INVISIBLE
                splash_image_2.alpha= 0.1F
                splash_image_2.visibility=View.VISIBLE
            } else {
                showRecyclerList()
                splash_image_2.visibility=View.INVISIBLE
                welcome_message.visibility=View.VISIBLE
                splash_image.visibility=View.VISIBLE
                showSnackMessage(getString(R.string.no_favorites))

            }
        }


    }

    private fun showSnackMessage(message: String) {
        Snackbar.make(favorites_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showRecyclerList() {

        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser

    }


}
