package com.example.bluefinder.Activities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluefinder.Room.AppDataBase;
import com.example.bluefinder.Room.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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


    //Método para guardar el usuario en la base de datos local (Room)
    protected void guardarUsuarioLocal(FirebaseUser user) {

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

}
