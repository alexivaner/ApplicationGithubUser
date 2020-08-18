package com.example.applicationgithubuser.PagerAdapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationgithubuser.Adapter.MyAdapter
import com.example.applicationgithubuser.R
import com.example.applicationgithubuser.Adapter.UserGithub
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.fragment_followers.progressBar
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Followers.newInstance] factory method to
 * create an instance of this fragment.
 */
class Followers : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var rvUser: RecyclerView

    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var avatar: String
    private lateinit var url: String

    private var users = arrayListOf<UserGithub>()
    lateinit var listGithubUser: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvUser = followers_layout.findViewById(R.id.followers_layout)
        rvUser.setHasFixedSize(true)

        val username= arguments?.getString(ARG_USERNAME)
        if (username != null) {
            prepare(username)
        }


    }

    private fun prepare(newText: String) {
        progressBar.visibility = View.VISIBLE

        val asyncClient = AsyncHttpClient()
        asyncClient.addHeader("Authorization", "token 2f94bde6f7c7fd89355467bc3eaa7d96f3808360")
        asyncClient.addHeader("User-Agent", "request")
        asyncClient.get(
            "https://api.github.com/users/$newText/followers",
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
                        val responseObject = JSONArray(result)
                        Log.d("COBA", responseObject.toString())

                        for (i in 0 until responseObject.length()) {
                            val item = responseObject.getJSONObject(i)
                            userName = item.getString("login")
                            userId = item.getString("id")
                            avatar = item.getString("avatar_url")
                            url = item.getString("url")

                            val user =
                                UserGithub()
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
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()

                }

            })
    }
    private fun showRecyclerList() {

        rvUser.layoutManager = LinearLayoutManager(activity)
        listGithubUser =
            MyAdapter(users)
        rvUser.adapter = listGithubUser

    }


    companion object {
        private val ARG_USERNAME="username"
        private lateinit var responseObject:JSONObject

        fun newInstance(username: String?): Followers {
            val fragment=
                Followers()
            val bundle=Bundle()
            bundle.putString(ARG_USERNAME,username)
            fragment.arguments=bundle
            return fragment
        }
    }
}