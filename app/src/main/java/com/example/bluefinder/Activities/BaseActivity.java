package com.example.bluefinder.Activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bluefinder.Room.AppDataBase;
import com.example.bluefinder.Room.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Clase que actuará como padre de las otras activities con el objetivo de
//heredar métodos comunes que estén relacionados con las activities en su parte gráfica
public class BaseActivity extends AppCompatActivity {

    //Método para mostrar un toast largo
    protected void MyLongToast( String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    //Método para mostrar un toast corto
    protected void MyShortToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    //Método para cerrar sesión
    protected void logOut() {

        FirebaseAuth.getInstance().signOut();
        MyShortToast("Sesión cerrada correctamente");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void goToApp() {
        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
        finish();
    }

    //Método único que guarda el usuario en la base local (Room) y en la online (Firebase)
    protected void guardarUsuario(FirebaseUser user) {

        // Si no hay usuario (por ejemplo, modo invitado) no hay nada que guardar
        if (user == null) {
            return;
        }

        guardarUsuarioLocal(user);
        guardarUsuarioOnline(user);
    }

    //Método para guardar el usuario en la base de datos local (Room)
    private void guardarUsuarioLocal(FirebaseUser user) {

        // Si no hay usuario no hay nada que guardar
        if (user == null) {
            return;
        }

        // Copiamos los datos en variables final para poder usarlas dentro del hilo
        final String uid = user.getUid();
        final String email = user.getEmail();
        final Context appContext = getApplicationContext();

        // Guardamos el usuario en la base de datos local (Room) con otro hilo ya que con el main thread
        // se bloquearía la aplicación
        new Thread(new Runnable() {
            @Override
            public void run() {
                Usuario usuario = new Usuario(uid, email);
                AppDataBase.getInstance(appContext).usuarioDao().insertar(usuario);
            }
        }).start();
    }

    //Método para crear el usuario en Realtime Database si todavía no existe
    //Firebase ejecuta todo esto en segundo plano, así que aquí no hace falta Thread
    private void guardarUsuarioOnline(FirebaseUser user) {

        if (user == null) {
            return;
        }

        final String uid = user.getUid();
        final String email = user.getEmail();

        // Referencia al nodo usuarios/<uid> del json de la bd online de firebase real time
        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid);

        // Leemos una sola vez para ver si ya existe
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //si no existe pues lo creamos
                if (!snapshot.exists()) {
                    ref.setValue(new Usuario(uid, email));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("BlueFinder", "Error al leer usuario online", error.toException());
            }
        });
    }

}
