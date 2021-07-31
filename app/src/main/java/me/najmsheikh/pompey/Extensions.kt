package me.najmsheikh.pompey

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun Any.toast(context: Context) {
    Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
}

fun Any.log() {
    Log.d("[DEBUG]", this.toString())
}

fun String?.parseAsDate(): LocalDate? {
    return try {
        LocalDate.parse(this ?: "")
    } catch (e: DateTimeParseException) {
        null
    }
}

fun String.encodeToBase64(): String = Base64.encodeToString(toByteArray(), Base64.DEFAULT)
