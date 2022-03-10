package com.example.smarthomeappkotlin

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity3 : AppCompatActivity() {
    var tvIdEdit: TextView? = null
    var etTipoEdit: EditText? = null
    var etValorEdit:EditText? = null
    var ivSaveEdit: ImageView? = null
    var ivCancelEdit:ImageView? = null

    var sesion: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        tvIdEdit = findViewById(R.id.tvIdEdit)
        etTipoEdit = findViewById(R.id.etTipoEdit)
        etValorEdit = findViewById(R.id.etValorEdit)
        ivSaveEdit = findViewById(R.id.ivSaveEdit)
        ivCancelEdit = findViewById(R.id.ivCancelEdit)

        sesion = getSharedPreferences("sesion", 0)
        supportActionBar!!.title = "Mensajes - " + sesion!!.getString("user","")

        val datos = this.intent.extras
        val id : String? = datos?.getString("id")
        val tipo : String? = datos?.getString("tipo")
        val valor : String? = datos?.getString("valor")

        tvIdEdit!!.setText(id)
        etTipoEdit!!.setText(tipo)
        etValorEdit!!.setText(valor)

        ivSaveEdit!!.setOnClickListener { guardar() }

        ivCancelEdit!!.setOnClickListener { startActivity(Intent(this@MainActivity3, MainActivity2::class.java)) }
    }

    private fun guardar() {
        val url = Uri.parse(Config.URL + "registro.php")
            .buildUpon()
            .appendQueryParameter("id", tvIdEdit!!.text.toString())
            .appendQueryParameter("sensor", etTipoEdit!!.text.toString())
            .appendQueryParameter("valor", etValorEdit!!.text.toString())
            .build().toString()
        val peticion: JsonObjectRequest = object : JsonObjectRequest(
            Method.PUT, url, null,
            Response.Listener { response -> respuestaGuardar(response) },
            Response.ErrorListener { Toast.makeText(this@MainActivity3, "Error de red", Toast.LENGTH_SHORT).show() }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = sesion!!.getString("token", "Error")!!
                return header
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun respuestaGuardar(response: JSONObject?) {
        try {
            if (response!!.getString("update").compareTo("y") == 0) {
                Toast.makeText(this, "Datos modificados", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity3, MainActivity2::class.java))
            } else {
                Toast.makeText(this, "No se pueden guardar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
        }
    }
}