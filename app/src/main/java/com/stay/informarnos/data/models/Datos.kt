package com.stay.informarnos.data.models

interface Datos {

    var fechaDeSubida:String
    var personasAtendidas:Int
    var calendariosCompletos:Int
    var dosisAplicadas:Int




}

data class Dato(

    override var fechaDeSubida:String,
    override var personasAtendidas:Int,
    override var calendariosCompletos:Int,
    override var dosisAplicadas:Int
):Datos {

}
