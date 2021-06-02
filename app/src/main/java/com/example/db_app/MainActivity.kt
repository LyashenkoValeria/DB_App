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
import com.example.db_app.dataClasses.*
import com.example.db_app.fragments.EditDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    var typeContentList: Type? = null
    var typeRecommendList: Type? = null
    var typeViewedList: Type? = null
    var prevFragment: Int = -1
    var needInit = true
    var needInitViewed = true
    var needInitReccom = true
    var needInitTops = true
    var needInitTopContent = true
    var needUpdateFilter = false

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

//                    if (destination.id != R.id.topContentListFragment)
//                        needInitTops = true
//
//                    if (destination.id != R.id.contentFragment) {
//                        needInitTopContent = true
//                        needInitReccom = true
//                        needInitViewed = true
//                        needInitTops = true
//                    }
//
//                    if (destination.label != "FilterFragment" && destination.label != "ContentFragment")
//                        needInit = true
                }
            }
            // названия фрагментов и формирование неоходимости обновления фрагментов
            toolbar.title = when (destination.id) {
                R.id.contentListFragment -> {
                    needInitReccom = true
                    needInitViewed = true
                    needInitTops = true
                    needInitTopContent = true
                    resources.getString(R.string.catalog_menu)
                }
                R.id.profileFragment -> {
                    needInitReccom = true
                    needInitViewed = true
                    needInitTops = true
                    needInitTopContent = true
                    needInit = true
                    resources.getString(R.string.prof_str)
                }
                R.id.topListFragment -> {
                    needInitReccom = true
                    needInitViewed = true
                    needInit = true
                    resources.getString(R.string.tops_menu)
                }
                R.id.chooseGenreFragment -> {
                    needInitReccom = true
                    needInitViewed = true
                    needInitTops = true
                    needInitTopContent = true
                    needInit = true
                    "Жанры книг"
                }
                R.id.superUserFragment -> {
                    needInitReccom = true
                    needInitViewed = true
                    needInitTops = true
                    needInitTopContent = true
                    needInit = true
                    "Изменение БД"
                }
                R.id.viewedFragment -> {
                    needInitReccom = true
                    needInitTops = true
                    needInitTopContent = true
                    needInit = true
                    resources.getString(R.string.viewed_menu)
                }
                R.id.recommendationFragment -> {
                    needInitViewed = true
                    needInitTops = true
                    needInitTopContent = true
                    needInit = true
                    resources.getString(R.string.recommend_menu)
                }
                R.id.filterFragment -> {
                    needInitReccom = true
                    needInitViewed = true
                    needInitTops = true
                    needInitTopContent = true
                    "Фильтр"
                }
                else -> ""
            }
        }


//  Проверка того, что пользователь уже авторизован
        val userToken = getUserToken()
        if (userToken == "") {
            toAuthorization()
        }

