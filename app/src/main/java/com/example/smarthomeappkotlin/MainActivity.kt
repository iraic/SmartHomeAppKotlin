package com.example.smarthomeappkotlin

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var etUser: EditText? = null
    var etPass: EditText? = null
    var bInicio: Button? = null
    var sesion: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etUser = findViewById(R.id.etUser)
        etPass = findViewById(R.id.etPass)
        bInicio = findViewById(R.id.bInicio)

        sesion = getSharedPreferences("sesion", 0)

        bInicio!!.setOnClickListener { login() }
    }

    private fun login() {
        val url = Uri.parse(Config.URL + "login.php")
            .buildUpon()
            .appendQueryParameter("user", etUser?.text.toString())
            .appendQueryParameter("pass", etPass?.text.toString())
            .build().toString()

        val peticion = JsonObjectRequest(Request.Method.GET, url, null,
            {response -> respuesta(response) },
            { error ->
                Toast.makeText(this, "Error: "+error.message,Toast.LENGTH_SHORT).show()
            })
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun respuesta(response: JSONObject?) {
        try {
            if (response!!.getString("login") == "y"){
                val jwt = response.getString("token")
                with (sesion!!.edit()) {
                    putString("user", etUser?.text.toString())
                    putString("token", jwt)
                    apply()
                }
                startActivity(Intent(this,MainActivity2::class.java))
            } else {
                Toast.makeText(this, "Error de usuario o contraseña", Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){}
    }
}