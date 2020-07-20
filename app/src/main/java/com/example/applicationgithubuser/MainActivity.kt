package com.example.applicationgithubuser

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
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.NullPointerException


class MainActivity : AppCompatActivity() {
    private lateinit var rvUser: RecyclerView
    private val waitingTime = 500
    private var cntr: CountDownTimer? = null
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var avatar: String
    private lateinit var url: String

    private var users = arrayListOf<UserGithub>()
    lateinit var listGithubUser: MyAdapter

    companion object {
        internal const val defaultText = "alexivaner"

        private val TAG = MainActivity::class.java.simpleName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = "Github User"
        }


        rvUser = findViewById(R.id.main_layout)
        rvUser.setHasFixedSize(true)
        prepare(defaultText)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                prepare(query)
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                cntr?.cancel()
                cntr = object : CountDownTimer(waitingTime.toLong(), 500) {
                    override fun onTick(millisUntilFinished: Long) {
                        Log.d(
                            "TIME",
                            "seconds remaining: " + millisUntilFinished / 1000
                        )
                    }

                    override fun onFinish() {
                        Log.d("FINISHED", "DONE")
                        if(newText.isNullOrEmpty()){
                            prepare(defaultText)
                        }
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
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun prepare(newText: String) {
        progressBar.visibility = View.VISIBLE

        val asyncClient = AsyncHttpClient()
        asyncClient.addHeader("Authorization", "token token change with your token")
        asyncClient.addHeader("User-Agent", "request")
        asyncClient.get(
            "https://api.github.com/search/users?q=$newText",
            object : AsyncHttpResponseHandler() {

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray
                ) {
                    users.clear()
                    progressBar.visibility = View.INVISIBLE
                    val result = String(responseBody)

                    try {
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONArray("items")
                        Log.d(TAG, items.toString())

                        for (i in 0 until items.length()) {
                            val item = items.getJSONObject(i)
                            userName = item.getString("login")
                            userId = item.getString("id")
                            avatar = item.getString("avatar_url")
                            url = item.getString("url")

                            val user = UserGithub()
                            user.username = userName
                            user.userid = userId
                            user.avatar = avatar
                            user.url=url
                            users.add(user)
                            showRecyclerList()

                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>,
                    responseBody: ByteArray,
                    error: Throwable
                ) {
                    progressBar.visibility = View.INVISIBLE
                    val errorMessage = when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        422 -> "Default will return my github alexivaner"
                        else -> "$statusCode : ${error.message}"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun showRecyclerList() {

        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser = MyAdapter(users)
        rvUser.adapter = listGithubUser

    }
}