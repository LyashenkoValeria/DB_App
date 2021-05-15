package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment: Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        edit_login_button.setOnClickListener {
            showDialog(R.id.edit_login_button)
        }

        edit_email_button.setOnClickListener {
            showDialog(R.id.edit_email_button)
        }

        edit_password_button.setOnClickListener {
            showDialog(R.id.edit_password_button)
        }

        edit_prof.setOnClickListener{
            (requireActivity() as MainActivity).profileToChooseList()
        }

        super.onViewCreated(view, savedInstanceState)

    }

    private fun showDialog(buttonID: Int) {
        val dialog = EditDialogFragment()
        dialog.typeOfDialog = when (buttonID) {
            R.id.edit_login_button -> 1
            R.id.edit_email_button -> 2
            else -> 3
        }
        dialog.oldPassword = user_password.text.toString()
        dialog.show(requireActivity().supportFragmentManager, "EditDialogFragment")
    }

}