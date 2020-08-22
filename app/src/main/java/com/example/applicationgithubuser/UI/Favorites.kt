package com.example.applicationgithubuser.UI

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationgithubuser.adapter.MyAdapter
import com.example.applicationgithubuser.adapter.UserGithub
import com.example.applicationgithubuser.R
import com.example.applicationgithubuser.db.FavoritesHelper
import com.example.applicationgithubuser.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_favorites.splash_image
import kotlinx.android.synthetic.main.activity_favorites.welcome_message
import kotlinx.android.synthetic.main.activity_favorites.progressBar


class Favorites : AppCompatActivity() {
    private lateinit var rvUser: RecyclerView
    private val waitingTime = 500
    private var cntr: CountDownTimer? = null
    private var mSearchQuery: String? = null
    private var defaultText = ""
    private var users = arrayListOf<UserGithub>()
    lateinit var listGithubUser: MyAdapter
    private lateinit var favoriteHelper: FavoritesHelper

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private const val STATE_RESULT = "state_result"


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = "Favorites"
        }

        favoriteHelper = FavoritesHelper.getInstance(applicationContext)
        favoriteHelper.open()
        rvUser = findViewById(R.id.favorites_layout)
        rvUser.setHasFixedSize(true)

        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser


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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu_favorites, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search_favorites).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                mSearchQuery = query;
                Toast.makeText(this@Favorites, query, Toast.LENGTH_SHORT).show()
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                mSearchQuery = newText;
                cntr?.cancel()
                cntr = object : CountDownTimer(waitingTime.toLong(), 20) {
                    override fun onTick(millisUntilFinished: Long) {
                        Log.d(
                            "TIME",
                            "seconds remaining: " + millisUntilFinished / 1000
                        )
                    }

                    override fun onFinish() {
                        Log.d("FINISHED", "DONE")
                        prepare(newText)
                    }
                }
                (cntr as CountDownTimer).start()
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_language_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }

        if (item.itemId == R.id.action_change_settings) {
            val moveIntent = Intent(this@Favorites, MainActivity::class.java)
            startActivity(moveIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun prepare(newText: String) {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = favoriteHelper.querybyUserName(newText)
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
                showSnackbarMessage(getString(R.string.no_favorites))

            }
        }


    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(favorites_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showRecyclerList() {

        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser

    }


}