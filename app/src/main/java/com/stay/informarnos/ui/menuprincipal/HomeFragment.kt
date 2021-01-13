package com.stay.informarnos.ui.menuprincipal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.stay.informarnos.R
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        obtenerDatos()
        mostrarFecha()

        bFormulario.setOnClickListener {
            val action = HomeFragmentDirections.actionGoEnviarDatos()
            Navigation.findNavController(it).navigate(action)
        }

        bEstadisticas.setOnClickListener {
            val action = HomeFragmentDirections.actionGoEstadisticas()
            Navigation.findNavController(it).navigate(action)
        }

        bPerfil.setOnClickListener {
            val action = HomeFragmentDirections.actionGoPerfil()
            Navigation.findNavController(it).navigate(action)
        }

        bBuscarPersona.setOnClickListener {
            val action = HomeFragmentDirections.actionGoBuscarPaciente()
            Navigation.findNavController(it).navigate(action)
        }

        bRegistrarAplicacion.setOnClickListener {
            val action = HomeFragmentDirections.actionGoRegistroAplicaciones()
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun obtenerDatos(){
        val foto = requireActivity().findViewById<ImageView>(R.id.ivFotoP)
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val checkUser: Query = reference.child(auth.currentUser!!.uid)
        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nameFromDB =
                        snapshot.child("Datos Personales").child("nombre").value.toString()
                    val photoFromDB =
                        snapshot.child("Datos Personales").child("profileImageUrl").value.toString()
                    tvNombre?.text = nameFromDB
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
    private fun mostrarFecha(){
        val currentDateTimeString: String = DateFormat.getDateTimeInstance().format(Date())
        tvDiaDeHoy.text = currentDateTimeString
    }
}