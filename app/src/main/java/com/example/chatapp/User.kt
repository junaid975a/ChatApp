package com.example.chatapp

import android.provider.ContactsContract.CommonDataKinds.Email

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var phoneNo: String? = null
    var imageUrl: String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?, phoneNo: String?, imageUrl: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.phoneNo = phoneNo
        this.imageUrl = imageUrl
    }
}