package com.example.db_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.db_app.adapters.ContentAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: 13.05.2021 Проверка того, что пользватель уже вошёл в аккаунт

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
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

    fun toContent(type: ContentAdapter.Type, id: Int) {
        val bundle = Bundle().apply {
            putInt("type", type.t)
            putInt("id", id)
        }
        navController.navigate(R.id.action_contentList_to_content, bundle)
    }




//    private fun kek (l:Content) = 1

}
