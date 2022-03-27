package com.example.projetws;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.beans.Etudiant;
import com.example.projetws.service.EtudiantService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class addEtudiant extends AppCompatActivity  {
    private EditText nom;
    final String TAG="data";
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add,upload;
    private Bitmap bitmap;
    private EtudiantService service;
    private CircleImageView img;
    String encodedimage="";
    RequestQueue requestQueue;
    String insertUrl = "http://192.168.11.109/Android/ws/createEtudiant.php";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            Intent intent = new Intent(getApplicationContext(), ListEtudiant.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        setContentView(R.layout.activity_add_etudiant);
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        ville = (Spinner) findViewById(R.id.ville);
        add = (Button) findViewById(R.id.add);
        upload = (Button) findViewById(R.id.btnUp);
        m = (RadioButton) findViewById(R.id.m);
        f = (RadioButton) findViewById(R.id.f);
        img = findViewById(R.id.img);
        service = EtudiantService.getInstance();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ok", "ok");

                StringRequest request = new StringRequest(Request.Method.POST,
                        insertUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nom.setText("");
                        prenom.setText("");
                        img.setImageResource(R.drawable.ic_launcher_background);

                        // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response);  Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                        Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                        for(Etudiant e : etudiants){
                            Log.d(TAG, e.toString());
                        }
                        service.setEtudiants((List<Etudiant>) etudiants);
                        for(Etudiant e : service.getEtudiants()){
                            e.setImage(SplashActivity.UrlImage+e.getImage());

                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        HashMap<String, String> params = new HashMap<String, String>();
                        String sexe = "";
                        if (m.isChecked())
                            sexe = "homme";
                        else
                            sexe = "femme";
                        params.put("nom", nom.getText().toString());
                        params.put("prenom", prenom.getText().toString());
                        params.put("ville", ville.getSelectedItem().toString());
                        params.put("sexe", sexe);
                        params.put("image", encodedimage);
                        return params;

                    }
                };
                requestQueue =  Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(request);
               Toast.makeText(getApplicationContext(),"bien ajouter",Toast.LENGTH_SHORT).show();
            }


        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }


    private void encodebitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        encodedimage = android.util.Base64.encodeToString(bytes, Base64.DEFAULT);

    }
    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(addEtudiant.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Dexter.withContext(getApplicationContext())
                            .withPermission(Manifest.permission.CAMERA)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    camera.launch(intent);

                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gallery.launch(intent);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    ActivityResultLauncher<Intent> camera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "vraiissssss C");

                        bitmap = (Bitmap) result.getData().getExtras().get("data");
                        img.setImageBitmap(bitmap);
                        encodebitmap(bitmap);


                }
            });
    ActivityResultLauncher<Intent> gallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                            Uri selectedImage = result.getData().getData();

                    Log.d(TAG, "onActivityResult: "+selectedImage);
                    try {
                         bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        img.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    encodebitmap(bitmap);


                    Log.d(TAG, "onActivityResult: "+bitmap);

                }
            });


}