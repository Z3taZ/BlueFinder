package com.example.bluefinder;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Creamos las variables necesarias
    //Variable Firebase
    private FirebaseAuth mAuth;

    //Resto de variables del xml
    private MaterialButton btnLogin,btnInvitado,btnGoogle,btnRegistro;
    private TextInputEditText etEmail, etPassword;


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MainActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializamos Firebase Auth
        this.mAuth = FirebaseAuth.getInstance();

        //Mapeo con los IDs de activity_main.xml
        this.btnGoogle = findViewById(R.id.btnGoogle);
        this.btnInvitado = findViewById(R.id.btnInvitado);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnRegistro = findViewById(R.id.btnRegistro);
        this.etEmail = findViewById(R.id.inEmail);
        this.etPassword = findViewById(R.id.inPassword);

        //listeners de los botones
        this.btnLogin.setOnClickListener(v->loginUser());
        this.btnInvitado.setOnClickListener(v->loginInvitado());
    }

    private void loginInvitado() {

        Intent intent = new Intent(MainActivity.this,AppActivity.class);
        intent.putExtra("Invitad@",true);
        startActivity(intent);
        finish();
    }

    private void loginUser() {

        String email = this.etEmail.getText().toString().trim();
        String password = this.etPassword.getText().toString().trim();

        //Validamos que los campos no esten vacios y que sean compatibles
        if(!validateForm(email,password)){return;}

        //iniciamos sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    //En caso exitoso
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithEmail:success");
                        updateUI(user);

                    //En caso fallido
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        MyLongToast("Credenciales incorrectas. Inténtelo de nuevo");
                    }
                });

    }

    //Método para Actualizar la UI en función de si el usuario está logueado o no
    private void updateUI(FirebaseUser user) {
        //Pasamos a la pantalla con las credenciales ya logueadas
        if(user!=null){
            Intent intent = new Intent(MainActivity.this,AppActivity.class);
            startActivity(intent);
            finish();

            //El usuario debe loguearse
        }else{
            MyShortToast("Debes identificarte");
        }
    }

    //Método para validar los campos
    private boolean validateForm(String email, String password) {

        //verificamos que email no esté vacío
        if(TextUtils.isEmpty(email)){
            this.etEmail.setError("El campo no puede estar vacío");
            MyLongToast("El campo Email no puede estar vacío");
            return false;
        }


        //verificamos que contraseña no esté vacío
        if(TextUtils.isEmpty(password)){
            this.etPassword.setError("El campo no puede estar vacío");
            MyLongToast("El campo Contraseña no puede estar vacío");
            return false;
        }

        //verificamos que la longitud de la contraseña sea mayor a 6
        if(password.length()<6){
            this.etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            MyLongToast("La contraseña debe tener al menos 6 caracteres");
            return false;
        }


        return true;
    }

    //Método para mostrar un toast largo
    private void MyLongToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    //Método para mostrar un toast corto
    private void MyShortToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}

