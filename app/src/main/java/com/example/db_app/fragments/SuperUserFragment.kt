/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_super_user.*

class SuperUserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_super_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userPermission = (requireActivity() as MainActivity).getUserPermission()
        // Проверка на тот случай, если вдруг почему-то отобразилась эта влкадка у обычного пользователя
        if (userPermission != 2) {
            (requireActivity() as MainActivity).makeToast("Извините, у вас недостаточно прав.")
            (requireActivity() as MainActivity).back()
        }

        super_add_book.setOnClickListener {
            (requireActivity() as MainActivity).toAddContent(Type.BOOK)
        }

        super_add_film.setOnClickListener {
            (requireActivity() as MainActivity).toAddContent(Type.FILM)
        }

        super_add_music.setOnClickListener {
            (requireActivity() as MainActivity).toAddContent(Type.MUSIC)
        }

        super_add_top_book.setOnClickListener {
            (requireActivity() as MainActivity).toAddTop(Type.BOOK)
        }

        super_add_top_film.setOnClickListener {
            (requireActivity() as MainActivity).toAddTop(Type.FILM)
        }

        super_add_top_music.setOnClickListener {
            (requireActivity() as MainActivity).toAddTop(Type.MUSIC)
        }

        super.onViewCreated(view, savedInstanceState)
    }
}