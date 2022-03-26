package com.example.projetws.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projetws.R;
import com.example.projetws.addEtudiant;
import com.example.projetws.updateActivity;
import com.example.projetws.beans.Etudiant;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder>  {

    private List<Etudiant> Etudiants;
    private Context context;

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
                Intent intent = new Intent(context,updateActivity.class);
                intent.putExtra("id",((TextView) view.findViewById(R.id.id)).getText().toString());
                context.startActivity(intent);
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
    }

    @Override
    public int getItemCount() {
        return Etudiants.size();
    }

    public class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView ide ,ville,nom,prenom;
        ImageView img;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            ide = itemView.findViewById(R.id.id);
            ville = itemView.findViewById(R.id.tvVille);
            nom = itemView.findViewById(R.id.tvNom);
            prenom = itemView.findViewById(R.id.tvPrenom);
            img = itemView.findViewById(R.id.imgE);

        }
    }
}
