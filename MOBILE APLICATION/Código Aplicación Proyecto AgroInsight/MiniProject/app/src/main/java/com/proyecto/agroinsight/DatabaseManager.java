package com.proyecto.agroinsight;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "plantas.db";
    private static final int DATABASE_VERSION = 6;


    private static final String TABLA_USUARIOS = "usuarios";
    private static final String COLUMNA_ID_USUARIO = "id_usuario";
    private static final String COLUMNA_NOMBRE_USUARIO = "nombre_usuario";
    private static final String COLUMNA_CONTRASENA_USUARIO = "contraseña";


    public static final String TABLA_PLANTAS = "plantas";
    public static final String COLUMNA_ID_PLANTA = "id_planta";
    public static final String COLUMNA_NOMBRE_COMUN = "nombre_comun";
    public static final String COLUMNA_IMAGEN = "imagen";
    public static final String COLUMNA_FECHA_GUARDADO = "fecha_guardado";
    public static final String COLUMNA_TIPO_PLAGA = "tipo_plaga";



    private static final String TABLA_USUARIOS_PLANTAS = "usuarios_plantas";
    private static final String COLUMNA_FECHA_ASIGNACION = "fecha_asignacion";


    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREAR_TABLA_USUARIOS = "CREATE TABLE IF NOT EXISTS " + TABLA_USUARIOS + "("
                + COLUMNA_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMNA_NOMBRE_USUARIO + " TEXT,"
                + COLUMNA_CONTRASENA_USUARIO + " TEXT" + ")";
        db.execSQL(CREAR_TABLA_USUARIOS);


        String CREAR_TABLA_PLANTAS = "CREATE TABLE IF NOT EXISTS " + TABLA_PLANTAS + "("
                + COLUMNA_ID_PLANTA + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMNA_NOMBRE_COMUN + " TEXT,"
                + COLUMNA_IMAGEN + " BLOB,"
                + COLUMNA_FECHA_GUARDADO + " TEXT,"
                + COLUMNA_ID_USUARIO + " INTEGER,"
                + "FOREIGN KEY(" + COLUMNA_ID_USUARIO + ") REFERENCES " + TABLA_USUARIOS + "(" + COLUMNA_ID_USUARIO + ")" + ")";
        db.execSQL(CREAR_TABLA_PLANTAS);


        String CREAR_TABLA_USUARIOS_PLANTAS = "CREATE TABLE IF NOT EXISTS " + TABLA_USUARIOS_PLANTAS + "("
                + COLUMNA_ID_USUARIO + " INTEGER,"
                + COLUMNA_ID_PLANTA + " INTEGER,"
                + COLUMNA_FECHA_ASIGNACION + " TEXT,"
                + "PRIMARY KEY (" + COLUMNA_ID_USUARIO + ", " + COLUMNA_ID_PLANTA + "),"
                + "FOREIGN KEY(" + COLUMNA_ID_USUARIO + ") REFERENCES " + TABLA_USUARIOS + "(" + COLUMNA_ID_USUARIO + "),"
                + "FOREIGN KEY(" + COLUMNA_ID_PLANTA + ") REFERENCES " + TABLA_PLANTAS + "(" + COLUMNA_ID_PLANTA + ")" + ")";
        db.execSQL(CREAR_TABLA_USUARIOS_PLANTAS);
        checkAndAddColumn(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLA_PLANTAS + " ADD COLUMN " + COLUMNA_ID_USUARIO + " INTEGER;");
        }
        if (oldVersion < 3) {
            String CREAR_TABLA_USUARIOS_PLANTAS = "CREATE TABLE IF NOT EXISTS " + TABLA_USUARIOS_PLANTAS + "("
                    + COLUMNA_ID_USUARIO + " INTEGER,"
                    + COLUMNA_ID_PLANTA + " INTEGER,"
                    + COLUMNA_FECHA_ASIGNACION + " TEXT,"
                    + "PRIMARY KEY (" + COLUMNA_ID_USUARIO + ", " + COLUMNA_ID_PLANTA + "),"
                    + "FOREIGN KEY(" + COLUMNA_ID_USUARIO + ") REFERENCES " + TABLA_USUARIOS + "(" + COLUMNA_ID_USUARIO + "),"
                    + "FOREIGN KEY(" + COLUMNA_ID_PLANTA + ") REFERENCES " + TABLA_PLANTAS + "(" + COLUMNA_ID_PLANTA + ")" + ")";
            db.execSQL(CREAR_TABLA_USUARIOS_PLANTAS);
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLA_PLANTAS + " ADD COLUMN " + COLUMNA_TIPO_PLAGA + " TEXT;");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLA_PLANTAS + " ADD COLUMN " + COLUMNA_TIPO_PLAGA + " TEXT;");
        }
        checkAndAddColumn(db);
    }

    public boolean existeUsuario(String nombreUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLA_USUARIOS + " WHERE " + COLUMNA_NOMBRE_USUARIO + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreUsuario});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }


    // Método para insertar un nuevo usuario en la base de datos
    public long insertarUsuario(String nombreUsuario, String contrasena) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_USUARIO, nombreUsuario);
        values.put(COLUMNA_CONTRASENA_USUARIO, contrasena);
        long id = db.insert(TABLA_USUARIOS, null, values);
        db.close();
        return id;
    }


    @SuppressLint("Range")
    public int validarUsuario(String nombreUsuario, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;


        String query = "SELECT " + COLUMNA_ID_USUARIO + " FROM " + TABLA_USUARIOS +
                " WHERE " + COLUMNA_NOMBRE_USUARIO + "=? AND " + COLUMNA_CONTRASENA_USUARIO + "=?";


        Cursor cursor = db.rawQuery(query, new String[]{nombreUsuario, contrasena});


        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMNA_ID_USUARIO));
        }


        cursor.close();
        return userId;
    }


    @SuppressLint("Range")
    public String obtenerNombreUsuarioConId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nombreUsuario = null;


        String query = "SELECT " + COLUMNA_NOMBRE_USUARIO + " FROM " + TABLA_USUARIOS +
                " WHERE " + COLUMNA_ID_USUARIO + "=?";


        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});


        if (cursor.moveToFirst()) {
            nombreUsuario = cursor.getString(cursor.getColumnIndex(COLUMNA_NOMBRE_USUARIO));
        }


        cursor.close();
        return nombreUsuario;
    }

    public long insertarPlanta(String nombreComun, byte[] imagen, String fechaGuardado, String tipoPlaga) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_COMUN, nombreComun);
        values.put(COLUMNA_IMAGEN, imagen);
        values.put(COLUMNA_FECHA_GUARDADO, fechaGuardado);
        values.put(COLUMNA_TIPO_PLAGA, tipoPlaga);
        long id = db.insert(TABLA_PLANTAS, null, values);
        db.close();
        return id;
    }

    public void checkAndAddColumn(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLA_PLANTAS + ")", null);
        boolean columnExists = false;
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
            if (columnName.equals(COLUMNA_TIPO_PLAGA)) {
                columnExists = true;
                break;
            }
        }
        cursor.close();

        if (!columnExists) {
            db.execSQL("ALTER TABLE " + TABLA_PLANTAS + " ADD COLUMN " + COLUMNA_TIPO_PLAGA + " TEXT;");
        }
    }

}
