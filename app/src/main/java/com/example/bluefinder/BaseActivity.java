package com.example.bluefinder;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

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
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void goToApp() {
        Intent intent = new Intent(this,AppActivity.class);
        startActivity(intent);
        finish();
    }



}
