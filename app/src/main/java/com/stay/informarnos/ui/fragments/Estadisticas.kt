package com.stay.informarnos.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.stay.informarnos.R
import kotlinx.android.synthetic.main.fragment_estadisticas.*
import java.util.*


class Estadisticas : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estadisticas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        etFechaIngresada.setOnClickListener {
            obtenerFecha()
        }

        bBuscar.setOnClickListener {
            obtenerDatos()
        }
    }

    private fun obtenerDatos() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        val checkUser: Query = reference.child(auth.currentUser!!.uid)
        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val fecha = etFechaIngresada.text.toString()
                val dosisFromDB = snapshot.child("Datos subidos").child(fecha).child("dosisAplicadas").value.toString()
                val personasFromDB = snapshot.child("Datos subidos").child(fecha).child("personasAtendidas").value.toString()
                val calendariosFromDB = snapshot.child("Datos subidos").child(fecha).child("calendariosCompletos").value.toString()
                if (snapshot.exists()) {
                    if (fecha.isEmpty()) {
                        Toast.makeText(activity, "Por favor seleccione una fecha.", Toast.LENGTH_SHORT).show()
                    } else if (snapshot.child("Datos subidos").child(fecha).exists()) {
                        tvDosis.text = dosisFromDB
                        tvPersonasA.text = personasFromDB
                        tvCalendariosC.text = calendariosFromDB
                    } else {
                        tvDosis.text = "No hay datos"
                        tvPersonasA.text = "No hay datos"
                        tvCalendariosC.text = "No hay datos"
                        Toast.makeText(activity, "No existen datos para la fecha ingresada.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun obtenerFecha(){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this.requireActivity(), R.style.MyDialogTheme, DatePickerDialog.OnDateSetListener { _, myear, mMonth, dayOfMonth ->
            val dayStr = dayOfMonth.twoDigits()
            val monthStr = (mMonth + 1).twoDigits()
            val selectedDate = "$dayStr$monthStr$year"
            etFechaIngresada.text = selectedDate
        },year,month,day)
        dpd.show()
    }

    fun Int.twoDigits() =
        if (this <= 9) "0$this" else this.toString()

}