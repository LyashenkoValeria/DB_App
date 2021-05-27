package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.fragments.EditDialogFragment.DialogType
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.User
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {
    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userToken = (requireActivity() as MainActivity).getUserToken()
        val callUserInfo = webClient.getUserInfo(userToken)

        callUserInfo.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                drawLayout(response.body()!!)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })

        edit_login_button.setOnClickListener {
            showDialog(R.id.edit_login_button, userToken)
        }

        edit_email_button.setOnClickListener {
            showDialog(R.id.edit_email_button, userToken)
        }

        edit_password_button.setOnClickListener {
            showDialog(R.id.edit_password_button, userToken)
        }

        edit_like_genres.setOnClickListener {
            (requireActivity() as MainActivity).savePreviousFragment()
            (requireActivity() as MainActivity).profileToChooseList()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun drawLayout(user: User) {
        user_login.setText(user.username)
        user_email.setText(user.email)
        val dateReg = Date(user.createTime.time)
        create_time_info.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(dateReg)
    }

    private fun showDialog(buttonID: Int, userToken: String) {
        val dialog = EditDialogFragment(userToken)
        dialog.typeOfDialog = when (buttonID) {
            R.id.edit_login_button -> DialogType.USERNAME
            R.id.edit_email_button -> DialogType.EMAIL
            else -> DialogType.PASSWORD
        }
        dialog.show(requireActivity().supportFragmentManager, "EditDialogFragment")
    }
}