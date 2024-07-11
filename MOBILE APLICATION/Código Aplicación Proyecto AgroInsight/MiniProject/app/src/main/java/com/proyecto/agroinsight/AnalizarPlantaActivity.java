package com.proyecto.agroinsight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.agroinsight.R;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnalizarPlantaActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefs";
    public static final String PREF_USER_ID = "userId";
    private SharedPreferences sharedPreferences;
    private static final int SELECT_IMAGE = 1;
    private EditText editTextNombre;
    private ImageView imageViewPlanta;
    private TextView textViewPrediccion;
    private Button buttonGuardarPlanta;
    private Bitmap imagenSeleccionada;
    private Interpreter modelo;
    private String tipoPlaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_planta);

        buttonGuardarPlanta = findViewById(R.id.buttonGuardarPlanta);
        Button buttonAnalizarPlanta = findViewById(R.id.buttonAnalizarPlanta);
        Button buttonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagen);

        editTextNombre = findViewById(R.id.editTextNombre);

        imageViewPlanta = findViewById(R.id.imageViewPlanta);
        LinearLayout perfilLayout = findViewById(R.id.Perfil);

        textViewPrediccion = findViewById(R.id.textViewPrediccion);
        TextView homeOption = findViewById(R.id.homeOption);
        TextView plagasOption = findViewById(R.id.plagasOption);
        TextView nombreUsuario = findViewById(R.id.nombre_usuario);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Establecer homeOption como seleccionado por defecto
        plagasOption.setSelected(true);

        FuncionesBasicas.setupNavigationButtons(this, homeOption, plagasOption);

        // Mostrar el nombre del usuario
        FuncionesBasicas.mostrarNombreUsuario(this, sharedPreferences, nombreUsuario);

        // Configurar el PopupMenu para el perfil
        perfilLayout.setOnClickListener(v -> FuncionesBasicas.mostrarMenuPerfil(AnalizarPlantaActivity.this, v, sharedPreferences));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, 1);
        editor.apply();

        // Cargar el modelo TFLite
        try {
            modelo = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar el modelo", Toast.LENGTH_SHORT).show();
        }

        buttonSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), SELECT_IMAGE);
        });

        buttonAnalizarPlanta.setOnClickListener(v -> {
            if (imagenSeleccionada != null) {
                tipoPlaga = realizarPrediccion(imagenSeleccionada);
                // Mostrar el botón para guardar la planta
                buttonGuardarPlanta.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Debe seleccionar una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });

        buttonGuardarPlanta.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString();
            if (nombre.isEmpty() || imagenSeleccionada == null) {
                Toast.makeText(this, "Debe ingresar un nombre y seleccionar una imagen", Toast.LENGTH_SHORT).show();
            } else {
                guardarPlanta(nombre, imagenSeleccionada, tipoPlaga);
            }
        });
        buttonGuardarPlanta.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                imagenSeleccionada = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageViewPlanta.setImageBitmap(imagenSeleccionada);
                imageViewPlanta.setVisibility(ImageView.VISIBLE);
                Toast.makeText(this, "Imagen seleccionada con éxito", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarPlanta(String nombreComun, Bitmap imagen, String tipoPlaga) {

        // Convertir la imagen a un arreglo de bytes
        byte[] imagenBytes = getBitmapAsByteArray(imagen);


        // Obtener la fecha actual
        String fechaGuardado = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        try {
            // Insertar la planta en la base de datos
            DatabaseManager dbManager = new DatabaseManager(this);
            long id = dbManager.insertarPlanta(nombreComun, imagenBytes, fechaGuardado, this.tipoPlaga);  // Pasar tipoPlaga al método

            // Verificar el resultado de la inserción
            if (id > 0) {
                Toast.makeText(this, "Planta guardada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar la planta", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Excepción al guardar la planta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



    // Cargar el archivo del modelo TFLite desde la carpeta de assets
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("modelo_final.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Método para analizar la planta usando el modelo TFLite
    private String realizarPrediccion(Bitmap bitmap) {
        try {
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(bitmap);
            float[][] output = new float[1][11];
            modelo.run(inputBuffer, output);

            // Mostrar las probabilidades en Logcat
            for (int i = 0; i < output[0].length; i++) {
                Log.d("Predicción", "Probabilidad para la clase " + i + ": " + output[0][i]);
            }
            int[] topIndices = getTopTwoIndices(output[0]);
            int index = topIndices[1];  // Usar el segundo índice

            String[] clases = {"Bacterial_spot", "Black_Measles", "Black_rot", "Desconocido", "Early_blight", "Late_blight", "Leaf_scorch", "Rust", "Scab", "Sin plaga", "Spot"};
            String resultado = getClaseEnEspanol(clases[index]);

            textViewPrediccion.setText(String.format("%s%s", getString(R.string.prediccion), resultado));
            textViewPrediccion.setVisibility(View.VISIBLE);
            return resultado;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en la predicción: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return "Error en la predicción";
        }
    }



    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);  // Cambiado a formato JPEG
        return outputStream.toByteArray();
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 180 * 180 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[180 * 180];

        // Redimensionar la imagen a 180x180
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, true);
        resizedBitmap.getPixels(intValues, 0, 180, 0, 0, 180, 180);

        for (int pixel : intValues) {
            // Normalizar los valores de los píxeles a [0, 1]
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);  // R
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);   // G
            byteBuffer.putFloat((pixel & 0xFF) / 255.0f);          // B
        }
        return byteBuffer;
    }

    private String getClaseEnEspanol(String claseIngles) {
        switch (claseIngles) {
            case "Bacterial_spot":
                return "Manchas bacterianas";
            case "Black_Measles":
                return "Sarampión negro";
            case "Black_rot":
                return "Podredumbre negra";
            case "Desconocido":
                return "Desconocido";
            case "Early_blight":
                return "Tizón temprano";
            case "Late_blight":
                return "Tizón tardía";
            case "Leaf_scorch":
                return "Quemadura de hojas";
            case "Rust":
                return "Roya";
            case "Scab":
                return "Costra";
            case "Sin plaga":
                return "Sin plaga";
            case "Spot":
                return "Mancha";
            default:
                return "Clase desconocida";
        }
    }

    private int[] getTopTwoIndices(float[] probabilities) {
        int firstIndex = 0;
        int secondIndex = -1;
        float firstMax = -1.0f;
        float secondMax = -1.0f;

        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > firstMax) {
                // Actualiza el segundo mayor
                secondMax = firstMax;
                secondIndex = firstIndex;

                // Actualiza el mayor
                firstMax = probabilities[i];
                firstIndex = i;
            } else if (probabilities[i] > secondMax && probabilities[i] < firstMax) {
                // Actualiza el segundo mayor
                secondMax = probabilities[i];
                secondIndex = i;
            }
        }

        return new int[]{firstIndex, secondIndex};
    }

}
