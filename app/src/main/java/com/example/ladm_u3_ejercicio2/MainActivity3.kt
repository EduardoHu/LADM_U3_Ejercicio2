package com.example.ladm_u3_ejercicio2

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity3 : AppCompatActivity() {
    var baseSQLite = BaseDatos(this,"Productos",null,1)
    var listaID = ArrayList<String>()
    var id =""
    var datalista = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        button6.setOnClickListener {
            consulta()
        }

        button7.setOnClickListener {
            finish()
        }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("Atencion").setMessage(s).setPositiveButton("OK"){ d, i->}.show()
    }

    private fun consulta() {
        try {
            var transaccion = baseSQLite.readableDatabase
            var idAbuscar = idmostap.text.toString()
            var cursor = transaccion.query(
                "APARTADO", arrayOf("NOMBRECLIENTE,NOMBRE_PREODUCTO,PRECIO"), "IDAPARTADO=?",
                arrayOf(idAbuscar), null, null, null
            )

            if (cursor.moveToFirst()) {
                textView.setText("NOMBRECLIENTE :${cursor.getString(0)}," +
                        "NOMBREPRODUCTO: ${cursor.getString(1)}\nPRECIO: ${cursor.getString(2)}"
                )
            } else {
                mensaje("ERROR! No se encontró resultado tras la consulta")
            }
            transaccion.close()

        }catch (err: SQLiteException){
            mensaje(err.message!!)
        }
    }
    }
