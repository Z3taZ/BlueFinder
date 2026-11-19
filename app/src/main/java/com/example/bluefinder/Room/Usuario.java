package com.example.bluefinder.Room;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.annotations.NotNull;
@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey
    @NonNull
    private String uid;      // identificador único de Firebase

    private String email;

    // Datos opcionales para el cálculo de agua
    private Integer edad;
    private Double altura;   // en cm
    private String sexo;

    public Usuario(@NonNull String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    //Room necesita un constructor sin argumentos. Pero hay que tener cuidado con esto
    //ya que al haber más de un cosntructor room no sabrá cuál usar. Así que usamos la etiqueta
    //ignore para ignorar este constructor
    @Ignore
    public Usuario() {}

    @NonNull
    public String getUid() { return uid; }
    public void setUid(@NonNull String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
}