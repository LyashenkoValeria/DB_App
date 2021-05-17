package com.example.db_app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.ContentIdName
import kotlinx.android.synthetic.main.fragment_authorization.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorisationFragment : Fragment() {

    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button_in.setOnClickListener {
            val username = login_auth.text.toString()
            val pass = password_auth.text.toString()

            if (username.isEmpty() || pass.isEmpty()) {
                (requireActivity() as MainActivity).toast("Введите логин и пароль, а затем повторите попытку.")
//                Toast.makeText(
//                    requireActivity(),
//                    "Введите логин и пароль, а затем повторите попытку.",
//                    Toast.LENGTH_SHORT
//                ).show()
            } else {
                val call = webClient.auth(username, pass)
                call.enqueue(object : Callback<Int?> {
                    override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                        val userId = response.body()
                        if (userId == null) {
                            (requireActivity() as MainActivity).toast("Введён неверный логин или пароль. Повторите попытку.")
//                            Toast.makeText(
//                                requireActivity(),
//                                "Введён неверный логин или пароль. Повторите попытку.",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        } else {
                            (requireActivity() as MainActivity).saveUserId(userId)
                            (requireActivity() as MainActivity).authToContentList()
                        }
                    }

                    override fun onFailure(call: Call<Int?>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })
            }
        }

        auth_to_reg.setOnClickListener {
            (requireActivity() as MainActivity).authToRegistration()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}