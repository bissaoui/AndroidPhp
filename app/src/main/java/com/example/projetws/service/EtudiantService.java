package com.example.projetws.service;

import com.example.projetws.beans.Etudiant;

import java.util.ArrayList;
import java.util.List;

public class EtudiantService {

    private List<Etudiant> etudiants;

    public List<Etudiant> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    private EtudiantService() {
        this.etudiants = new ArrayList<>();
    }

    private static EtudiantService instance;

    public int size(){
        return etudiants.size();
    }

    public  static EtudiantService getInstance() {
        if(instance == null)
            instance = new EtudiantService();
        return instance;
    }
}
