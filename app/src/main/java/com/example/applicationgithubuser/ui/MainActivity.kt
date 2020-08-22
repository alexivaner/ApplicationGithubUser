package com.example.applicationgithubuser.ui

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
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationgithubuser.adapter.MyAdapter
import com.example.applicationgithubuser.adapter.UserGithub
import com.example.applicationgithubuser.alarm.AlarmReceiver
import com.example.applicationgithubuser.R
import com.example.applicationgithubuser.alarm.UserPreference
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progressBar
import kotlinx.android.synthetic.main.activity_main.splash_image
import kotlinx.android.synthetic.main.activity_main.splash_image_2
import kotlinx.android.synthetic.main.activity_main.welcome_message
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var rvUser: RecyclerView
    private val waitingTime = 500
    private var cntr: CountDownTimer? = null
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var avatar: String
    private lateinit var url: String
    private var mSearchQuery: String? = null
    private var defaultText = ""
    private var users = arrayListOf<UserGithub>()
    lateinit var listGithubUser: MyAdapter
    private var isReminder = true
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var mUserPreference: UserPreference


    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private const val STATE_RESULT = "state_result"


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(STATE_RESULT, listGithubUser.listDataFilter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = getString(R.string.Status_Bar_Main)
        }
        mUserPreference = UserPreference(this)

        /*
        Get  shared preference
        */
        isReminder = mUserPreference.getReminder()

        rvUser = findViewById(R.id.main_layout)
        rvUser.setHasFixedSize(true)
        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser

        welcome_message.visibility = View.VISIBLE
        alarmReceiver = AlarmReceiver()

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
                mSearchQuery = query;
                prepare(query)
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                mSearchQuery = newText;
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
                        if (newText.isNullOrEmpty()) {
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

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val checkable = menu.findItem(R.id.daily_reminder_setting)
        checkable.isChecked = isReminder
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_change_language_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.action_change_settings -> {
                val moveIntent = Intent(this@MainActivity, Favorites::class.java)
                startActivity(moveIntent)
            }

            R.id.daily_reminder_setting -> {
                isReminder = !item.isChecked
                item.isChecked = isReminder
                val repeatMessage = getString(R.string.reminder)
                val repeatTime = "09:00"

                /*
                Set  shared preference
                */
                mUserPreference.setReminder(isReminder)

                /* No repeating alarm */
                if (isReminder) {

                    alarmReceiver.setRepeatingAlarm(
                        this,
                        AlarmReceiver.TYPE_REPEATING,
                        repeatTime,
                        repeatMessage
                    )
                } else {
                    alarmReceiver.cancelAlarm(this)
                }
            }


        }

        return super.onOptionsItemSelected(item)
    }

    private fun prepare(newText: String) {
        progressBar.visibility = View.VISIBLE

        val asyncClient = AsyncHttpClient()
        asyncClient.addHeader("Authorization", "token 2f94bde6f7c7fd89355467bc3eaa7d96f3808360")
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
                    welcome_message.visibility = View.INVISIBLE
                    splash_image.visibility = View.INVISIBLE
                    splash_image_2.alpha = 0.1F
                    splash_image_2.visibility = View.VISIBLE


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

                            val user =
                                UserGithub()
                            user.username = userName
                            user.userid = userId
                            user.avatar = avatar
                            user.url = url
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
                        422 -> getString(R.string.search_user)
                        else -> "$statusCode : ${error.message}"
                    }
                    showSnackbarMessage(errorMessage)
                }

            })
    }

    private fun showRecyclerList() {

        rvUser.layoutManager = LinearLayoutManager(this)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser

    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(main_layout, message, Snackbar.LENGTH_SHORT).show()
    }



}