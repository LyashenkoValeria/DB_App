package com.example.db_app.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_layout.view.*


class EditDialogFragment : DialogFragment() {
    var typeOfDialog = 1
    var oldPassword = ""

    private lateinit var listener: EditDialogListener

    enum class Result { LOGIN, MAIL, OK }

    interface EditDialogListener {
        fun applyText(newValue: String, type: Int)
    }

    //typeOfDialog 1 - логин, 2 - почта, 3 - пароль
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(com.example.db_app.R.layout.dialog_fragment_layout, null)

        val title = when (typeOfDialog) {
            1 -> "Логин"
            2 -> "Электронная почта"
            else -> "Пароль"
        }

        if (typeOfDialog != 3){
            view.enter_old.visibility = View.GONE
            view.old_pass_layout.visibility = View.GONE
            view.enter_new.visibility = View.GONE
            view.new_pass_layout.visibility = View.GONE
            view.enter_new_confirm.visibility = View.GONE
            view.new_pass_confirm_layout.visibility = View.GONE
        } else {
            view.edit_line.visibility = View.GONE
        }
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(title)
            .setView(view)

        view.save_button.setOnClickListener {
            when (typeOfDialog) {
                3 -> {
                    val currentPass = view.old_pass.text.toString()
                    val newPass = view.new_pass.text.toString()
                    val confirmPass = view.new_pass_confirm.text.toString()

                    val check = (oldPassword == currentPass) && (newPass == confirmPass)
                    val emptyFields =
                        currentPass.isNotEmpty() && newPass.isNotEmpty() && confirmPass.isNotEmpty()

                    if (check && checkFormat(newPass) && checkFormat(confirmPass) && emptyFields) {
                        listener.applyText(newPass, typeOfDialog)
                        dialog?.dismiss()
                    } else {
                        val message = when {
                            !emptyFields -> "Есть незаполненные поля"
                            oldPassword != currentPass -> "Неверно введён текущий пароль"
                            newPass != confirmPass -> "Пароли не совпадают. Повторите попытку"
                            else -> "Данные введены неверно"
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val editLine = view.edit_line.text.toString()
                    val check = checkEditElement(editLine)

                    if (check == Result.OK && checkFormat(editLine) && editLine.isNotEmpty()) {
                        listener.applyText(editLine, typeOfDialog)
                        dialog?.dismiss()
                    } else {
                        val message =
                            when {
                                editLine.isEmpty() -> "Поле не заполнено"
                                check != Result.OK -> "Пользователь с таким именем уже существует"
                                else -> "Данные введены неверно"
                            }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        view.cancel_button.setOnClickListener { dialog?.cancel() }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as EditDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement EditDialogListener"
            )
        }
    }

    private fun checkEditElement(elem: String): Result {
        // TODO: Делать проверку на то, что username/mail не заняты.
        //  Если type 1 проверяем username.
        //  Если type 2 проверяем mail.
        //  Если всё ок - возвращать OK.
        //  Если не ок - возвращать LOGIN или MAIL в зависимости от того, что требуется.
        return Result.OK
    }

    private fun checkFormat(edit: String): Boolean {
        return when (typeOfDialog) {
            1 -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+".toRegex()) && edit.length < 20
            2 -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+@[a-z]{2,7}\\.(ru|com)".toRegex()) && edit.length < 255
            else -> edit.matches("[a-zA-Z0-9!@#$%&]{6,32}".toRegex())
        }
    }
}