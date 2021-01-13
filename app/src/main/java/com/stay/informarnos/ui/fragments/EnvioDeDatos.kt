package com.stay.informarnos.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.stay.informarnos.R
import com.stay.informarnos.data.models.Dato
import kotlinx.android.synthetic.main.fragment_envio_de_datos.*
import java.util.*


class EnvioDeDatos : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_envio_de_datos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        etCalendario.setOnClickListener {
            obtenerFecha()
        }
        bEnviar.setOnClickListener {
            subidaDeDatos()
        }
    }
    private fun subidaDeDatos() {

        if (etCalendario.text.isEmpty() || etCc.text.isEmpty() || etPa.text.isEmpty() || etDo.text.isEmpty()) {
            Toast.makeText(activity, "Completa los campos", Toast.LENGTH_SHORT).show()
        } else {
            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
            val checkUser = auth.currentUser!!.uid
            val fecha = etCalendario.text.toString()
            val personasAtendidas = etPa.text.toString().toInt()
            val calendariosCompletos = etCc.text.toString().toInt()
            val dosisAplicadas = etDo.text.toString().toInt()

            val uploadInfo = Dato(fecha, personasAtendidas, calendariosCompletos, dosisAplicadas)
            reference.child(checkUser)
                .child("Datos subidos")
                .child(fecha)
                .setValue(uploadInfo)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Formulario enviado con exito", Toast.LENGTH_SHORT).show()
                }
        }
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
            etCalendario.setText(selectedDate)
        },year,month,day)
        dpd.show()
    }

    fun Int.twoDigits() =
        if (this <= 9) "0$this" else this.toString()
}