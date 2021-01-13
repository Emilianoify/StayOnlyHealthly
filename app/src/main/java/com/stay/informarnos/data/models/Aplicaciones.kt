package com.stay.informarnos.data.models

interface Aplicaciones {

    var fechaDeAplicacion:String
    var categoria:String
    var vacuna:String
    var motivo:String
    var dosis:String
    var lote:String


}

data class Aplicacion(
    override var fechaDeAplicacion:String,
    override var categoria: String,
    override var vacuna: String,
    override var motivo: String,
    override var dosis: String,
    override var lote: String
):Aplicaciones{

}

