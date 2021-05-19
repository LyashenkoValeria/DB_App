package com.example.db_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.Type
import com.example.db_app.fragments.EditDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_content_list.*
import kotlinx.android.synthetic.main.fragment_profile.*


class MainActivity : AppCompatActivity(), EditDialogFragment.EditDialogListener {

    private lateinit var navController: NavController

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
            // отображение поисковой строки
//            toolbar.menu.findItem(R.id.toolbar_search).isVisible =
//                destination.id == R.id.contentListFragment

//            if (destination.id == R.id.contentListFragment) {
//                (toolbar.menu.findItem(R.id.toolbar_search).actionView as SearchView?)!!.setOnQueryTextListener(
//                    object : SearchView.OnQueryTextListener {
//                        override fun onQueryTextSubmit(query: String): Boolean {
//                            return false
//                        }
//
//                        override fun onQueryTextChange(newText: String): Boolean {
//                            (recycler.adapter as ContentAdapter).filter.filter(newText)
//                            return false
//                        }
//                    })
//            }
        }

//        val searchView = toolbar.menu.findItem(R.id.toolbar_search).actionView as SearchView
//        searchView.queryHint = "Введите название" // TODO: 19.05.2021 Не работатет?
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                (recycler.adapter as ContentAdapter).filter.filter(newText)
//                return false
//            }
//        })


//  Проверка того, что пользователь уже авторизован
        val userId = getUserId()
        if (userId == -1) {
            toAuthorization()
        }
    }

//    fun getSearchView() = toolbar.menu.findItem(R.id.toolbar_search) as SearchView


    fun makeToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun exit(item: MenuItem) {
        val sp = this.getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("userId", -1)
        editor.apply()

        toAuthorization()
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
        navController.navigate(R.id.action_contentList_to_content, bundle)
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

    // Обработчик нажатия кнопки назад
    override fun onBackPressed() {
        val fragmentId = navController.currentBackStackEntry!!.destination.id
        val previousId = navController.previousBackStackEntry?.destination?.id

        when (fragmentId) {
            R.id.contentListFragment ->
                if (previousId == R.id.authorisationFragment)
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

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
////        val searchView = menu!!.getItem(R.id.toolbar_search).actionView as SearchView
////        searchView.queryHint = "Введите название" // TODO: 19.05.2021 Не работатет?
////
////        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
////            override fun onQueryTextSubmit(query: String): Boolean {
////                return false
////            }
////
////            override fun onQueryTextChange(newText: String): Boolean {
////                (recycler.adapter as ContentAdapter).filter.filter(newText)
////                return false
////            }
////        })
//        return true
//    }
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        toolbar.setOnMenuItemClickListener {
//            if (it.itemId == R.id.toolbar_search)
//
//        }
//        searchView = menu.findItem(R.id.toolbar_search) as SearchView
//        searchView.queryHint = "Введите название" // TODO: 19.05.2021 Не работатет?
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                (recycler.adapter as ContentAdapter).filter.filter(newText)
//                return false
//            }
//        })
//    }


}
