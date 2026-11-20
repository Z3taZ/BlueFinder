package com.example.bluefinder.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

    @Dao
    public interface UsuarioDao {

        /*
   Con REPLACE, cada vez que el usuario inicie sesión y hagas insertar, Room borraría la fila anterior
   y la crearía de nuevo, perdiendo su edad, altura y sexo.
   Con IGNORE, si el email ya existe no hace nada. Así si tiene la misma pk no hay peligro.
    */
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insertar(Usuario usuario);

        @Update
        void actualizar(Usuario usuario);

        @Query("SELECT * FROM usuarios WHERE uid = :uid")
        Usuario buscarPorUid(String uid);

        @Query("DELETE FROM usuarios WHERE uid = :uid")
        void borrarPorUid(String uid);
    }