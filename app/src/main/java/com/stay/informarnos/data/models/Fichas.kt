package com.stay.informarnos.data.models

interface Fichas {

    var dni:String
    var nombre:String
    var apellido:String
    var sexo:String
    var fechaDeNacimiento:String
    var provincia:String

}
data class Ficha(

    override var dni:String,
    override var nombre:String,
    override var apellido:String,
    override var sexo:String,
    override var fechaDeNacimiento: String,
    override var provincia: String

):Fichas {

}