package com.example.projetws;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class updateActivity extends AppCompatActivity {
    EditText nom, prenom;
    RadioButton rdm,rdf;
    private Spinner ville;
    ImageView imgm;
    TextView iddd;
    private Bitmap bitmap;
    Button btnupd,addm , retour;
    EtudiantService service;
    RequestQueue requestQueue;
    public static String insertUrl = "http://10.0.2.2/Android/ws/findById.php";
    String insertUpdate = "http://10.0.2.2/Android/ws/updateEtudiant.php";

    String encodedimage="dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        nom = findViewById(R.id.nomm);
        iddd=findViewById(R.id.idm);
        prenom = findViewById(R.id.prenomm);
        rdf = findViewById(R.id.fm);
        rdm = findViewById(R.id.mm);
        imgm =findViewById(R.id.imgm);
        btnupd = findViewById(R.id.btnUpm);
        addm = findViewById(R.id.addm);
        ville = findViewById(R.id.villem);
        retour= findViewById(R.id.retour);
        service = EtudiantService.getInstance();


        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(updateActivity.this, ListEtudiant.class);
                startActivity(intent);
            }
        });
        btnupd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        addm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ok", "ok");

                StringRequest request = new StringRequest(Request.Method.POST,
                        insertUpdate, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", response);  Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                        Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
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
                        if (rdm.isChecked())
                            sexe = "homme";
                        else
                            sexe = "femme";
                        params.put("id",iddd.getText().toString());
                        params.put("nom", nom.getText().toString());
                        params.put("prenom", prenom.getText().toString());
                        params.put("ville", ville.getSelectedItem().toString());
                        params.put("sexe", sexe);
                        if (encodedimage=="dd"){
                            BitmapDrawable drawable = (BitmapDrawable) imgm.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            encodebitmap(bitmap);
                        }
                        params.put("image", encodedimage);


                        return params;

                    }
                };

              requestQueue =  Volley.newRequestQueue(getApplicationContext());
              requestQueue.add(request);
               Toast.makeText(getApplicationContext(),"Bien Modifier",Toast.LENGTH_SHORT).show();

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("id");
            StringRequest request = new StringRequest(Request.Method.POST,
                    insertUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                    Type type = new TypeToken<Etudiant>(){}.getType();
                    Etudiant etudiant = new Gson().fromJson(response, type);
                    iddd.setText(etudiant.getId()+"");
                    nom.setText(etudiant.getNom());
                    prenom.setText(etudiant.getPrenom());
                    Glide.with(getApplicationContext()).load(SplashActivity.UrlImage+etudiant.getImage()).into(imgm);
                    if(etudiant.getSexe().equals("homme"))
                        rdm.setChecked(true);
                    else
                        rdf.setChecked(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", id);
                    return params;
                }
            };
            requestQueue =  Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(request);
        }
    }

    private void encodebitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        encodedimage = android.util.Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(updateActivity.this);
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
                    Log.d("TAG", "vraiissssss C");
                    bitmap = (Bitmap) result.getData().getExtras().get("data");
                    imgm.setImageBitmap(bitmap);
                    encodebitmap(bitmap);
                }
            });
    ActivityResultLauncher<Intent> gallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri selectedImage = result.getData().getData();
                    Log.d("TAG", "onActivityResult: "+selectedImage);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        imgm.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    encodebitmap(bitmap);
                    Log.d("TAG", "onActivityResult: "+bitmap);
                }
            });

}