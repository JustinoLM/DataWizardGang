package com.proyecto.agroinsight;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FuncionesBasicas {

    public static void crearMensaje(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

    /* Como se hace la llamada
    private void crearMensaje(String mensaje) {
        FuncionesBasicas.crearMensaje(this, mensaje);
        }
    */
    public static void mostrarMenuPerfil(final Context context, View v, final SharedPreferences sharedPreferences) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout) {
                logOff(context, sharedPreferences);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public static void logOff(Context context, SharedPreferences sharedPreferences) {
        // Eliminar el ID de usuario almacenado
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(MainActivity.PREF_USER_ID);
        editor.apply();

        // Mostrar mensaje y redirigir a la pantalla de inicio de sesión
        String mensaje = context.getString(R.string.logoff_mensaje);
        crearMensaje(context, mensaje);

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).finish();
        }
    }

    /*
    Como se hace la llamada

    // Configurar el PopupMenu para el perfil
        perfilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FuncionesBasicas.mostrarMenuPerfil(MainActivity.this, v, sharedPreferences);
            }
        });

    * */
    public static void mostrarNombreUsuario(AppCompatActivity activity, SharedPreferences sharedPreferences, TextView nombreUsuario) {
        int userId = sharedPreferences.getInt(MainActivity.PREF_USER_ID, -1);
        Log.d("FuncionesBasicas", "User ID: " + userId);
        String nombre;
        try (DatabaseManager databaseManager = new DatabaseManager(activity)) {
            nombre = databaseManager.obtenerNombreUsuarioConId(userId);
        }

        if (nombre != null) {
            nombreUsuario.setText(nombre);
        } else {
            nombreUsuario.setText(R.string.usuario_placeholder);
        }
    }

    /*
    FuncionesBasicas.mostrarNombreUsuario(this, sharedPreferences, nombreUsuario);
    */
    public static void setupNavigationButtons(Context context, TextView homeOption, TextView plagasOption) {
        homeOption.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });

        plagasOption.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnalizarPlantaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });
    }
}
