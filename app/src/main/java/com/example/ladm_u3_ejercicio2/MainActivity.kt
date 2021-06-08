package com.example.ladm_u3_ejercicio2

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var baseremota = FirebaseFirestore.getInstance()
    var baseDatos = BaseDatos(this,"Productos",null,1)
    var listaID = ArrayList<String>()
    var datalista = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //mostrarcontenid()

        button.setOnClickListener {
            insertar()
           /* try{
            AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage("QUE DESEAS HACER?")
                .setNegativeButton("CANCELAR"){d,i->
                    d.dismiss()
                }
                .setPositiveButton("INSERTAR"){d,i->
                    insertar()
                }
                .setNeutralButton("CONSULTAR"){d,i->
                    consulta()
                }
            }catch (err:Exception){
                mensaje("NO SE PUDO SELECCIONAR")
            }*///AQUI SE TERMINA TODITO
        }

       button2.setOnClickListener {
          try{
              var transaccion = baseDatos.writableDatabase
              var productos = ArrayList<String>()
              var cursor = transaccion.query("APARTADO", arrayOf("*"),null,null,null,null,null)
              if(cursor.moveToFirst()){
                  listaID.clear()

                  do{
                      var dataInsert= hashMapOf(
                              "IDAPARTADO" to cursor.getString(0),
                              "NOMBRECLIENTE" to cursor.getString(1),
                              "NOMBRE_PREODUCTO" to cursor.getString(2),
                              "PRECIO" to cursor.getString(3))
                      listaID.add(cursor.getInt(0).toString())

                      baseremota.collection("APARTADO")
                              .add(dataInsert)
                              .addOnSuccessListener {
                                  mensaje("SINCRONIZACION EXITOSA")
                              }
                              .addOnFailureListener{
                                  mensaje("NO SE PUDO HACER LA ELIMINACION")
                              }
                  }while (cursor.moveToNext())

              }else{
                  productos.add("sin datos")
              }
              listaproduct.adapter=ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,productos)
              eliminaruno()
              transaccion.close()

              mostrarcontenid()

          }catch (err:SQLiteException){
              mensaje(err.message!!)
          }

        }

        buttonc.setOnClickListener {
            consulta()
        }
    }

    private fun mostrarcontenid() {
        baseremota.collection("APARTADO")
                .addSnapshotListener { value, error ->
                    if(error!=null){
                        mensaje(error.message!!)
                        return@addSnapshotListener
                    }
                    datalista.clear()
                    listaID.clear()

                    for (document in value!!){
                        var cad = "${document.getString("IDAPARTADO")} || ${document.getString("NOMBRECLIENTE")} || " +
                                "${document.getString("NOMBRE_PREODUCTO")} || ${document.getString("PRECIO")}"
                        datalista.add(cad)
                        listaID.add(document.id.toString())
                    }
                    listaproduct.adapter=ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,datalista)
                    listaproduct.setOnItemClickListener { parent, view, position, id ->
                        eliminaoactualiza(position)
                    }


                }
    }

    private fun eliminaoactualiza(posicion:Int) {
        var idElegido = listaID.get(posicion)
        AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage("¿QUE DESEAS HACER CON\n${datalista.get(posicion)}")
                .setPositiveButton("Eliminar"){d,i->
                    eliminarq(idElegido)
                }
                .setNeutralButton("ACTUALIZAR"){d,i->}
                .setNegativeButton("CANCELAR"){d,i->}
                .show()

    }

    private fun eliminarq(idElegido: String) {
        baseremota.collection("APARTADO")
                .document(idElegido)
                .delete()
                .addOnSuccessListener {
                    alerta("SE ELIMINO CON EXITO")
                }
                .addOnFailureListener{
                    mensaje("ERROR: ${it.message!!}")
                }
    }


    private fun mensaje2(s: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setPositiveButton("OK"){d,i->}
            .show()
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    }


    private fun insertar() {
        try {
            var insertar = baseDatos.writableDatabase
            var SQL ="INSERT INTO APARTADO VALUES(NULL,'${nombrecliente.text.toString()}','${nombreproducto.text.toString()}',${precioproducto.text.toString().toFloat()})"
            insertar.execSQL(SQL)
            cargarproductos()
            limpiarcampos()
            insertar.close()
        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun limpiarcampos() {
        nombrecliente.setText("")
        nombreproducto.setText("")
        precioproducto.setText("")
    }

    private fun cargarproductos() {
        try {
            var select = baseDatos.readableDatabase
            var productos = ArrayList<String>()
            var SQL = "SELECT * FROM APARTADO"

            var cursor =select.rawQuery(SQL,null)
            listaID.clear()

            if(cursor.moveToFirst()){
                do{
                    var data = "["+cursor.getString(1)+"] -- "+ cursor.getString(2) +" "+"$"+ cursor.getString(3)
                    productos.add(data)
                    listaID.add(cursor.getInt(0).toString())

                }while (cursor.moveToNext())

            }else{
                productos.add("NO HAY PRODUCTOS")
            }
            select.close()

            listaproduct.adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,productos)
            listaproduct.setOnItemClickListener {
                adapterView, view, posicion, l ->
                var idBorrar = listaID.get(posicion)
                AlertDialog.Builder(this)
                    .setTitle("ATENCION")
                    .setMessage("¿QUE DESEAS HACER CON ID: ${idBorrar}")
                    .setNegativeButton("CANCELAR"){d,i->
                        d.dismiss()
                    }
                    .setPositiveButton("Eliminar"){d,i->
                        eliminar(idBorrar)
                    }
                        .setNeutralButton("ACTUALIZAR"){d,i->
                            var intent = Intent(this,MainActivity2::class.java)
                            intent.putExtra("idactualizar",idBorrar)
                            startActivity(intent)
                        }
                    .show()
            }

        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun eliminar(idBorrar: String) {

        try {
            var eliminar = baseDatos.writableDatabase
            var SQL ="DELETE FROM APARTADO WHERE IDAPARTADO =${idBorrar}"
            eliminar.execSQL(SQL)
            cargarproductos()
            eliminar.close()
        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }

    }

    fun eliminaruno(){
        try{
            var transaccion=baseDatos.writableDatabase
            for(i in 1..listaID.size) {
                var resultado = transaccion.delete("APARTADO", "IDAPARTADO=?", arrayOf(listaID.get(i-1).toString()))
            }
            transaccion.close()
        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun consulta(){
        try {
            var transaccion = baseDatos.readableDatabase
            var idAbuscar = idpartado.text.toString()
            var cursor = transaccion.query(
                "APARTADO", arrayOf("NOMBRECLIENTE","NOMBRE_PREODUCTO","PRECIO"), "IDAPARTADO=?",
                arrayOf(idAbuscar), null, null, null)

            if (cursor.moveToFirst()) {
                textViewc.setText("NOMBRECLIENTE :${cursor.getString(0)}, NOMBREPRODUCTO: ${cursor.getString(1)}\nPRECIO: ${cursor.getString(2)}"
                )
            } else {
                mensaje("ERROR! No se encontró resultado tras la consulta")
            }
            transaccion.close()
            limpiarcampos()
        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("Atencion").setMessage(s).setPositiveButton("OK"){ d, i->}.show()
    }


}