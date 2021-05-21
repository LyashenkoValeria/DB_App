package com.example.db_app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.db_app.dataClasses.Type
import com.example.db_app.fragments.EditDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*


class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    var typeContentList: Type? = null
//    var posContentList: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController

// Настройка toolBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

// Настройка sidebar
        sidebar.setupWithNavController(navController)

// Настройка различного отображения toolbar и sidebar в разных фрагментах
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // скрытие toolbar и sidebar
            when (destination.id) {
                R.id.authorisationFragment, R.id.registrationFragment -> {
                    toolbar?.visibility = View.GONE
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    toolbar?.visibility = View.VISIBLE
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
            // названия фрагментов
            toolbar.title = when (destination.id) {
                R.id.contentListFragment -> resources.getString(R.string.catalog_menu)
                R.id.profileFragment -> resources.getString(R.string.prof_str)
                R.id.topListFragment -> resources.getString(R.string.tops_menu)
                // TODO: 19.05.2021 Добавить названия для остальных фрагментов
                else -> ""
            }
        }


//  Проверка того, что пользователь уже авторизован
        val userId = getUserToken()
        if (userId == null || userId == "")
            toAuthorization()
//        else
//            setToolbarTitle(resources.getString(R.string.catalog_menu))
    }

    fun setToolbarListener(listener: View.OnClickListener) {
        toolbar.setOnClickListener(listener)
    }

    fun setToolbarTitle(title: String) {
        toolbar.title = title
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitle)
    }

    fun saveContentListType(type: Type) {
        typeContentList = type
    }




    fun makeToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun exit(item: MenuItem) {
        exit()
    }

    fun exit() {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("userId", -1)
        editor.apply()

        toAuthorization()
    }

    fun getUserToken(): String? {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        return sp.getString("userToken", "")
    }

    fun saveUserToken(userToken: String) {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("userToken", userToken)
        editor.apply()
    }

    private fun toAuthorization() {
        navController.navigate(R.id.authorisationFragment)
        collapsing_toolbar_layout.visibility = View.GONE
    }

    fun authToReg() {
        navController.navigate(R.id.action_auth_to_reg)
    }

    fun authToContentList() {
        navController.navigate(R.id.contentListFragment)
        collapsing_toolbar_layout.visibility = View.VISIBLE
    }

    fun toContentList() {
        navController.navigate(R.id.contentListFragment)
    }

    fun toChooseGenre() {
        navController.navigate(R.id.chooseGenreFragment)
    }

    fun back() {
        navController.popBackStack()
    }

    fun toContent(type: Type, id: Int) {
        val bundle = Bundle().apply {
            putInt("type", type.t)
            putInt("id", id)
        }
//        navController.navigate(R.id.action_contentList_to_content, bundle)
        navController.navigate(R.id.contentFragment, bundle)
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

    // Обработчик нажатия кнопки назад
    override fun onBackPressed() {
        val fragmentId = navController.currentBackStackEntry!!.destination.id
        val previousId = navController.previousBackStackEntry?.destination?.id

        when (fragmentId) {
            R.id.contentListFragment ->
                if (previousId == R.id.authorisationFragment || previousId == R.id.registrationFragment)
                    finish()
                else
                    super.onBackPressed()

            R.id.authorisationFragment ->
                if (previousId == R.id.contentListFragment)
                    finish()
                else
                    super.onBackPressed()

            else -> super.onBackPressed()
        }
    }

    fun onSubmitClicked(typeOfDialog: EditDialogFragment.DialogType, newValue: String) {
        // TODO: 21.05.2021 Для повторной скрытой авторизации надо знать пароль
        if (typeOfDialog == EditDialogFragment.DialogType.USERNAME)
            exit()
        if (typeOfDialog == EditDialogFragment.DialogType.EMAIL)
            user_email.setText(newValue)
    }

}
