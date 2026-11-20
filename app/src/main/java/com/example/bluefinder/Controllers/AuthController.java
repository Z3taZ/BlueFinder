package com.example.bluefinder.Controllers;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthController {

    private static final String TAG = "AuthController";

    // La vista implementa esta interfaz para enterarse del resultado
    public interface AuthListener {
        void onExito(FirebaseUser user);
        void onError(String mensaje);
    }

    private final FirebaseAuth mAuth;

    public AuthController() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getUsuarioActual() {
        return mAuth.getCurrentUser();
    }

    public void cerrarSesion() {
        mAuth.signOut();
    }

    // ---------- Validaciones: devuelven el mensaje de error, o null si todo está bien ----------

    public String validarEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return "El campo Email no puede estar vacío";
        }
        return null;
    }

    public String validarPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return "El campo Contraseña no puede estar vacío";
        }
        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        return null;
    }

    public String validarConfirmacion(String password, String confirmacion) {
        if (!password.equals(confirmacion)) {
            return "Las contraseñas no coinciden";
        }
        return null;
    }

    // ---------- Operaciones con Firebase Auth ----------

    public void login(String email, String password, final AuthListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            listener.onExito(mAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            listener.onError("Credenciales incorrectas. Inténtelo de nuevo");
                        }
                    }
                });
    }

    public void registrar(String email, String password, final AuthListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            listener.onExito(mAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            listener.onError("No te has podido registrar como usuario");
                        }
                    }
                });
    }

    // Último paso del login con Google: el token ya lo ha obtenido la Activity
    public void loginConGoogle(String idToken, final AuthListener listener) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            listener.onExito(mAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            listener.onError("Fallo en la autenticación con Firebase");
                        }
                    }
                });
    }
}