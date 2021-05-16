package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import kotlinx.android.synthetic.main.fragment_authorization.*

class AuthorisationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button_in.setOnClickListener {
            val login = login_auth.text.toString()
            val pass = password_auth.text.toString()
            val check = checkUser(login, pass)
            if (check)
                (requireActivity() as MainActivity).toContentList()
            else {
                Toast.makeText(
                    requireContext(),
                    "Введён неверный логин или пароль. Повторите попытку",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        auth_to_reg.setOnClickListener {
            (requireActivity() as MainActivity).authToRegistration()
        }

        // TODO: 13.05.2021 Добавить кнопку перехода к регистрации

        super.onViewCreated(view, savedInstanceState)

    }

    private fun checkUser(login: String, pass: String): Boolean {
        // TODO: 13.05.2021 Если данные подходят - true. Если нет - false


        return true
    }

}