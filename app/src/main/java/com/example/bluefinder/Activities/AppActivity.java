package com.example.bluefinder.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.example.bluefinder.Controllers.AuthController;
import com.example.bluefinder.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

public class AppActivity extends BaseActivity {

    private TextView email;
    private MaterialButton btnCerrarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app);

        //obtenemos las variables del xml
        this.email = findViewById(R.id.txtEmail);
        this.btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        //listener del botón de cerrar sesión
        this.btnCerrarSesion.setOnClickListener(v->logOut());

        //obtenemos el usuario actual (con el que se ha logueado el usuario)
        FirebaseUser user = new AuthController().getUsuarioActual();

        //en caso de que no sea null, obtenemos su email del logueo que haya realizado el usuario
        if(user != null){
            MyShortToast("Iniciada la sesión correctamente");
            this.email.setText(user.getEmail());

            //En caso de que no se haya logueado y haya accedido con invitado
            //Obtenemos el valor booleano asociado a la clave Invitad@. Si no existe, devuelve false
        }else if(getIntent().getBooleanExtra("Invitad@",false)){
            MyShortToast("Acceso como invitado realizado");
            this.email.setText("Invitad@");
        }
    }
}