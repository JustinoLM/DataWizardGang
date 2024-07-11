package com.proyecto.agroinsight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import static com.proyecto.agroinsight.DatabaseManager.COLUMNA_FECHA_GUARDADO;
import static com.proyecto.agroinsight.DatabaseManager.COLUMNA_ID_PLANTA;
import static com.proyecto.agroinsight.DatabaseManager.COLUMNA_IMAGEN;
import static com.proyecto.agroinsight.DatabaseManager.COLUMNA_NOMBRE_COMUN;
import static com.proyecto.agroinsight.DatabaseManager.COLUMNA_TIPO_PLAGA;
import static com.proyecto.agroinsight.DatabaseManager.TABLA_PLANTAS;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "MyPrefs";
    public static final String PREF_USER_ID = "userId";
    private PlantaAdapter adapter;
    private DatabaseManager dbManager;
    private Spinner filtroSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new DatabaseManager(this);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        TextView homeOption = findViewById(R.id.homeOption);
        TextView plagasOption = findViewById(R.id.plagasOption);
        TextView nombreUsuario = findViewById(R.id.nombre_usuario);
        LinearLayout perfilLayout = findViewById(R.id.Perfil);
        filtroSpinner = findViewById(R.id.filtro_carrusel);
        RecyclerView listViewPlantas = findViewById(R.id.ListaPlantas);
        EditText inputChatbot = findViewById(R.id.input_chatbot);  // Añadir esta línea para obtener el EditText

        // Establecer homeOption como seleccionado por defecto
        homeOption.setSelected(true);

        FuncionesBasicas.setupNavigationButtons(this, homeOption, plagasOption);

        // Mostrar el nombre del usuario
        FuncionesBasicas.mostrarNombreUsuario(this, sharedPreferences, nombreUsuario);

        // Configurar el PopupMenu para el perfil
        perfilLayout.setOnClickListener(v -> FuncionesBasicas.mostrarMenuPerfil(MainActivity.this, v, sharedPreferences));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, 1);
        editor.apply();

        List<Planta> plantas = obtenerPlantas(null);  // Inicialmente no se aplica ningún filtro
        adapter = new PlantaAdapter(this, plantas);
        listViewPlantas.setAdapter(adapter);

        // Configurar el RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listViewPlantas.setLayoutManager(layoutManager);

        // Configurar el Spinner con las opciones
        List<String> opciones = new ArrayList<>();
        opciones.add(getString(R.string.spinner_filtro));
        opciones.add(getString(R.string.spinner_alfabetico));
        opciones.add(getString(R.string.spinner_mas_viejo));
        opciones.add(getString(R.string.spinner_mas_nuevo));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtroSpinner.setAdapter(spinnerAdapter);

        // Establecer el Listener para el Spinner
        filtroSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opcionSeleccionada = (String) parent.getSelectedItem();
                if (opcionSeleccionada != null) {
                    switch (opcionSeleccionada) {
                        case "Alfabético":
                            actualizarListaDePlantas("nombre_comun ASC");  // Ordenar alfabéticamente por nombre común
                            break;
                        case "Más viejo":
                            actualizarListaDePlantas("fecha_guardado ASC");  // Ordenar por fecha más antigua
                            break;
                        case "Más nuevo":
                            actualizarListaDePlantas("fecha_guardado DESC");  // Ordenar por fecha más reciente
                            break;
                        default:
                            actualizarListaDePlantas(null);  // Sin filtro
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no se selecciona nada
            }
        });

        // Configurar el OnKeyListener para el EditText
        inputChatbot.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String message = inputChatbot.getText().toString().trim();
                if (!message.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
                    intent.putExtra("user_message", message);  // Enviar el mensaje al ChatbotActivity
                    startActivity(intent);
                }
                return true;  // Indica que el evento ha sido manejado
            }
            return false;  // No ha manejado el evento, el teclado debe manejarlo también
        });

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(listViewPlantas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarListaDePlantas(null);  // Asegúrate de obtener la lista actualizada al reanudar
    }

    private List<Planta> obtenerPlantas(String orden) {
        List<Planta> plantas = new ArrayList<>();
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor;
        if (orden == null) {
            cursor = db.query(TABLA_PLANTAS, null, null, null, null, null, null);
        } else {
            cursor = db.query(TABLA_PLANTAS, null, null, null, null, null, orden);
        }

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int idPlanta = cursor.getInt(cursor.getColumnIndex(COLUMNA_ID_PLANTA));
                @SuppressLint("Range") String nombreComun = cursor.getString(cursor.getColumnIndex(COLUMNA_NOMBRE_COMUN));
                @SuppressLint("Range") byte[] imagen = cursor.getBlob(cursor.getColumnIndex(COLUMNA_IMAGEN));
                @SuppressLint("Range") String fechaGuardado = cursor.getString(cursor.getColumnIndex(COLUMNA_FECHA_GUARDADO));
                @SuppressLint("Range") String tipoPlaga = cursor.getString(cursor.getColumnIndex(COLUMNA_TIPO_PLAGA));

                plantas.add(new Planta(idPlanta, nombreComun, imagen, fechaGuardado, tipoPlaga));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return plantas;
    }

    private void actualizarListaDePlantas(String orden) {
        List<Planta> plantas = obtenerPlantas(orden);
        if (adapter != null) {
            adapter.setPlantas(plantas);
            adapter.notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
        }
    }
}

