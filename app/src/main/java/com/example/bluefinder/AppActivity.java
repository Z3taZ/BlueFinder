package com.example.bluefinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppActivity extends AppCompatActivity {

    private TextView email;
    private MaterialButton btnCerrarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app);

        this.email = findViewById(R.id.txtEmail);
        this.btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        this.btnCerrarSesion.setOnClickListener(v->cerrarSesion());
        //obtenemos el usuario actual (con el que se ha logueado el usuario)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //en caso de que no sea null, obtenemos su email del logueo que haya realizado el usuario
        if(user != null){
            MyShortToast("Iniciada la sesión correctamente");
            this.email.setText(user.getEmail());
        }

        //En caso de que no se haya logueado y haya accedido con invitado
        //Obtenemos el valor booleano asociado a la clave Invitad@. Si no existe, devuelve false
        if(getIntent().getBooleanExtra("Invitad@",false)){
            MyShortToast("Acceso como invitado realizado");
            this.email.setText("Invitad@");
        }
    }

    //Método para cerrar sesión
    private void cerrarSesion() {

        FirebaseAuth.getInstance().signOut();
        MyShortToast("Sesión cerrada correctamente");
        Intent intent = new Intent(AppActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Método para mostrar un toast corto
    private void MyShortToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}