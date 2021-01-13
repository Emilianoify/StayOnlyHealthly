package com.stay.informarnos.presentation.auth.login.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stay.informarnos.R
import com.stay.informarnos.network.NetworkCheck
import com.stay.informarnos.ui.menuprincipal.HomeActivity
import kotlinx.android.synthetic.main.fragment_auth.*


class Auth : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        bLogear.setOnClickListener {
            doLogin()
        }

        btn_reset_password.setOnClickListener {
            val builder = AlertDialog.Builder(this.requireActivity(), R.style.MyDialogTheme)
            builder.setTitle("Recuperar contraseña")

            val view2 = layoutInflater.inflate(R.layout.dialog_forgot_pass, null)
            builder.setView(view2)
            val userName = view2.findViewById<EditText>(R.id.et_useremail)
            builder.setPositiveButton("Recuperar") { _, _ ->
                forgotPassword(userName)
            }
            builder.setNegativeButton("Cerrar") { _, _ -> }
            builder.show()
        }

        bRegistrarse.setOnClickListener {
            val action = AuthDirections.actionGoRegister()
            Navigation.findNavController(it).navigate(action)
        }

    }
    private fun doLogin() {

        val email = etEmail.text.toString()
        val password = etContraseña.text.toString()
        if (email.isEmpty()) {
            etEmail.error = "Revisa la dirección de correo electronico."
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Revisa la dirección de correo electronico."
            etEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etContraseña.error = "Verifica tu contraseña."
            etContraseña.requestFocus()
            return
        }

        if (NetworkCheck().isOnline()) {
            progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(activity, "Email y/o contraseña invalidos", Toast.LENGTH_LONG).show()
                }
            }
    }else{
            Toast.makeText(activity, "Verifica tu conexión a internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(currentUser: FirebaseUser?){

        if (currentUser != null){
            if (currentUser.isEmailVerified){
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }else{
                Toast.makeText(activity,"Email no verificado. Por favor verifica tu email", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun forgotPassword(usermail: EditText) {
        if (usermail.text.toString().isEmpty()) {
            Toast.makeText(activity, "Escriba una dirección de email valida.", Toast.LENGTH_LONG).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(usermail.text.toString()).matches()) {
            Toast.makeText(activity, "La dirección de email no es valida o no existe.", Toast.LENGTH_LONG).show()

            return
        }
        auth.sendPasswordResetEmail(usermail.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Las instrucciones fueron enviadas a su email.", Toast.LENGTH_LONG).show()
                }
            }
    }
}