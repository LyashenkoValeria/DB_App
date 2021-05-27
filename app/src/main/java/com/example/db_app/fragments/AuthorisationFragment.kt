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
                call.enqueue(object : Callback<Map<String, String?>> {
                    override fun onResponse(call: Call<Map<String, String?>>, response: Response<Map<String, String?>>) {
                        val userToken = response.body()?.get("token")
                        val userPermission = response.body()?.get("role")
                        if (userToken == null)
                            (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_auth_data))
                        else {
                            (requireActivity() as MainActivity).saveUserInfo("Bearer $userToken", userPermission?.toInt() ?: 1)
                            (requireActivity() as MainActivity).authToContentList()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String?>>, t: Throwable) {
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