package com.example.bluefinder.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.bluefinder.R;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.example.bluefinder.Controllers.AuthController;

public class MainActivity extends BaseActivity {

    // Variables ControladorAuth y CredentialManager
    private AuthController authController;
    private CredentialManager credentialManager;

    // Resto de variables del xml
    private MaterialButton btnLogin, btnInvitado, btnGoogle, btnRegistro;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onStart() {
        super.onStart();

        // Comprobamos si el usuario está logueado y lo actualizamos en la UI
        updateUI(authController.getUsuarioActual());
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

        // Inicializamos Controlador y CredentialManager
        this.authController = new AuthController();
        this.credentialManager = CredentialManager.create(this);

        // Mapeo con los IDs de activity_main.xml
        this.btnGoogle = findViewById(R.id.btnGoogle);
        this.btnInvitado = findViewById(R.id.btnInvitado);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnRegistro = findViewById(R.id.btnRegistro);
        this.etEmail = findViewById(R.id.inEmail);
        this.etPassword = findViewById(R.id.inPassword);

        // Listeners de los botones
        this.btnLogin.setOnClickListener(v -> loginUser());
        this.btnInvitado.setOnClickListener(v -> loginInvitado());
        this.btnRegistro.setOnClickListener(v -> goToRegister());
        this.btnGoogle.setOnClickListener(v -> lanzarGoogleSignIn());
    }

    private void lanzarGoogleSignIn() {
        String webClientId = getString(R.string.default_web_client_id);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                getMainExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        procesarResultadoCredential(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Error en CredentialManager: " + e.getMessage(), e);
                        MyLongToast("Error al conectar con Google: " + e.getLocalizedMessage());
                    }
                }
        );
    }

    private void procesarResultadoCredential(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential &&
                credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            try {
                GoogleIdTokenCredential googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.getData());

                String idToken = googleIdTokenCredential.getIdToken();
                firebaseAuthConGoogle(idToken);

            } catch (Exception e) {
                Log.e(TAG, "Error al extraer token de Google", e);
                MyShortToast("Error al procesar credenciales de Google");
            }
        }
    }

    private void firebaseAuthConGoogle(String idToken) {
        authController.loginConGoogle(idToken, new AuthController.AuthListener() {
            @Override
            public void onExito(FirebaseUser user) {
                updateUI(user);
            }

            @Override
            public void onError(String mensaje) {
                MyLongToast(mensaje);
            }
        });
    }

    private void loginUser() {
        String email = this.etEmail.getText().toString().trim();
        String password = this.etPassword.getText().toString().trim();

        // Validamos que los campos no estén vacíos y que sean compatibles
        if (!validateForm(email, password)) {
            return;
        }

        authController.login(email, password, new AuthController.AuthListener() {
            @Override
            public void onExito(FirebaseUser user) {
                updateUI(user);
            }

            @Override
            public void onError(String mensaje) {
                MyLongToast(mensaje);
            }
        });
    }

    private void loginInvitado() {
        Intent intent = new Intent(MainActivity.this, AppActivity.class);
        intent.putExtra("Invitad@", true);
        startActivity(intent);
        finish();
    }



    // Método para actualizar la UI en función de si el usuario está logueado o no
    private void updateUI(FirebaseUser user) {
        // Pasamos a la pantalla con las credenciales ya logueadas
        if (user != null) {
            guardarUsuario(user); // Guardamos el usuario en la base de datos local y online
            goToApp();
        } else {
            MyShortToast("Debes identificarte");
        }
    }

    // Método para validar los campos del MainActivity
    private boolean validateForm(String email, String password) {
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

        return true;
    }

    //Método que en un futuro se pondrá en BaseActivity para que otras activities lo hereden
    private void goToRegister() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
