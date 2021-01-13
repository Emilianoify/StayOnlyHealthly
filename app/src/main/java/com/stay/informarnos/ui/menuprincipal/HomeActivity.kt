package com.stay.informarnos.ui.menuprincipal

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.stay.informarnos.R
import com.stay.informarnos.presentation.auth.login.view.AuthActivity
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar2)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        val deslog = menu?.findItem(R.id.bDeslog)
        deslog?.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            builder.setTitle(" ")
            val view = layoutInflater.inflate(R.layout.dialog_sign_out, null)
            builder.setView(view)
            builder.setPositiveButton("Si", DialogInterface.OnClickListener { _, _ ->
                salir()
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> })
            builder.show()

            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun salir() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }


   override fun onSupportNavigateUp(): Boolean {

        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment),
            drawer_layout
        )
    }

}