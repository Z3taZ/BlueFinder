package com.example.bluefinder.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bluefinder.R;
import com.example.bluefinder.Controllers.AuthController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends BaseActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister,btnGoBack;

    private AuthController authController;
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

        this.authController = new AuthController();

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

        authController.registrar(email, password, new AuthController.AuthListener() {
            @Override
            public void onExito(FirebaseUser user) {
                guardarUsuario(user); // Guardamos el usuario en la base de datos local y online
                MyShortToast("Te has registrado correctamente");
                goToApp();
            }

            @Override
            public void onError(String mensaje) {
                MyLongToast(mensaje);
            }
        });
    }

    //Método para validar los campos del RegisterActivity
    private boolean validateForm(String email, String password, String confirmPassword) {

        String errorEmail = authController.validarEmail(email);
        if (errorEmail != null) {
            this.etEmail.setError(errorEmail);
            MyLongToast(errorEmail);
            return false;
        }

        String errorPassword = authController.validarPassword(password);
        if (errorPassword != null) {
            this.etPassword.setError(errorPassword);
            MyLongToast(errorPassword);
            return false;
        }

        String errorConfirmacion = authController.validarConfirmacion(password, confirmPassword);
        if (errorConfirmacion != null) {
            this.etConfirmPassword.setError(errorConfirmacion);
            MyLongToast(errorConfirmacion);
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