package com.example.glucometro_colores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity
{
    Button btnProcess;
    Intent intent;
    ImageView imgView;
    ListView infoapp;
    EditText resultadoglu;
    String encoded="";
    Uri mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnProcess = findViewById(R.id.BtnProcess);
        resultadoglu = findViewById(R.id.Resultado);
        imgView = findViewById(R.id.Foto);
        //poner el arreglo de strings en el listview
        String [] pasos = getResources().getStringArray(R.array.texto_info);
        infoapp = findViewById(R.id.Listapasos);
        ArrayAdapter <String> adapter = new ArrayAdapter <String>(this,  android.R.layout.simple_list_item_1,pasos);
        infoapp.setAdapter(adapter);


        //llamada a la funcion del boton process
        btnProcess.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               if (imgView.getDrawable() == null)
                {
                    infoapp.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Por favor tome la foto de su tira reactiva", Toast.LENGTH_SHORT).show();
                    return;
                }
                postDataUsingVolley(encoded);
            }
        });
    }

    //Se pone en uso la barra de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menubar, menu);
        return true;
    }

    //accionamos los botones de la barra
    @Override
    public  boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.camara:
                infoapp.setVisibility(View.INVISIBLE);
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
                return true;

            case R.id.rangos:
                startActivity(new Intent(MainActivity.this,Popup.class));
                return true;

            case R.id.info:
                Toast.makeText(MainActivity.this, "La información estará disponible próximamente", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    /*
    public void onChooseaFile(View v)
    {
        CropImage.activity().start(MainActivity.this);
    }

    //Se inicia con el proceso de captura de la imagen
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                mImage = result.getUri();
                imgView.setImageURI(mImage);
            }

            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                Exception e = result.getError();
                Toast.makeText(MainActivity.this, "La información estará disponible próximamente", Toast.LENGTH_SHORT).show();
            }
        }

    }*/

    //transformamos la imagen a base64
    public void bitMapToBase64(Bitmap bitmap)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            System.out.println("La imagen es:"+ encoded);
        }
        catch(Exception e )
        {
            System.out.println(e);
        }
    }

    //Se hace la vinculación de la API para el procesamiento de la imagen
    private void postDataUsingVolley(String encoded)
    {
        String url = "https://0076-2806-2f0-92c0-7394-984b-7664-f68a-ae3b.ngrok.io/glucometro/v1/images/detect-image";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JSONObject postData = new JSONObject();
         try {
            postData.put("image", ""+encoded);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }
}