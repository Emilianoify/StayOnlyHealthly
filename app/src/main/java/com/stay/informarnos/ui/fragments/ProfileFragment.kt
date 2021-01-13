package com.stay.informarnos.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.stay.informarnos.R
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        obtenerDatos()

        ivFotoDelPerfil.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        bGuardarCambios.setOnClickListener {
            uploadImageToFirebase()
        }

        tvCambiarEmail.setOnClickListener{
            val action = ProfileFragmentDirections.actionUpdateMail()
            Navigation.findNavController(it).navigate(action)
        }

        tvCambiarContraseña.setOnClickListener {
            val action = ProfileFragmentDirections.actionUpdatePass()
            Navigation.findNavController(it).navigate(action)
        }


    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data !=null && data.data != null){
            //procede a chequear la imagen seleccionada
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)
            cvFotoDelPerfil.setImageBitmap(bitmap)
            ivFotoDelPerfil.alpha = 0f

        }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null)
            return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    saveNewPhoto(it.toString())

                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "No se ha cargado la foto", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveNewPhoto(profileImageUrl: String){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val checkUser = auth.currentUser!!.uid
        reference.child(checkUser)
            .child("Datos Personales")
            .child("profileImageUrl")
            .setValue(profileImageUrl)
            .addOnSuccessListener {
                Toast.makeText(activity, "Foto de perfil cambiada con exito.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerDatos(){
        var foto = requireActivity().findViewById<ImageView>(R.id.ivFotoDelPerfil)
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val checkUser: Query = reference.child(auth.currentUser!!.uid)
        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val nameFromDB = snapshot.child("Datos Personales").child("nombre").value.toString()
                    val emailFromDB = snapshot.child("Datos Personales").child("email").value.toString()
                    val photoFromDB = snapshot.child("Datos Personales").child("profileImageUrl").value.toString()
                    val usernameFromDB = snapshot.child("Datos Personales").child("username").value.toString()
                    tvNombreIngresado.text = nameFromDB
                    tvEmailIngresado.text = emailFromDB
                    tvUsername.text = usernameFromDB
                    tvCambiarContraseña.setText(R.string.tv_cambiarcontra)
                    tvCambiarEmail.setText(R.string.tv_cambiarEmail)
                    Glide.with(activity!!)
                        .load(photoFromDB)
                        .placeholder(R.drawable.ic_boy)
                        .error(R.drawable.ic_boy)
                        .into(foto)

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}