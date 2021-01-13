package com.stay.informarnos.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.stay.informarnos.R
import kotlinx.android.synthetic.main.fragment_update_password.*


class UpdatePassword : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       showPassLayout()

        bSiguiente.setOnClickListener {

            val password = etContraActual.text.toString().trim()

            if (password.isEmpty()) {
                etContraActual.error = "Ingrese su contraseña"
                etContraActual.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                showUpdatePassLayout()
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                etContraActual.error = "Contraseña Invalida"
                                etContraActual.requestFocus()
                            }

                        }
                    }
            }
        }
            bActualizarContra.setOnClickListener {

                val password = etNuevaContraseña.text.toString().trim()

                if(password.isEmpty() || password.length < 6){
                    etNuevaContraseña.error = "Ingresa al menos 6 caracteres"
                   etNuevaContraseña.requestFocus()
                    return@setOnClickListener
                }

                if(password != etConfirmar.text.toString().trim()){
                    etConfirmar.error = "Las contraseñas no coinciden"
                    etConfirmar.requestFocus()
                    return@setOnClickListener
                }

                currentUser?.let{ user ->
                    progressbar.visibility = View.VISIBLE
                    user.updatePassword(password)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val action = UpdatePasswordDirections.actionUpdatedPass()
                                Navigation.findNavController(it).navigate(action)
                                Toast.makeText(activity, "Se ha actualizado su contraseña", Toast.LENGTH_LONG).show()

                            }else{
                                Toast.makeText(activity, "No se ha podido actualizar su contraseña", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }

    private fun showPassLayout(){
        layoutPassword.visibility = View.VISIBLE
        layoutUpdatePassword.visibility = View.GONE
    }

    private fun showUpdatePassLayout(){
        layoutPassword.visibility = View.GONE
        layoutUpdatePassword.visibility = View.VISIBLE
    }

    }