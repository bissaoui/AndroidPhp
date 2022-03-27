package com.example.projetws.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projetws.R;
import com.example.projetws.SplashActivity;
import com.example.projetws.addEtudiant;
import com.example.projetws.service.EtudiantService;
import com.example.projetws.updateActivity;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder>  {

    private List<Etudiant> Etudiants;
    private Context context;
    RequestQueue requestQueue;



    public EtudiantAdapter(Context context , List<Etudiant> etudiants)
    {
        this.context=context;
        this.Etudiants = etudiants;
    }


    @NonNull
    @Override
    public EtudiantAdapter.EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.items,
                parent, false);
        final EtudiantViewHolder holder = new EtudiantViewHolder(v);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence[] options = { "modifier", "Supprimer","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("modifier"))
                        {
                            Intent intent = new Intent(context,updateActivity.class);
                            intent.putExtra("id",((TextView) view.findViewById(R.id.id)).getText().toString());
                            context.startActivity(intent);

                        }
                        else if (options[item].equals("Supprimer"))
                        {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            final CharSequence[] optionss = { "confirmer","Cancel" };

                            builder1.setItems(optionss, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (optionss[i].equals("confirmer")){
                                        Toast.makeText(context,"Bien Supprimer",Toast.LENGTH_SHORT).show();

                                        String urlReq="http://192.168.11.109/Android/ws/deleteEtudiant.php";
                                        StringRequest request = new StringRequest(Request.Method.POST,
                                                urlReq, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {


                                                Log.d("TAG", response);  Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                                                Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                                                for(Etudiant e : etudiants){
                                                    Log.d("TAG", e.toString());
                                                }
                                                EtudiantService.getInstance().setEtudiants((List<Etudiant>) etudiants);
                                                for(Etudiant e : EtudiantService.getInstance().getEtudiants()){
                                                    e.setImage(SplashActivity.UrlImage+e.getImage());

                                                }



                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();

                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {

                                                HashMap<String, String> params = new HashMap<String, String>();
                                                params.put("id", ((TextView) view.findViewById(R.id.id)).getText().toString());
                                                return params;

                                            }
                                        };
                                        requestQueue =  Volley.newRequestQueue(context);
                                        requestQueue.add(request);
                                        Etudiants.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());



                                    }
                                    else if (optionss[i].equals("Cancel")) {
                                        dialogInterface.dismiss();
                                    }

                                }
                            });
                            builder1.show();

                        }
                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantAdapter.EtudiantViewHolder holder, int position) {

        Glide.with(context)
                .asBitmap()
                .load(Etudiants.get(position).getImage())
                .apply(new RequestOptions().override(100, 100))
                .into(holder.img);
        holder.nom.setText(Etudiants.get(position).getNom().toUpperCase());
        holder.prenom.setText(Etudiants.get(position).getPrenom().toUpperCase());
        holder.ville.setText(Etudiants.get(position).getVille().toUpperCase());
        holder.ide.setText(Etudiants.get(position).getId()+"");
        holder.sexe.setText(Etudiants.get(position).getSexe());
    }

    @Override
    public int getItemCount() {
        return Etudiants.size();
    }

    public class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView ide ,ville,nom,prenom, sexe;
        ImageView img;


        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            ide = itemView.findViewById(R.id.id);
            ville = itemView.findViewById(R.id.tvVille);
            nom = itemView.findViewById(R.id.tvNom);
            prenom = itemView.findViewById(R.id.tvPrenom);
            img = itemView.findViewById(R.id.imgE);
            sexe = itemView.findViewById(R.id.sexeee);

        }
    }
}
