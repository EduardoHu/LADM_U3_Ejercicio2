package com.example.ladm_u3_ejercicio2

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    var baseremota2 = FirebaseFirestore.getInstance()
    var baseSQLite = BaseDatos(this,"Productos",null,1)
    var listaID = ArrayList<String>()
    var id =""
    var datalista = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

       /* baseremota2.collection("Productos")
            .addSnapshotListener { value, error ->
                if(error != null){
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                datalista.clear()
                for(document in value!!){
                    var cadena = "NombreCliente${document.getString("nombrecliente")} NombreProducto ${document.get("nombreproducto")} ${document.get("Precio")}"
                    datalista.add(cadena)

                    listaID.add(document.id.toString())
                }
                listprodutact.adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,datalista)
                listprodutact.setOnItemClickListener { parent, view, position, id ->
                    dialogoEliminaActualiza(position)
                }
            }*/


        var extra = intent.extras
        id = extra!!.getString("idactualizar")!!
        try {
            var transaccion = baseSQLite.readableDatabase
            var cursor = transaccion.query("APARTADO", arrayOf("NOMBRECLIENTE, NOMBRE_PREODUCTO, PRECIO"),
                    "IDAPARTADO=?", arrayOf(id),null,null,null)

            if(cursor.moveToFirst()){
                nombreclienteactualizar.setText(cursor.getString(0))
                nombreproductoactualizar.setText(cursor.getString(1))
                precioproductoactualizar.setText(cursor.getString(2))
            }else{
                mensaje("No se pudo recuperar la data de ID ${id}")
            }
            transaccion.close()

        }catch (err:SQLiteException)
        {
            mensaje(err.message!!)
        }

        button3.setOnClickListener {
            actualizar(id)
        }

        button4.setOnClickListener {
            finish()
        }
        button5.setOnClickListener {
            var intent = Intent(this,MainActivity3::class.java)
            startActivity(intent)
        }

    }

    private fun actualizar(id: String) {
        try {

            var datosInsertar = hashMapOf(
                "NOMBRECLIENTE" to nombreclienteactualizar.text.toString(),
                "NOMBRE_PREODUCTO" to nombreproductoactualizar.text.toString(),
                "PRECIO" to precioproductoactualizar.text.toString().toFloat()
            )
            baseremota2.collection("Apartado")
                .add(datosInsertar)
                .addOnSuccessListener {
                    alerta("SE INCERTO CORRECTAMENTE EN FIRESTORE")
                }
                .addOnFailureListener{
                    mensaje2("ERROR: ${it.message!!}")
                }

            var transaccion = baseSQLite.writableDatabase
            var valores = ContentValues()

            valores.put("NOMBRECLIENTE",nombreclienteactualizar.text.toString())
            valores.put("NOMBRE_PREODUCTO",nombreproductoactualizar.text.toString())
            valores.put("PRECIO",precioproductoactualizar.text.toString())
            var resultado = transaccion.update("APARTADO",valores,"IDAPARTADO=?", arrayOf(id))

            if(resultado>0){
                mensaje("Se actualizao correctamente ID")
                finish()
            }else{
                mensaje("No se actualizo")
            }
            transaccion.close()
        }catch (err:SQLiteException)
        {
            mensaje(err.message!!)
        }

        //var productos = ArrayList<String>()
        //eliminartodo()



    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("Atencion").setMessage(s).setPositiveButton("OK"){ d, i->}.show()
    }

    private fun dialogoEliminaActualiza(position: Int) {
        var idElegido = listaID.get(position)

        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setPositiveButton("ELIMINAR"){d,i->
                eliminardoc(idElegido)
            }
            .setNegativeButton("ACTUALIZAR "){d,i->}
            .setNeutralButton("CANCELAR"){d,i->}

    }

    private fun eliminardoc(idElegido: String) {
        baseremota2.collection("Productos")
            .document(idElegido)
            .delete()
            .addOnSuccessListener { alerta("SE ELIMINO CON EXITO") }
            .addOnFailureListener{mensaje("ERROR ${it.message!!}")}
    }
    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

    private fun mensaje2(s: String) {
        AlertDialog.Builder(this).setTitle("Atencion").setMessage(s).setPositiveButton("OK"){ d, i->}.show()
    }


}