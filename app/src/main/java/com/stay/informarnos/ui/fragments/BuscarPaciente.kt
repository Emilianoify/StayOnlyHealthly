package com.stay.informarnos.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.stay.informarnos.R
import com.stay.informarnos.data.models.Ficha
import kotlinx.android.synthetic.main.fragment_buscar_paciente.*
import java.util.*


class BuscarPaciente : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buscar_paciente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showBuscarPorDniLayout()
        spinners()

        bBuscarPorDni.setOnClickListener {
            buscarPaciente()
        }
        bIngreseFdn.setOnClickListener {
            obtenerFecha()
        }

        bGenerarFicha.setOnClickListener {
            generarFicha()
        }

        bBuscarVacuna.setOnClickListener {
            buscarDosis()
        }
        spinnerFun()
    }

    private fun buscarPaciente(){
        val dni = etDni.text.toString()
        if (dni.isEmpty() || dni.length <7 || dni.length >8) {
            Toast.makeText(activity, "Por favor ingrese un DNI valido.", Toast.LENGTH_SHORT).show()
        } else {
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("pacientes")
            val checkdni: Query = reference.child(dni)
            checkdni.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dniFromDB = snapshot.child("Datos Personales").child("dni").value.toString()
                    val nombreFromDB =
                        snapshot.child("Datos Personales").child("nombre").value.toString()
                    val apellidoFromDB =
                        snapshot.child("Datos Personales").child("apellido").value.toString()
                    val fdnFromDB = snapshot.child("Datos Personales")
                        .child("fechaDeNacimiento").value.toString()

                    if (snapshot.exists()) {
                        showDosisYDatosLayout()
                        tvNumeroDeDni.text = "DNI N°: $dniFromDB"
                        tvNombreyApellido.text = "Paciente: $nombreFromDB $apellidoFromDB"
                        tvEdad.text = "Fecha de nacimiento: $fdnFromDB"
                        //tvEdad.text = "Edad actual $edadFromDB"

                    } else {
                        showCrearFichaLayout()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    private fun buscarDosis(){
        val dni = etDni.text.toString()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("pacientes")
        val checkdni:Query = reference.child(dni)
        checkdni.addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoria = sBuscarCategoria.selectedItem.toString()
                val aplicacion = sBuscarAplicacion.selectedItem.toString()
                val vacunaFromDB = snapshot.child("Aplicaciones").child(categoria).child(aplicacion).child("vacuna").value.toString()
                val dosisFromDB =  snapshot.child("Aplicaciones").child(categoria).child(aplicacion).child("dosis").value.toString()
                val fechaFromDB =  snapshot.child("Aplicaciones").child(categoria).child(aplicacion).child("fechaDeAplicacion").value.toString()
               if(snapshot.child("Aplicaciones").child(categoria).child(aplicacion).exists()){
                   tvAplicacion.text = "Vacuna o biologico: $vacunaFromDB"
                   tvDosis.text = "Ultima dosis registrada: $dosisFromDB"
                   tvFdr.text = "Fecha de registro: $fechaFromDB"
               }else if(categoria == "-Seleccione-" || aplicacion == "-Seleccione-"){
                   tvAplicacion.text = ""
                   tvDosis.text = ""
                   tvFdr.text = ""
                   Toast.makeText(activity, "Por favor ingrese vacuna o biologico", Toast.LENGTH_SHORT).show()
               }else{
                   tvAplicacion.text = ""
                   tvDosis.text = ""
                   tvFdr.text = ""
                   Toast.makeText(activity, "No se ha encontrado la vacuna o biologico", Toast.LENGTH_SHORT).show()
               }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun generarFicha(){
        val dni = etIngreseDNI.text.toString()
        val nombre = etIngreseNombre.text.toString()
        val apellido = etIngreseApellido.text.toString()
        val sexo = spSexo.selectedItem.toString()
        val fdn = bIngreseFdn.text.toString()
        val provincia = spProvincias.selectedItem.toString()


        if (fdn.isEmpty() || dni.equals(null) || dni.length<7 || dni.length >8  || nombre.isEmpty() || apellido.isEmpty() || sexo == "-Seleccione-" || provincia == "-Seleccione-"){
            Toast.makeText(activity, "Complete todos los campos", Toast.LENGTH_SHORT).show()
        }else{
            val rootNode = FirebaseDatabase.getInstance()
            val referencia = rootNode.getReference("pacientes")
            val ficha = Ficha(dni, nombre, apellido, sexo, fdn, provincia)
            referencia.child(dni)
                .child("Datos Personales")
                .setValue(ficha)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Ficha generada con exito", Toast.LENGTH_SHORT).show()
                   showBuscarPorDniLayout()
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
            val selectedDate = "$dayStr/$monthStr/$year"
            bIngreseFdn.setText(selectedDate)
        },year,month,day)
        dpd.show()
    }

    private fun spinners(){
        val sp = activity?.findViewById<Spinner>(R.id.spProvincias)
        val spsexo = activity?.findViewById<Spinner>(R.id.spSexo)
        val provincias = resources.getStringArray(R.array.provincias)
        val sexos = resources.getStringArray(R.array.sexo)
        val adp1 = ArrayAdapter(this.requireActivity(), R.layout.spinner_item, provincias)
        sp?.adapter = adp1
        adp1.setDropDownViewResource(R.layout.spinner_item)
        val adp3 = ArrayAdapter(this.requireActivity(), R.layout.spinner_item, sexos)
        spsexo?.adapter = adp3
        adp3.setDropDownViewResource(R.layout.spinner_item)
    }

    private fun spinnerFun(){
        val categorias = resources.getStringArray(R.array.categorias)
        val vcn = resources.getStringArray(R.array.vcn)
        val vCampania = resources.getStringArray(R.array.vCampaña)
        val vOtros = resources.getStringArray(R.array.vOtros)
        val seleccione = resources.getStringArray(R.array.seleccione)
        var pos = 0
        val spinnerC = activity?.findViewById<Spinner>(R.id.sBuscarCategoria)
        val spinnerV = activity?.findViewById<Spinner>(R.id.sBuscarAplicacion)
        val adapter = ArrayAdapter(this.requireActivity(), R.layout.spinner_item, categorias)
        spinnerC?.adapter = adapter
        spinnerC?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                arg2: Int, arg3: Long
            ) {
                pos = arg2
                add()
            }

            private fun add() {

                when (pos) {
                    0 -> {
                        val adp2 = ArrayAdapter(context!!, R.layout.spinner_item, seleccione)
                        adp2.setDropDownViewResource(R.layout.spinner_item)
                        spinnerV?.adapter = adp2

                    }

                    1 -> {
                        val adp2 = ArrayAdapter(context!!, R.layout.spinner_item, vcn)
                        adp2.setDropDownViewResource(R.layout.spinner_item)
                        spinnerV?.adapter = adp2
                    }
                    2 -> {
                        val adp2 = ArrayAdapter(context!!, R.layout.spinner_item, vCampania)
                        adp2.setDropDownViewResource(R.layout.spinner_item)
                        spinnerV?.adapter = adp2
                    }
                    3 -> {
                        val adp2 = ArrayAdapter(context!!, R.layout.spinner_item, vOtros)
                        adp2.setDropDownViewResource(R.layout.spinner_item)
                        spinnerV?.adapter = adp2
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }

        }
    }

    private fun showBuscarPorDniLayout(){
        layoutCrearFicha.visibility = View.GONE
        layoutDosisYDatos.visibility = View.GONE
        layoutBuscarPorDni.visibility = View.VISIBLE
    }

    private fun showDosisYDatosLayout(){
        layoutDosisYDatos.visibility = View.VISIBLE
        layoutCrearFicha.visibility = View.GONE
        layoutBuscarPorDni.visibility = View.GONE
    }

    private fun showCrearFichaLayout(){
        layoutCrearFicha.visibility = View.VISIBLE
        layoutBuscarPorDni.visibility = View.GONE
        layoutDosisYDatos.visibility = View.GONE
    }

    fun Int.twoDigits() =
        if (this <= 9) "0$this" else this.toString()
}
