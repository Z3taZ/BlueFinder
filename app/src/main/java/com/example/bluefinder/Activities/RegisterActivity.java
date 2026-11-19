package com.example.bluefinder.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bluefinder.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends BaseActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister,btnGoBack;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.mAuth = FirebaseAuth.getInstance();
        //mapeo las variables con el xml
        this.etEmail = findViewById(R.id.inEmailRegistro);
        this.etPassword = findViewById(R.id.inPasswordRegistro);
        this.etConfirmPassword = findViewById(R.id.inConfirmarPassword);
        this.btnRegister = findViewById(R.id.btnCrearCuenta);
        this.btnGoBack = findViewById(R.id.btnVolverLogin);

        //Creamos los listeners
        this.btnRegister.setOnClickListener(v->registerUser());
        this.btnGoBack.setOnClickListener(v->goBack());

    }


    private void registerUser() {

        String email = this.etEmail.getText().toString().trim();
        String password = this.etPassword.getText().toString().trim();
        String confirmPassword = this.etConfirmPassword.getText().toString().trim();

        //Validamos que los campos no esten vacios y que sean compatibles
        if(!validateForm(email,password,confirmPassword)){return;}

        //Registramos al usuario con Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "createUserWithEmail:success");
                        guardarUsuarioLocal(user); // Guardamos el usuario en la base de datos local
                        MyShortToast("Te has registrado correctamente");
                        goToApp();
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        MyLongToast("No te has podido registrar como usuario");
                    }
                });
    }

    //Método para validar los campos del RegisterActivity
    private boolean validateForm(String email, String password,String confirmPassword) {

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

        //verificamos que la contraseña y la confirmación de la contraseña sean iguales
        if(!password.equals(confirmPassword)){
            this.etConfirmPassword.setError("Las contraseñas no coinciden");
            MyLongToast("Las contraseñas no coinciden");
            return false;
        }



        return true;
    }

    //Método para volver a MainActivity sin cerrar sesión ya que no hay ninguna sesión iniciada
    private void goBack() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}