package com.stay.informarnos.ui.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.stay.informarnos.R
import com.stay.informarnos.data.models.User
import com.stay.informarnos.network.NetworkCheck
import com.stay.informarnos.presentation.auth.login.view.AuthActivity
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.cvFotoDelRegistro
import java.util.*

class Register : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        bRegistro.setOnClickListener {
            performRegister()

        }

        cvFotoDelRegistro.setOnClickListener {
            takePictureIntent()
        }
    }

    var selectedPhotoUri: Uri? = null

    private fun takePictureIntent(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    private fun saveUserInDatabase(){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val rootNode = FirebaseDatabase.getInstance()
        val referencia = rootNode.getReference("users")
        val nombre = etNombre.text.toString()
        val email = etMail.text.toString()
        val username = etUsername.text.toString()
        val profileImage:String = ""

        val user = User(uid, profileImage, nombre, email, username)
        referencia.child(uid)
            .child("Datos Personales")
            .setValue(user)
    }

    private fun performRegister() {

        val nombre = etNombre.text.toString()
        val email = etMail.text.toString()
        val contrasena = etContra.text.toString()
        val username = etUsername.text.toString()


        if (username.isEmpty()) {
            etUsername.error = "Agrega tu nombre de usuario."
            etUsername.requestFocus()
            return
        }

        if (nombre.isEmpty()) {
            etNombre.error = "Utiliza tu nombre y apellido."
            etNombre.requestFocus()
            return
        }
        if (email.isEmpty()) {
            etMail.error = "Revisa la dirección de correo electronico."
            etMail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etMail.error = "Revisa la dirección de correo electronico."
            etMail.requestFocus()
            return
        }
        if (contrasena.isEmpty()) {
            etContra.error = "La contraseña debe poseer al menos 6 digitos."
            etContra.requestFocus()
            return
        }
        if (NetworkCheck().isOnline()) {
        if (username.isNotEmpty() && nombre.isNotEmpty() && email.isNotEmpty() && contrasena.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            bRegistro.visibility = View.GONE
            auth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this.requireActivity()) { task ->
                    progressBar.visibility = View.GONE
                    bRegistro.visibility = View.VISIBLE
                    saveUserInDatabase()
                    saveProfilePhotoInFirestore()
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(activity, "Registro exitoso, revisa tu casilla de email para verificar tu cuenta", Toast.LENGTH_LONG).show()
                                    val goAuthActivity = Intent(activity, AuthActivity::class.java)
                                    startActivity(goAuthActivity)
                                    activity?.finish()

                                }
                            }
                    } else {
                        Toast.makeText(activity, "El registro no se ha podido completar, verifica los datos ingresados", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }else{
            Toast.makeText(activity, "Verifica tu conexión a internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveProfilePhotoInFirestore(){
        if (selectedPhotoUri == null)
            return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    saveProfilePhotoInDB(it.toString())
                }
            }
    }

    private fun saveProfilePhotoInDB(profileImageUrl: String){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val checkUser = auth.currentUser!!.uid
        reference.child(checkUser)
            .child("Datos Personales")
            .child("profileImageUrl")
            .setValue(profileImageUrl)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data !=null && data.data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)
            cvFotoDelRegistro.setImageBitmap(bitmap)
            ivCircle.alpha = 0f

        }
    }
}