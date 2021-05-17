package com.example.db_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.db_app.dataClasses.Type
import com.example.db_app.fragments.AuthorisationFragment
import com.example.db_app.fragments.ContentListFragment
import com.example.db_app.fragments.EditDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class MainActivity : AppCompatActivity(), EditDialogFragment.EditDialogListener {

    // TODO: 17.05.2021 Авторизация -> на fragmentContentList серый "фильтр"  

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        sidebar.setupWithNavController(navController)
        sidebar.setNavigationItemSelectedListener {
            if (!it.isChecked)
                when (it.itemId) {
                    R.id.catalog_menu -> navController.navigate(R.id.contentListFragment)
                    R.id.tops_menu -> navController.navigate(R.id.topListFragment)
                    R.id.profile_menu -> navController.navigate(R.id.profileFragment)
                    R.id.exit_menu -> exit()
                    // TODO: 17.05.2021  добавть другие фрагменты
                }
            true
        }

//  Проверка того, что пользователь уже авторизован
        // TODO: 17.05.2021 убрать авторизацию и регистрацию из navigationGraph и при необходимости
        //  вызывать эти фрагменты отдельно
        val userId = getUserId()
        if (userId == -1) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(main_nav_host.id, AuthorisationFragment())
//                .add(R.id.fragment_container, fragment2)
//                .show(fragment3)
//                .hide(fragment4)
                .commit()
//            navController.navigate(R.id.authorisationFragment)
            // TODO: 17.05.2021 Удалять из стека предыдущие фрагменты
        }
        else
            toContentList()
    }

    fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun exit() {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("userId", -1)
        editor.apply()

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(main_nav_host.id, AuthorisationFragment())
//                .add(R.id.fragment_container, fragment2)
//                .show(fragment3)
//                .hide(fragment4)
            .disallowAddToBackStack()
            .commit()
//        navController.navigate(R.id.authorisationFragment)
    }

    private fun getUserId(): Int {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        return sp.getInt("userId", -1)
    }

    fun saveUserId(userId: Int) {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("userId", userId)
        editor.apply()
    }

    fun authToRegistration() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        navController.navigate(R.id.action_auth_to_reg)
    }

    fun toContentList() {
        navController.navigate(R.id.contentListFragment)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun authToContentList() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(main_nav_host.id, ContentListFragment())
            .disallowAddToBackStack()
            .commit()

    }

    fun toChooseGenre() {
        navController.navigate(R.id.action_registration_to_chooseGenre)
    }

    fun back() {
        navController.popBackStack()
    }

    fun toContent(type: Type, id: Int) {
        val bundle = Bundle().apply {
            putInt("type", type.t)
            putInt("id", id)
        }
        navController.navigate(R.id.action_contentList_to_content, bundle)
    }

    fun listToProfile() {
        navController.navigate(R.id.action_contentListFragment_to_profileFragment)
    }

    fun listToAuthorization() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        navController.navigate(R.id.action_contentListFragment_to_authorisationFragment)
    }

    fun profileToChooseList() {
        navController.navigate(R.id.action_profileFragment_to_chooseGenreFragment)
    }
    
    fun toTop(type: Type, id: Int) {
        val bundle = Bundle().apply {
            putInt("type", type.t)
            putInt("id", id)
        }
        navController.navigate(R.id.topContentListFragment, bundle)
    }

    override fun applyText(newValue: String, type: Int) {
        when (type) {
            1 -> user_login.text = newValue
            2 -> user_email.text = newValue
            else -> user_password.text = newValue
        }
    }

}
