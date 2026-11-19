package com.example.bluefinder.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.bluefinder.Room.UsuarioDao;

@Database(entities = {Usuario.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    // Room implementará este método por al compilar
    public abstract UsuarioDao usuarioDao();

    // Única instancia de la base de datos en toda la app
    private static AppDataBase instancia;


    public static synchronized AppDataBase getInstance(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDataBase.class,
                            "bluefinder_db")
                    .build();
        }
        return instancia;
    }
}
