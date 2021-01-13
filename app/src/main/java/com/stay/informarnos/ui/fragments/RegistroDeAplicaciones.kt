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
import com.stay.informarnos.data.models.Aplicacion
import com.stay.informarnos.data.models.Ficha
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.*
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.bGenerarFicha
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.bIngreseFdn
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.etIngreseApellido
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.etIngreseDNI
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.etIngreseNombre
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.layoutCrearFicha
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.spProvincias
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.spSexo
import kotlinx.android.synthetic.main.fragment_registro_de_aplicaciones.tvNombreyApellido
import java.util.*


class RegistroDeAplicaciones : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro_de_aplicaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showBuscarYAplicarlayout()
        spinners()
        spinnerFun()

        bVerFicha.setOnClickListener {
            traerDatos()
        }

        bIngreseFdn.setOnClickListener {
            obtenerFecha()
        }

        bGenerarFicha.setOnClickListener {
            generarFicha()
        }

        bFechaDeRegistro.setOnClickListener{
            obtenerFecha()
        }

        bAgregarAplicacion.setOnClickListener {
            subirDosis()
        }
    }

    private fun generarFicha(){
        val dni = etIngreseDNI.text.toString()
        val nombre = etIngreseNombre.text.toString()
        val apellido = etIngreseApellido.text.toString()
        val sexo = spSexo.selectedItem.toString()
        val fdn = bIngreseFdn.text.toString()
        val provincia = spProvincias.selectedItem.toString()


        if (fdn.isEmpty() || dni.equals(null) || dni.length<7 || dni.length >8  || nombre.isEmpty() || apellido.isEmpty() || sexo == "Seleccione-" || provincia == "Seleccione-"){
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
                    showRegistroLayout()
                }
        }
    }

    private fun traerDatos(){
        val dni = etBuscarPorDni.text.toString()
        if (dni.isEmpty() || dni.length<7 || dni.length >8) {
            Toast.makeText(activity, "Por favor ingrese un DNI valido.", Toast.LENGTH_SHORT).show()
        }
        else {
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
                        showAplicacioneslayout()
                        tvDni.text = dniFromDB
                        tvNombreyApellido.text = "$nombreFromDB $apellidoFromDB"
                        tvFdn.text = fdnFromDB
                    } else {
                        showCrearFichaLayout()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    private fun subirDosis(){
        val dni = tvDni.text.toString()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("pacientes")
        val fecha = bFechaDeRegistro.text.toString()
        val categoria = sCategorias.selectedItem.toString()
        val vacuna = sVacunasOBiologico.selectedItem.toString()
        val motivo = sMotivo.selectedItem.toString()
        val dosis = sNDosis.selectedItem.toString()
        val lote = etLote.text.toString()

        if(fecha.isNotEmpty() && categoria != "-Seleccione-" && vacuna != "-Seleccione-" && motivo != "-Seleccione-" && dosis != "-Seleccione-" ){
            val uploadAplicaciones = Aplicacion(fecha, categoria, vacuna, motivo, dosis, lote)
            reference.child(dni)
                .child("Aplicaciones")
                .child(categoria)
                .child(vacuna)
                .setValue(uploadAplicaciones)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Envío de datos realizado correctamente.", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(activity, "Los datos no se han podido enviar, verifique los campos.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun spinnerFun() {
        val categorias = resources.getStringArray(R.array.categorias)
        val vcn = resources.getStringArray(R.array.vcn)
        val vCampania = resources.getStringArray(R.array.vCampaña)
        val vOtros = resources.getStringArray(R.array.vOtros)
        val seleccione = resources.getStringArray(R.array.seleccione)
        var pos:Int = 0
        val spinner = activity?.findViewById<Spinner>(R.id.sCategorias)
        val spinnerV = activity?.findViewById<Spinner>(R.id.sVacunasOBiologico)
        otrosSpinner()
        val adp1 = ArrayAdapter(this.requireActivity(), R.layout.spinner_item, categorias)
        adp1.setDropDownViewResource(R.layout.spinner_item)
        spinner?.adapter = adp1
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    private fun otrosSpinner(){
        val motivo = resources.getStringArray(R.array.motivos)
        val tresDosis = resources.getStringArray(R.array.tresdosis)
        val dosis = resources.getStringArray(R.array.tresDosisConRefuerzo)
        val spinnerM = activity?.findViewById<Spinner>(R.id.sMotivo)
        val spinnerD = activity?.findViewById<Spinner>(R.id.sNDosis)
        val adp = ArrayAdapter(requireContext(), R.layout.spinner_item, motivo)
        adp.setDropDownViewResource(R.layout.spinner_item)
        spinnerM?.adapter = adp
        val adp2 = ArrayAdapter(requireContext(), R.layout.spinner_item, dosis)
        adp2.setDropDownViewResource(R.layout.spinner_item)
        spinnerD?.adapter = adp2
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
                bFechaDeRegistro.setText(selectedDate)
                bIngreseFdn.setText(selectedDate)
            }, year, month, day)
        dpd.show()
    }

    private fun showRegistroLayout(){
        layoutCrearFicha.visibility = View.GONE
        layoutBuscarYaplicar.visibility = View.GONE
        layoutRegistro.visibility = View.VISIBLE
    }

    private fun showCrearFichaLayout(){
        layoutBuscarYaplicar.visibility = View.GONE
        layoutAplicaciones.visibility = View.GONE
        layoutCrearFicha.visibility = View.VISIBLE
    }

    private fun showAplicacioneslayout(){
        layoutAplicaciones.visibility = View.VISIBLE
        layoutBuscarYaplicar.visibility = View.GONE
        layoutCrearFicha.visibility = View.GONE
    }

    private fun showBuscarYAplicarlayout(){
        layoutBuscarYaplicar.visibility = View.VISIBLE
        layoutAplicaciones.visibility = View.GONE
        layoutCrearFicha.visibility = View.GONE
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

    fun Int.twoDigits() =
        if (this <= 9) "0$this" else this.toString()
}
