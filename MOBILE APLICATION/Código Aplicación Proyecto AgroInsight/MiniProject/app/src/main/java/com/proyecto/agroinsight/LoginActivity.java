package com.proyecto.agroinsight;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextView textViewError;
    private EditText editTextUsername, editTextPassword;
    private SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "MyPrefs";
    public static final String PREF_USER_ID = "userId";
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        databaseManager = new DatabaseManager(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        editTextUsername = findViewById(R.id.editTextUsuario);
        editTextPassword = findViewById(R.id.editTextContrasena);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegistrar);
        textViewError = findViewById(R.id.textViewError);

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrarseActivity.class)));
    }

    private void loginUser() {
        String usuario = editTextUsername.getText().toString().trim();
        String contrasena = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(contrasena)) {
            mostrarMensaje("Por favor, complete todos los campos");
            return;
        }

        int userId = databaseManager.validarUsuario(usuario, contrasena);
        if (userId != -1) {
            guardarIdUsuario(userId);
            viajarAlMain();
        } else {
            mostrarMensaje("Credenciales incorrectas");
        }
    }

    private void guardarIdUsuario(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, userId);
        editor.apply();
    }

    private void viajarAlMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void mostrarMensaje(String message) {
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(message);
    }
}
