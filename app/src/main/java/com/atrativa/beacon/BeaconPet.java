package com.atrativa.beacon;


import org.altbeacon.beacon.Beacon;

public class BeaconPet   {

    private String nome;
    private Beacon beacon;

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
