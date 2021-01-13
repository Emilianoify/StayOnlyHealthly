package com.stay.informarnos.data.models

interface Users {

    var uid:String
    var profileImageUrl:String
    var nombre:String
    var email:String
    var username:String



}

data class User(
    override var uid: String,
    override var profileImageUrl:String,
    override var nombre: String,
    override var email: String,
    override var username: String
):Users {

}