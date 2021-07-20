package me.najmsheikh.pompey

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Any.toast(context: Context) {
    Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
}

fun Any.log() {
    Log.d("[DEBUG]", this.toString())
}
