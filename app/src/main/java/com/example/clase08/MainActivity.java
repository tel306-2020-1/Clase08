package com.example.clase08;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("edad",31);
        editor.putBoolean("intercambio",true);

        editor.apply();

        Log.d("infoApp", String.valueOf(sharedPreferences.getInt("edad",0)));

        //deleteFile("archivo.txt");

       /* String[] arregloArchivos = fileList();
        for (String archivo : arregloArchivos) {
            Log.d("infoApp", archivo);
        }*/
    }

    public void descargarDownloadManager(View view) {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permiso == PackageManager.PERMISSION_GRANTED) {
            //Que si tengo los permisos

            String fileName = "pucp.jpg";
            String endPoint = "https://pbs.twimg.com/profile_images/323479275/isotipoweb.jpg";

            Uri downloadUri = Uri.parse(endPoint);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle(fileName);
            request.setMimeType("image/jpeg");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                    File.separator + fileName);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);

        } else {
            //no tengo los permisos
            String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permisos, 1);
        }
    }

    public void descargarVolley(View view) {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permiso == PackageManager.PERMISSION_GRANTED) {
            //Que tengo los permisos

        } else {
            //no tengo los permisos
            String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permisos, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("infoApp", "Permisos aceptados");

            if (requestCode == 1) {
                descargarDownloadManager(null);
            } else {
                //requestCode == 2
                descargarVolley(null);
            }

        } else {
            Log.d("infoApp", "Permisos denegados");
        }

    }

    public void guardarEnSharedStorage(View view) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "ArchivoEjemplo.txt");

        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {

            Uri location = data.getData();
            try (ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(location, "w");
                 FileWriter fileWriter = new FileWriter(parcelFileDescriptor.getFileDescriptor());) {

                EditText editText = findViewById(R.id.editTextGuardar);
                String textoAGuardar = editText.getText().toString();
                fileWriter.write(textoAGuardar);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void leerArchivoDeTexto(View view) {
        try (FileInputStream fileInputStream = openFileInput("archivo.txt");
             FileReader fileReader = new FileReader(fileInputStream.getFD());
             BufferedReader bufferedReader = new BufferedReader(fileReader);) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("infoApp", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leerArchivoDeObjetos(View view) {
        try (FileInputStream fileInputStream = openFileInput("archivoPersonas.data");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {

            while (fileInputStream.available() > 0) {
                Log.d("infoApp", "fileInputStream.available(): " + fileInputStream.available());
                Persona p = (Persona) objectInputStream.readObject();
                Log.d("infoApp", p.getNombre());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean puedoEscribirSd() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public void guardarTexto(View view) {

/*
        FileOutputStream fileOutputStream = null;
        FileWriter fileWriter = null;
        try {
            fileOutputStream = openFileOutput("demo.txt", Context.MODE_PRIVATE);
             String texto = "Vamos a guardar esta demo para Alvaro";
            fileWriter = new FileWriter(fileOutputStream.getFD());
            fileWriter.write(texto);
            Log.d("infoApp", "escritura exitosa");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        try (FileOutputStream fileOutputStream = openFileOutput("demo2.txt", Context.MODE_PRIVATE);) {

            String texto = "Vamos a guardar esta demo para Alvaro 2";

            try (FileWriter fileWriter = new FileWriter(fileOutputStream.getFD());) {

                fileWriter.write(texto);
                Log.d("infoApp", "escritura exitosa");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void guardarTextoSd(View view) {
        String texto = "Nuevo Texto a guardar\nEsta es otra línea";
        if (puedoEscribirSd()) {
            File file = new File(getExternalFilesDir(null), "archivo.txt");
            try (FileWriter fileWriter = new FileWriter(file,true);) {
                fileWriter.write(texto);
                Log.d("infoApp", "escritura exitosa");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void leerTrabajos(View view) {
        try (FileInputStream fileInputStream = openFileInput("listaTrabajos.txt");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {

            Trabajo[] arregloTrabajos = (Trabajo[]) objectInputStream.readObject();
            for(Trabajo t : arregloTrabajos){
                Log.d("infoApp",t.getJobTitle());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void guardarTrabajos(View view) {
        String url = "http://192.168.1.108:9000/listar/trabajos";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("infoApp", response);
                        Gson gson = new Gson();
                        TrabajoDto trabajoDto = gson.fromJson(response, TrabajoDto.class);
                        Trabajo[] arregloTrabajos = trabajoDto.getTrabajos();

                        try (FileOutputStream fileOutputStream = openFileOutput("listaTrabajos2.txt", MODE_PRIVATE);
                             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
                            objectOutputStream.writeObject(arregloTrabajos);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("api-key", "HTUxbtfKpEb2GJ3Y2d9e");
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void guardarObjeto(View view) {
        Persona p1 = new Persona();
        p1.setNombre("Stuardo");
        p1.setApellido("Lucho");

        try (FileOutputStream fileOutputStream =
                     openFileOutput("archivoPersonas.data", Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
            objectOutputStream.writeObject(p1);
            Log.d("infoApp", "escritura de objeto exitosa");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}