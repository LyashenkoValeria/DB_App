package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import kotlinx.android.synthetic.main.dialog_fragment_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditDialogFragment(private val userToken: String) : DialogFragment() {
    var typeOfDialog = DialogType.USERNAME
    private lateinit var viewRoot: View
    private val webClient = WebClient().getApi()

    enum class DialogType { USERNAME, EMAIL, PASSWORD }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.dialog_fragment_layout, container, false)

        view.run {
            val enterText: String
            dialog_title.text = when (typeOfDialog) {
                DialogType.USERNAME -> {
                    enterText = resources.getString(R.string.new_login)
                    resources.getString(R.string.change_login)
                }
                DialogType.EMAIL -> {
                    enterText = resources.getString(R.string.new_email)
                    resources.getString(R.string.change_email)
                }
                DialogType.PASSWORD -> {
                    enterText = resources.getString(R.string.current_pass)
                    resources.getString(R.string.change_pass)
                }
            }
            view.enter_old.text = enterText

            if (typeOfDialog != DialogType.PASSWORD) {
                view.old_pass_layout.visibility = View.GONE
                view.enter_new.visibility = View.GONE
                view.new_pass_layout.visibility = View.GONE
                view.enter_new_confirm.visibility = View.GONE
                view.new_pass_confirm_layout.visibility = View.GONE
            } else {
                view.edit_line.visibility = View.GONE
            }

            view.save_button.setOnClickListener {
                val newUsernameOrEmail = viewRoot.edit_line.text.toString()
                val oldPass = viewRoot.old_pass.text.toString()
                val newPass = viewRoot.new_pass.text.toString()
                val newPassConfirm = viewRoot.new_pass_confirm.text.toString()
                var msg: String? = null

                // Проверка для username и email
                if (typeOfDialog == DialogType.USERNAME || typeOfDialog == DialogType.EMAIL) {
                    // проверка на пустоту
                    if (newUsernameOrEmail.isEmpty())
                        msg = resources.getString(R.string.err_edit_empty)
                    else
                    // проверка на неверный формат
                        if (!checkFormat(newUsernameOrEmail))
                            msg = resources.getString(R.string.err_edit_incorrect)
                }
                // Проверка для пароля
                else {
                    // проверка на пустоту
                    if (oldPass.isEmpty() || newPass.isEmpty() || newPassConfirm.isEmpty())
                        msg = resources.getString(R.string.err_edit_empty)
                    else
                    // проверка на неверный формат
                        if (!checkFormat(oldPass) || !checkFormat(newPass) || !checkFormat(
                                newPassConfirm
                            )
                        )
                            msg = resources.getString(R.string.err_edit_incorrect)
                        else
                        // проверка на несовпадение
                            if (newPass != newPassConfirm)
                                msg = resources.getString(R.string.err_edit_confirm_incorrect)
                }

                // Если есть сообщение об ошибке, выводим его и не идём дальше
                if (msg != null) {
                    (requireActivity() as MainActivity).makeToast(msg)
                    return@setOnClickListener
                }

                val callUpdate = when (typeOfDialog) {
                    DialogType.USERNAME -> webClient.updateUsername(newUsernameOrEmail, userToken)
                    DialogType.EMAIL -> webClient.updateEmail(newUsernameOrEmail, userToken)
                    DialogType.PASSWORD -> webClient.updatePass(oldPass, newPass, userToken)
                }


                callUpdate.enqueue(object : Callback<Map<String, Int>> {
                    override fun onResponse(
                        call: Call<Map<String, Int>>,
                        response: Response<Map<String, Int>>
                    ) {
                        val code = response.body()!!["code"]
                        val message = when (code) {
                            0 -> {
                                when (typeOfDialog) {
                                    DialogType.USERNAME -> resources.getString(R.string.change_login_complete)
                                    DialogType.EMAIL -> resources.getString(R.string.change_email_complete)
                                    DialogType.PASSWORD -> resources.getString(R.string.change_pass_complete)
                                }
                            }
                            1 -> resources.getString(R.string.err_username)
                            2 -> resources.getString(R.string.err_username_length)
                            3 -> resources.getString(R.string.err_email)
                            4 -> resources.getString(R.string.err_pass_length_max)
                            5 -> resources.getString(R.string.err_pass_length_min)
                            6 -> resources.getString(R.string.err_curr_pass_incorrect)
                            else -> resources.getString(R.string.err_unknown)
                        }
                        (requireActivity() as MainActivity).makeToast(message)
                        // если всё ок, завершаем диалог
                        if (code == 0) {
                            if (typeOfDialog == DialogType.USERNAME || typeOfDialog == DialogType.EMAIL)
                                (requireActivity() as MainActivity).onSubmitClicked(typeOfDialog, newUsernameOrEmail)
                            dismiss()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Int>>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })
            }

            view.cancel_button.setOnClickListener {
                dismiss()
            }
        }
        viewRoot = view
        return view
    }


    private fun checkFormat(edit: String): Boolean {
        return when (typeOfDialog) {
            DialogType.USERNAME -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+".toRegex()) && edit.length < 20
            DialogType.EMAIL -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+@[a-z]{2,7}\\.(ru|com)".toRegex()) && edit.length < 255
            DialogType.PASSWORD -> edit.matches("[a-zA-Z0-9!@#$%&]{6,32}".toRegex())
        }
    }

}