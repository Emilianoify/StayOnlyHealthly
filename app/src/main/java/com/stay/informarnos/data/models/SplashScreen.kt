package com.stay.informarnos.data.models

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stay.informarnos.R
import com.stay.informarnos.presentation.auth.login.view.AuthActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splash.alpha = 0f
        splash.animate().setDuration(2000).alpha(1f).withEndAction{
            val i = Intent(this, AuthActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}