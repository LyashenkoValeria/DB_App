package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
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

            if (username.isEmpty() || pass.isEmpty())
                (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_auth_empty))
            else {

                val call = webClient.auth(username, pass)
                call.enqueue(object : Callback<Map<String,Int?>> {
                    override fun onResponse(call: Call<Map<String,Int?>>, response: Response<Map<String,Int?>>) {
                        val userId = response.body()?.get("uid")
                        if (userId == null)
                            (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_auth_data))
                        else {
                            (requireActivity() as MainActivity).saveUserId(userId)
                            (requireActivity() as MainActivity).authToContentList()
                        }
                    }

                    override fun onFailure(call: Call<Map<String,Int?>>, t: Throwable) {
                        // TODO: 19.05.2021 Ошибка при верном логине, но неверном пароле
//                        (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_auth_data))
                        Log.d("db", "Response = $t")
                    }
                })
            }
        }

        auth_to_reg.setOnClickListener {
            (requireActivity() as MainActivity).authToReg()
        }

        super.onViewCreated(view, savedInstanceState)
    }


}