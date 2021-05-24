package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.fragments.EditDialogFragment.DialogType
import com.example.db_app.R
import com.example.db_app.WebClient
import kotlinx.android.synthetic.main.fragment_registration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : Fragment() {
    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button_reg.setOnClickListener {
            val username = login_reg.text.toString()
            val pass = password_reg.text.toString()
            val email = email_reg.text.toString()
            
            if (username.isEmpty() || pass.isEmpty() || email.isEmpty())
                (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_edit_empty))
            else
                if (!checkFormat(username, DialogType.USERNAME) || !checkFormat(pass, DialogType.PASSWORD) || !checkFormat(email, DialogType.EMAIL))
                    (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_edit_incorrect))

            else {
                    val callReg = webClient.reg(username, pass, email)
                    callReg.enqueue(object : Callback<Map<String, Int>> {
                        override fun onResponse(
                            call: Call<Map<String, Int>>,
                            response: Response<Map<String, Int>>
                        ) {
                            val code = response.body()?.get("code")
                            val msg = when (code) {
                                0 -> ""
                                1 -> resources.getString(R.string.err_username)
                                2 -> resources.getString(R.string.err_username_length)
                                3 -> resources.getString(R.string.err_email)
                                4 -> resources.getString(R.string.err_pass_length_max)
                                5 -> resources.getString(R.string.err_pass_length_min)
                                else -> resources.getString(R.string.err_unknown)
                            }

                            if (code == 0)
                                auth(username, pass)
                            else
                                (requireActivity() as MainActivity).makeToast(msg)
                        }

                        override fun onFailure(call: Call<Map<String, Int>>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }
        }

        reg_to_auth.setOnClickListener {
            (requireActivity() as MainActivity).back()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun auth(username: String, pass: String) {
        val callAuth = webClient.auth(username, pass)
        callAuth.enqueue(object : Callback<Map<String, String?>> {
            override fun onResponse(call: Call<Map<String, String?>>, response: Response<Map<String, String?>>) {
                val userToken = response.body()?.get("token")
                if (userToken == null)
                    (requireActivity() as MainActivity).back() // переход к авторизации
                else {
                    (requireActivity() as MainActivity).saveUserToken("Bearer $userToken")
                    (requireActivity() as MainActivity).savePreviousFragment()
                    (requireActivity() as MainActivity).toChooseGenre()
                }
            }

            override fun onFailure(call: Call<Map<String, String?>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun checkFormat(edit: String, typeOfDialog: DialogType): Boolean {
        return when (typeOfDialog) {
            DialogType.USERNAME -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+".toRegex())
            DialogType.EMAIL -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+@[a-z]{2,7}\\.(ru|com)".toRegex()) && edit.length < 255
            DialogType.PASSWORD -> edit.matches("[a-zA-Z0-9!@#$%&]+".toRegex())
        }
    }
}