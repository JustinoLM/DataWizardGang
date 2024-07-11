package com.proyecto.agroinsight;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlantasActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantas);

        ImageView imageView = findViewById(R.id.imageViewPlanta);
        TextView textViewNombre = findViewById(R.id.textViewNombrePlanta);
        TextView textViewFecha = findViewById(R.id.textViewFechaPlanta);
        TextView textViewTipoPlaga = findViewById(R.id.textViewTipoPlaga);
        backButton = findViewById(R.id.back_button);

        // Obtener los datos de la planta desde el Intent
        int idPlanta = getIntent().getIntExtra("idPlanta", -1);
        String nombreComun = getIntent().getStringExtra("nombreComun");
        byte[] imagen = getIntent().getByteArrayExtra("imagen");
        String fechaGuardado = getIntent().getStringExtra("fechaGuardado");
        String tipoPlaga = getIntent().getStringExtra("tipoPlaga");

        // Mostrar los datos de la planta en la interfaz de usuario
        if (imagen != null && imagen.length > 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imagen, 0, imagen.length));
        } else {
            imageView.setImageResource(R.drawable.ic_arrow);  // Imagen de recurso predeterminado si no hay imagen
        }
        textViewNombre.setText(nombreComun);
        textViewFecha.setText(fechaGuardado);
        textViewTipoPlaga.setText(tipoPlaga);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantasActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
