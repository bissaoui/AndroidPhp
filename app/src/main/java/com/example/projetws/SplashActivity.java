package com.example.projetws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.projetws.beans.Etudiant;
import com.example.projetws.service.EtudiantService;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    EtudiantService service;
    RequestQueue requestQueue;
    public static String insertUrl = "http://10.0.2.2/Android/ws/loadEtudiant.php";
    public static String UrlImage = "http://10.0.2.2/Android/Images/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_splash);
        service = EtudiantService.getInstance();

        StringRequest request = new StringRequest(Request.Method.POST,
                insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                 Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                Collection<Etudiant> etudiantss = new Gson().fromJson(response, type);
                Log.d("AG", "  : " + etudiantss.size());

                service.setEtudiants((List<Etudiant>) etudiantss);
                for(Etudiant e : service.getEtudiants()){
                    e.setImage(UrlImage+e.getImage());

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

                return null;
            }
        };
        requestQueue =  Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, ListEtudiant.class);
                startActivity(intent);

            }
        },4000);
    }

    protected void onPause() {
        super.onPause();
        this.finish();
    }
}