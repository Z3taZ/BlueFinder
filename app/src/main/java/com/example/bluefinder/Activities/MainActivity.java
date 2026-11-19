package com.example.bluefinder.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends BaseActivity {

    // Variables Firebase y CredentialManager
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    // Resto de variables del xml
    private MaterialButton btnLogin, btnInvitado, btnGoogle, btnRegistro;
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

        // Inicializamos Firebase Auth y CredentialManager
        this.mAuth = FirebaseAuth.getInstance();
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
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithCredential:success");
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        MyLongToast("Fallo en la autenticación con Firebase");
                        updateUI(null);
                    }
                });
    }

    private void loginInvitado() {
        Intent intent = new Intent(MainActivity.this, AppActivity.class);
        intent.putExtra("Invitad@", true);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String email = this.etEmail.getText().toString().trim();
        String password = this.etPassword.getText().toString().trim();

        // Validamos que los campos no estén vacíos y que sean compatibles
        if (!validateForm(email, password)) {
            return;
        }

        // Iniciamos sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // En caso exitoso
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithEmail:success");
                        updateUI(user);
                    // En caso fallido
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        MyLongToast("Credenciales incorrectas. Inténtelo de nuevo");
                    }
                });
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
        if (TextUtils.isEmpty(email)) {
            this.etEmail.setError("El campo no puede estar vacío");
            MyLongToast("El campo Email no puede estar vacío");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            this.etPassword.setError("El campo no puede estar vacío");
            MyLongToast("El campo Contraseña no puede estar vacío");
            return false;
        }

        if (password.length() < 6) {
            this.etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            MyLongToast("La contraseña debe tener al menos 6 caracteres");
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
