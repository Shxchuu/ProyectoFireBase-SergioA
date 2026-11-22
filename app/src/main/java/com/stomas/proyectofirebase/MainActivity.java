package com.stomas.proyectofirebase;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etRaza, etEdad;
    private Button btnEnviar, btnCargar;
    private ListView lvMascotas;

    private FirebaseFirestore db;
    private ArrayList<String> listaMascotas = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etRaza = findViewById(R.id.etRaza);
        etEdad = findViewById(R.id.etEdad);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnCargar = findViewById(R.id.btnCargar);
        lvMascotas = findViewById(R.id.lvMascotas);

        db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listaMascotas);
        lvMascotas.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> enviarDatos());
        btnCargar.setOnClickListener(v -> cargarListaFirestore());
    }

    private void enviarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (nombre.isEmpty() || raza.isEmpty() || edad.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mascota = new HashMap<>();
        mascota.put("nombre", nombre);
        mascota.put("raza", raza);
        mascota.put("edad", Integer.parseInt(edad));

        db.collection("mascotas")
                .add(mascota)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Datos enviados",
                            Toast.LENGTH_SHORT).show();
                    etNombre.setText("");
                    etRaza.setText("");
                    etEdad.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void cargarListaFirestore() {
        db.collection("mascotas")
                .get()
                .addOnSuccessListener(query -> {
                    listaMascotas.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        listaMascotas.add(doc.getString("nombre") + " - "
                                + doc.getString("raza") + " - "
                                + doc.get("edad") + " años");
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}