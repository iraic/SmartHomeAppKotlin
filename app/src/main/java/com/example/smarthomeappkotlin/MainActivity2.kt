package com.example.smarthomeappkotlin

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {
    var etTipo: EditText? = null
    var etValor:EditText? = null
    var bAdd: Button? = null
    var bRefresh:Button? = null
    var rvMsg: RecyclerView? = null
    var sesion: SharedPreferences? = null
    var lista: Array<Array<String?>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        etTipo = findViewById(R.id.etTipo)
        etValor = findViewById(R.id.etValor)
        bAdd = findViewById(R.id.bAdd)
        bRefresh = findViewById(R.id.bRefresh)
        rvMsg = findViewById(R.id.rvMsg)
        sesion = getSharedPreferences("sesion", 0)
        supportActionBar!!.title = "Mensajes - " + sesion!!.getString("user","")
        rvMsg!!.setHasFixedSize(true)
        rvMsg!!.itemAnimator = DefaultItemAnimator()
        rvMsg!!.layoutManager = LinearLayoutManager(this)
        llenar()
        bRefresh!!.setOnClickListener { llenar() }
        bAdd!!.setOnClickListener { agregar() }
    }

    private fun agregar() {
        val url = Uri.parse(Config.URL + "registro.php")
            .buildUpon().build().toString()
        val peticion = object : StringRequest(
            Request.Method.POST, url,
            {response -> agregarRespuesta(response)},
            {error -> Toast.makeText(this@MainActivity2, "Error de conexion", Toast.LENGTH_SHORT).show()}
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String?>? {
                val header: MutableMap<String, String?> = java.util.HashMap()
                header["Authorization"] = sesion!!.getString("token", "Error")
                return header
            }

            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["sensor"] = etTipo!!.text.toString()
                params["valor"] = etValor!!.text.toString()
                return params
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun agregarRespuesta(response: String?) {
        try {
            val r = JSONObject(response)
            if (r.getString("add").compareTo("y") == 0) {
                Toast.makeText(this@MainActivity2, "Almacenado correctamente " + r.getString("id"), Toast.LENGTH_SHORT).show()
                llenar()
            } else {
                Toast.makeText(this@MainActivity2, "Error no se pudo agregar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {}
    }

    private fun llenar() {
        val url = Uri.parse(Config.URL + "registro.php")
            .buildUpon().build().toString()
        val peticion = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response -> llenarRespuesta(response) },
            { Toast.makeText(this@MainActivity2, "Error de conexion", Toast.LENGTH_SHORT).show() }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String?>? {
                val header: MutableMap<String, String?> = HashMap()
                header["Authorization"] = sesion!!.getString("token", "Error")
                return header
            }
        }

        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun llenarRespuesta(response: JSONArray?) {
        try {
            Log.d("DEPURAR", "llenarRespuesta:" + response.toString())
            lista = Array(response!!.length()) { Array<String?>(5){null}}
            for (i in 0 until response!!.length()) {
                lista!![i][0] = response!!.getJSONObject(i).getString("id")
                lista!![i][1] = response!!.getJSONObject(i).getString("user")
                lista!![i][2] = response!!.getJSONObject(i).getString("sensor")
                lista!![i][3] = response!!.getJSONObject(i).getString("valor")
                lista!![i][4] = response!!.getJSONObject(i).getString("fecha")
            }
            Log.d("DEPURAR", lista.toString())
            rvMsg!!.adapter = MyAdapter(lista, object : RecyclerViewOnItemClickListener {
                override fun onClick(v: View?, position: Int) {
                    Toast.makeText(
                        this@MainActivity2,
                        "Clicl al elemento $position",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onClickEdit(v: View?, position: Int) {
                    val extras = Bundle()
                    extras.putString("id", lista!![position][0])
                    extras.putString("tipo", lista!![position][2])
                    extras.putString("valor", lista!![position][3])
                    val i = Intent(this@MainActivity2, MainActivity3::class.java)
                    i.putExtras(extras)
                    startActivity(i)
                }

                override fun onClickDel(v: View?, position: Int) {
                    AlertDialog.Builder(this@MainActivity2)
                        .setTitle("Eliminar")
                        .setMessage("Quieres eliminar el mensaje id=" + lista!![position][0] + "?")
                        .setPositiveButton(
                            "Si"
                        ) { dialogInterface, i -> eliminar(lista!![position][0]) }
                        .setNegativeButton("No", null)
                        .create().show()
                }
            })
            Toast.makeText(this, "Lista Actualizada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d("Error", e.message!!)
        }
    }

    private fun eliminar(id: String?) {
        val url = Uri.parse(Config.URL + "registro.php")
            .buildUpon()
            .appendQueryParameter("id", id)
            .build().toString()
        val peticion: JsonObjectRequest = object : JsonObjectRequest(
            Method.DELETE, url, null,
            Response.Listener { response -> respuestaEliminar(response) },
            Response.ErrorListener {
                Toast.makeText(
                    this@MainActivity2,
                    "Error de red",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: MutableMap<String, String> = java.util.HashMap()
                header["Authorization"] = sesion!!.getString("token", "Error")!!
                return header
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun respuestaEliminar(response: JSONObject?) {
        try {
            if (response!!.getString("delete").compareTo("y") == 0) {
                Toast.makeText(this, "Datos eliminados", Toast.LENGTH_SHORT).show()
                llenar()
            } else {
                Toast.makeText(this, "No se puede eliminar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
        }
    }
}