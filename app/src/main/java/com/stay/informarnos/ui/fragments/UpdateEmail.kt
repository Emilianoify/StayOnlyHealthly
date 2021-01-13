package com.stay.informarnos.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.stay.informarnos.R
import kotlinx.android.synthetic.main.fragment_update_email.*


class UpdateEmail : Fragment() {

    private val currenterUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showPasswordLayout()

        bAutenticarse.setOnClickListener {
            val password = etIngresarContraseña.text.toString()

            if (password.isEmpty()){
                etIngresarContraseña.error = "Ingrese su contraseña"
                etIngresarContraseña.requestFocus()
                return@setOnClickListener
            }

            currenterUser?.let { user->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential).addOnCompleteListener { task->
                    progressbar.visibility = View.GONE
                    when {
                        task.isSuccessful -> {
                            showEmailLayout()
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            etIngresarContraseña.error = "Contraseña invalida"
                            etIngresarContraseña.requestFocus()
                        }
                        else -> {

                        }
                    }

                }

            }
        }

        bCambiarEmail.setOnClickListener { view ->
            val email = etNuevoEmail.text.toString().trim()
            if (email.isEmpty()) {
                etNuevoEmail.error = "Revisa la dirección de correo electronico."
                etNuevoEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etNuevoEmail.error = "Revisa la dirección de correo electronico."
                etNuevoEmail.requestFocus()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            currenterUser?.let { user->
                user.updateEmail(email)
                    .addOnCompleteListener { task->
                        progressbar.visibility = View.GONE
                        if (task.isSuccessful) {
                            saveNewEmail(email)
                            val action = UpdateEmailDirections.actionReturnProfile()
                            Navigation.findNavController(view).navigate(action)
                        }else{
                            Toast.makeText(activity, "No se ha podido cambiar el email", Toast.LENGTH_LONG).show()
                        }
                    }

            }
        }

    }

    private fun showPasswordLayout(){
        layoutPassword.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.GONE
    }

    private fun showEmailLayout(){
        layoutPassword.visibility = View.GONE
        layoutUpdateEmail.visibility = View.VISIBLE
    }

    private fun saveNewEmail(email: String){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val checkUser = auth.currentUser!!.uid
        reference.child(checkUser)
            .child("Datos Personales")
            .child("email")
            .setValue(email)
            .addOnSuccessListener {
                Toast.makeText(activity, "Los cambios se han realizado con exito", Toast.LENGTH_LONG).show()
            }
    }
}