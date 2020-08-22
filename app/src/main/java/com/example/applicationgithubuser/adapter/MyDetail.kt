package com.example.applicationgithubuser.adapter

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationgithubuser.fragment.SectionsPagerAdapter
import com.example.applicationgithubuser.R
import com.example.applicationgithubuser.db.DatabaseContract
import com.example.applicationgithubuser.db.DatabaseContract.UserColumnns.Companion.CONTENT_URI
import com.example.applicationgithubuser.db.FavoritesHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.detail.*
import kotlinx.android.synthetic.main.detail.progressBar
import org.json.JSONObject


class MyDetail : AppCompatActivity() {
    private lateinit var favoriteHelper: FavoritesHelper
    private var statusFavorite: Boolean = false
    private lateinit var uriWithId: Uri

    companion object {
        const val EXTRA_USER = "extra_user"
        private lateinit var responseObject:JSONObject
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail)
        var fab = findViewById<FloatingActionButton>(R.id.fab)

        favoriteHelper = FavoritesHelper.getInstance(applicationContext)
        favoriteHelper.open()

        full_name.visibility = View.INVISIBLE
        company.visibility = View.INVISIBLE
        location.visibility = View.INVISIBLE
        total_repositories.visibility = View.INVISIBLE
        textView.visibility = View.INVISIBLE
        followings.visibility = View.INVISIBLE
        followers.visibility = View.INVISIBLE
        textView2.visibility = View.INVISIBLE
        textView3.visibility = View.INVISIBLE


        val user = intent.getParcelableExtra(EXTRA_USER) as UserGithub
        username.text = user.username.toString()
        Picasso.get()
            .load(user.avatar)
            .into(profile_picture)
        var url = user.url.toString()
        prepare(url)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager
            )

        sectionsPagerAdapter.username=user.username.toString()
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        supportActionBar?.elevation = 0f


        uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user?.userid)
        val cursor = contentResolver?.query(uriWithId, null, null, null, null)

        if (cursor != null) {
            statusFavorite = if(cursor.count <= 0){
                cursor.close()
                false
            }else{
                true
            }
        }
        cursor?.close()

        setStatusFavorite(statusFavorite)

        fab.setOnClickListener{
            statusFavorite=!statusFavorite
            if (statusFavorite){
                val values = ContentValues()
                values.put(DatabaseContract.UserColumnns.COLUMN_NAME_USERNAME, user.username)
                values.put(DatabaseContract.UserColumnns.COLUMN_NAME_AVATAR_URL, user.avatar)
                values.put(DatabaseContract.UserColumnns.COLUMN_USER_URL, user.url)
                values.put(DatabaseContract.UserColumnns.COLUMN_NAME_USER_ID, user.userid)
                uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user?.userid)
                contentResolver.insert(uriWithId, values)
                showSnackbarMessage(getString(R.string.favorite_add_success))

            }else{
                uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user?.userid)
                contentResolver.delete(uriWithId, null, null)
                showSnackbarMessage(getString(R.string.favorite_delete_success))

            }

            setStatusFavorite(statusFavorite)


        }

    }

    private fun setStatusFavorite(statusFavorite: Boolean){
        if(statusFavorite){
            fab.setImageResource(R.drawable.baseline_star_white_18dp)

        }

        else{
            fab.setImageResource(R.drawable.baseline_star_border_white_18dp)

        }

    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(cardView, message, Snackbar.LENGTH_SHORT).show()
    }


    private fun prepare(url: String) {
        progressBar.visibility = View.VISIBLE

        val asyncClient = AsyncHttpClient()
        asyncClient.addHeader("Authorization", "token 2f94bde6f7c7fd89355467bc3eaa7d96f3808360")
        asyncClient.addHeader("User-Agent", "request")
        asyncClient.get(
            url,
            object : AsyncHttpResponseHandler() {

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray
                ) {
                    progressBar.visibility = View.INVISIBLE
                    val result = String(responseBody)

                    try {
                        responseObject = JSONObject(result)
                        company.text = responseObject.getString("company")
                        location.text = responseObject.getString("location")
                        total_repositories.text = responseObject.getString("public_repos")
                        full_name.text = responseObject.getString("name")
                        followers.text = responseObject.getString("followers")
                        followings.text = responseObject.getString("following")


                        full_name.visibility = View.VISIBLE
                        company.visibility = View.VISIBLE
                        location.visibility = View.VISIBLE
                        followings.visibility = View.VISIBLE
                        followers.visibility = View.VISIBLE
                        total_repositories.visibility = View.VISIBLE
                        textView.visibility = View.VISIBLE
                        textView2.visibility = View.VISIBLE
                        textView3.visibility = View.VISIBLE

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
                        else -> "$statusCode : ${error.message}"
                    }
                    Toast.makeText(this@MyDetail, errorMessage, Toast.LENGTH_SHORT).show()

                }

            })
    }
}