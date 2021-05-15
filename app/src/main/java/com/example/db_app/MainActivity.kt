package com.example.db_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.fragments.EditDialogFragment
import kotlinx.android.synthetic.main.fragment_profile.*

class MainActivity : AppCompatActivity(), EditDialogFragment.EditDialogListener {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: 13.05.2021 Проверка того, что пользватель уже вошёл в аккаунт

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController

    }

    fun authToRegistration() {
        navController.navigate(R.id.action_auth_to_reg)
    }

    fun toContentList() {
        navController.navigate(R.id.action_to_contentListFragment)
    }

    fun toChooseGenre() {
        navController.navigate(R.id.action_registration_to_chooseGenre)
    }

    fun back() {
        navController.popBackStack()
    }

    fun listToProfile() {
        navController.navigate(R.id.action_contentListFragment_to_profileFragment)
    }

    fun listToAuthorization() {
        navController.navigate(R.id.action_contentListFragment_to_authorisationFragment)
    }

    fun profileToChooseList() {
        navController.navigate(R.id.action_profileFragment_to_chooseGenreFragment)
    }


    fun toContent(type: ContentAdapter.Type, id: Int) {
        val bundle = Bundle().apply {
            putInt("type", type.t)
            putInt("id", id)
        }
        navController.navigate(R.id.action_contentList_to_content, bundle)
    }

    override fun applyText(newValue: String, type: Int) {
        when(type){
            1 -> user_login.text = newValue
            2 -> user_email.text = newValue
            else -> user_password.text = newValue
        }
    }


//    private fun kek (l:Content) = 1

}
