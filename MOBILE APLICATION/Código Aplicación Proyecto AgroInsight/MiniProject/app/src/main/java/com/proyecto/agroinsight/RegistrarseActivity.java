package com.proyecto.agroinsight;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarseActivity extends AppCompatActivity {

    private EditText editTextUsuario, editTextContrasena;
    private DatabaseManager databaseManager;
    private static final int MAX_USERNAME_LENGTH = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        editTextContrasena = findViewById(R.id.editTextContrasena);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        Button buttonRegistrar = findViewById(R.id.buttonRegistrar);
        databaseManager = new DatabaseManager(this);


        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        String usuario = editTextUsuario.getText().toString().trim();
        String contrasena = editTextContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(contrasena)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuario.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(this, "El nombre de usuario no puede tener más de 14 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseManager.existeUsuario(usuario)) {
            Toast.makeText(this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        long resultado = databaseManager.insertarUsuario(usuario, contrasena);
        if (resultado != -1) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(RegistrarseActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        }
    }
}