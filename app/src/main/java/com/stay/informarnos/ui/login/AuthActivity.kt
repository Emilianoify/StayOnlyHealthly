package com.stay.informarnos.presentation.auth.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.stay.informarnos.R
import com.stay.informarnos.network.NetworkCheck
import com.stay.informarnos.ui.menuprincipal.HomeActivity
import kotlinx.android.synthetic.main.activity_home.*

class AuthActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        val navController = Navigation.findNavController(this, R.id.authfragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    override fun onStart() {
        super.onStart()
        if (NetworkCheck().isOnline()){
            val user = auth.currentUser
            if (user != null && user.isEmailVerified){
                val goHome = Intent(this, HomeActivity::class.java)
                startActivity(goHome)
                finish()
            }else return
        }else{
            FirebaseAuth.getInstance().signOut()
            val navController = Navigation.findNavController(this, R.id.authfragment)
            NavigationUI.setupActionBarWithNavController(this, navController)
            Toast.makeText(this, "Verifica tu conexión a internet", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {

        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.authfragment),
            drawer_layout
        )
    }

}