// Установка видимости на элемент бокового меню для модератора
        setSuperUserMenu()
    }

    fun savePreviousFragment() {
        prevFragment = navController.currentDestination!!.id
    }

    fun setToolbarListener(listener: View.OnClickListener) {
        toolbar.setOnClickListener(listener)
    }

    fun setToolbarTitle(title: String) {
        toolbar.title = title
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitle)
    }

    private fun setSuperUserMenu() {
        val userPermission = getUserPermission()
        sidebar.menu.findItem(R.id.superUserFragment).isVisible = userPermission == 2
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

    private fun exit() {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("userToken", "")
        editor.putInt("userPermission", -1)
        editor.apply()

        toAuthorization()
    }

    fun getUserToken(): String {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        return sp.getString("userToken", "") ?: ""
    }

    fun getUserPermission(): Int {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        return sp.getInt("userPermission", 1)
    }

    fun saveUserInfo(userToken: String, userPermission: Int) {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("userToken", userToken)
        editor.putInt("userPermission", userPermission)
        editor.apply()
        setSuperUserMenu()
    }


    private fun toAuthorization() {
        navController.navigate(R.id.authorisationFragment)
    }

    fun authToReg() {
        navController.navigate(R.id.action_auth_to_reg)
    }

    fun authToContentList() {
        navController.navigate(R.id.contentListFragment)
    }

    fun toContentList() {
        needInit = true
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
            putString("type", type.t)
            putInt("id", id)
        }
        navController.navigate(R.id.contentFragment, bundle)
    }

    fun profileToChooseList() {
        navController.navigate(R.id.action_profileFragment_to_chooseGenreFragment)
    }

    fun toTop(type: Type, id: Int, topName: String, topAuthor: String) {
        val bundle = Bundle().apply {
            putString("type", type.t)
            putInt("id", id)
            putString("name", topName)
            putString("author", topAuthor)
        }
        navController.navigate(R.id.topContentListFragment, bundle)
    }

    fun toFilter(type: Type, needRestore: Boolean) {
        val bundle = Bundle().apply {
            putString("type", type.t)
            putBoolean("restore", needRestore)
        }
        navController.navigate(R.id.action_contentListFragment_to_filterFragment, bundle)
    }

    fun fromFilter(
        type: Type,
        dir: Boolean,
        filterChanges: Boolean
    ) {
        val bundle = Bundle().apply {
            putString("typeFromFilter", type.t)
            putBoolean("fromFilter", dir)
            putBoolean("notChanged", filterChanges)
        }
        needInit = false
        needUpdateFilter = true
        navController.navigate(R.id.action_filterFragment_to_contentListFragment, bundle)
//        navController.navigate(R.id.contentListFragment, bundle)
    }

    fun toAddContent(type: Type) {
        val bundle = Bundle().apply {
            putString("type", type.t)
        }
        navController.navigate(R.id.superUserAddFragment, bundle)
    }

    fun toAddTop(type: Type) {
        val bundle = Bundle().apply {
            putString("type", type.t)
        }
        navController.navigate(R.id.superUserAddTopFragment, bundle)
    }


    // Обработчик нажатия кнопки назад
    override fun onBackPressed() {
        val current = navController.currentBackStackEntry!!.destination
        val previous = navController.previousBackStackEntry?.destination

        when (current.id) {
            R.id.contentListFragment ->
                if (previous?.id == R.id.authorisationFragment || previous?.id == R.id.registrationFragment || previous?.id == R.id.chooseGenreFragment || previous?.id == R.id.filterFragment)
                    finish()
                else
                    super.onBackPressed()

            R.id.contentFragment -> {
                needInit = previous?.id != R.id.contentListFragment ||  previous.label != "ContentListFragment"
                needInitViewed = previous?.id != R.id.viewedFragment ||  previous.label != "ViewedFragment"
                needInitReccom = previous?.id != R.id.recommendationFragment ||  previous.label != "RecommendationFragment"
                needInitTops = true
                needInitTopContent = previous?.id != R.id.topContentListFragment ||  previous.label != "TopContentListFragment"
                super.onBackPressed()
                }

            R.id.filterFragment -> {
                needInit = false
                needInitViewed = true
                needInitReccom = true
                needInitTops = true
                needInitTopContent = true
                super.onBackPressed()
            }

            R.id.authorisationFragment -> {
                needInit = true
                needInitViewed = true
                needInitReccom = true
                needInitTops = true
                needInitTopContent = true
                if (previous?.id == R.id.contentListFragment)
                    finish()
                else
                    super.onBackPressed()
            }

            R.id.topContentListFragment -> {
                needInit = true
                needInitViewed = true
                needInitReccom = true
                needInitTops = false
                needInitTopContent = true
                super.onBackPressed()
            }

            R.id.chooseGenreFragment -> {
                needInit = true
                needInitViewed = true
                needInitReccom = true
                needInitTops = true
                needInitTopContent = true
                if (previous?.id == R.id.registrationFragment)
                    toContentList()
                else
                    super.onBackPressed()
            }

            else -> {
                when (current.label) {
                    "ContentListFragment" -> {
                        needInit = true
                        needInitViewed = true
                        needInitReccom = true
                        needInitTops = true
                        needInitTopContent = true
                        if (previous?.id == R.id.authorisationFragment || previous?.id == R.id.registrationFragment || previous?.id == R.id.chooseGenreFragment ||
                            previous?.label == "FilterContent"
                        )
                            finish()
                        else
                            super.onBackPressed()
                    }

                    "ContentFragment" -> {
                        needInit = previous?.id != R.id.contentListFragment ||  previous.label != "ContentListFragment"
                        needInitViewed = previous?.id != R.id.viewedFragment ||  previous.label != "ViewedFragment"
                        needInitReccom = previous?.id != R.id.recommendationFragment ||  previous.label != "RecommendationFragment"
                        needInitTops = true
                        needInitTopContent = previous?.id != R.id.topContentListFragment ||  previous.label != "TopContentListFragment"
                        super.onBackPressed()
                    }
                    else -> {
                        needInit = true
                        needInitViewed = true
                        needInitReccom = true
                        needInitTops = true
                        needInitTopContent = true
                        super.onBackPressed()

                    }

                }
            }
        }
    }

    fun onSubmitClicked(typeOfDialog: EditDialogFragment.DialogType, newValue: String) {
        if (typeOfDialog == EditDialogFragment.DialogType.USERNAME)
            exit()
        if (typeOfDialog == EditDialogFragment.DialogType.EMAIL)
            user_email.setText(newValue)
    }

}
