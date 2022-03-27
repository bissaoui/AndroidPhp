package com.example.projetws;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.adapter.EtudiantAdapter;
import com.example.projetws.beans.Etudiant;
import com.example.projetws.service.EtudiantService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ListEtudiant extends AppCompatActivity {
    private EtudiantService service;
    private RecyclerView recyclerView;
    RequestQueue requestQueue;

    private EtudiantAdapter etudiantAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        setContentView(R.layout.activity_list_etudiant);
        service = EtudiantService.getInstance();
        recyclerView = findViewById(R.id.Recycler);
        etudiantAdapter = new EtudiantAdapter(this,service.getEtudiants());
        recyclerView.setAdapter(etudiantAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        loadEtudiant();
    }

    private void loadEtudiant() {
        StringRequest request = new StringRequest(Request.Method.POST,
                SplashActivity.insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                Collection<Etudiant> etudiantss = new Gson().fromJson(response, type);
                Log.d("AG", "  : " + etudiantss.size());

                service.setEtudiants((List<Etudiant>) etudiantss);
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

                return null;
            }
        };
        requestQueue =  Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add){
            Intent intent = new Intent(getApplicationContext(), addEtudiant.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onPause() {
        super.onPause();
    }
}