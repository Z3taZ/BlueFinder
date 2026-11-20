package com.example.bluefinder.Controllers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bluefinder.Room.AppDataBase;
import com.example.bluefinder.Room.Usuario;
import com.example.bluefinder.Room.UsuarioDao;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsuarioController {

    private static final String TAG = "UsuarioController";

    private final UsuarioDao dao;

    public UsuarioController(Context context) {
        this.dao = AppDataBase.getInstance(context.getApplicationContext()).usuarioDao();
    }

    // Se llama al iniciar sesión o registrarse: crea al usuario en Room y en Firebase si no existe
    public void registrarUsuario(FirebaseUser user) {
        if (user == null) {
            return;
        }

        final String uid = user.getUid();
        final String email = user.getEmail();

        // Room (IGNORE: si ya existe, no pisa sus datos)
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao.insertar(new Usuario(uid, email));
            }
        }).start();

        // Firebase: solo se crea el nodo si todavía no existe
        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Si no existe, lo crea
                if (!snapshot.exists()) {
                    ref.setValue(new Usuario(uid, email));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Error al leer usuario online", error.toException());
            }
        });
    }
}