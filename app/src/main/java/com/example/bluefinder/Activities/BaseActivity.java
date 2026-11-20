package com.example.bluefinder.Activities;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluefinder.Controllers.AuthController;
import com.example.bluefinder.Controllers.UsuarioController;
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

    //Método para cerrar sesión: la lógica la hace el controlador, la Activity solo navega
    protected void logOut() {
        new AuthController().cerrarSesion();
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

    //Guarda el usuario en la base local y en la online: lo hace el controlador
    protected void guardarUsuario(FirebaseUser user) {
        new UsuarioController(this).registrarUsuario(user);
    }
}