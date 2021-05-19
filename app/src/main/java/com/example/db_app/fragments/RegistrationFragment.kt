package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : Fragment() {

    enum class Result { LOGIN, EMAIL, OK }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button_reg.setOnClickListener {
            val log = login_reg.text.toString()
            val pass = password_reg.text.toString()
            val mail = email_reg.text.toString()
            val check = registration(log, pass, mail)

            if (check == Result.OK)
                (requireActivity() as MainActivity).toChooseGenre()

            else {
                val message = if (check == Result.LOGIN)
                    resources.getString(R.string.err_reg_username)
                else
                    resources.getString(R.string.err_reg_email)

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        reg_to_auth.setOnClickListener {
            (requireActivity() as MainActivity).back()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun registration(login: String, pass: String, mail: String): Result {
        // TODO: 13.05.2021 Делать проверку на то, что username и mail не заняты.
        //  Если всё ок - регистрировать и возвращать OK.
        //  Если не ок - возвращать LOGIN или MAIL в зависимости от того, что занято.
        return Result.OK
    }
}