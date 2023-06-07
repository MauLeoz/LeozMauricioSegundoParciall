package org.example;

import java.io.Serializable;

public class TemperaturaAltaException extends Exception implements Serializable {
    private int numeroTest;
    private String barrio;

    public TemperaturaAltaException(int numeroTest, String barrio) {
        this.numeroTest = numeroTest;
        this.barrio = barrio;
    }
    public int getNumeroTest() {
        return numeroTest;
    }

    public String getBarrio() {
        return barrio;
    }

    @Override
    public String toString() {
        return "TemperaturaAltaException{" +
                "numeroTest=" + numeroTest +
                ", barrio='" + barrio + '\'' +
                "} ";
    }
}