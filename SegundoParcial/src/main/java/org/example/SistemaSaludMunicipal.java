package org.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.*;

public class SistemaSaludMunicipal {
    private LinkedHashSet<Persona> registros;
    private int cantidadReactivos;
    private int numeroKit;

    public SistemaSaludMunicipal(int cantidadReactivos) {
        this.registros = new LinkedHashSet<>();
        this.cantidadReactivos = cantidadReactivos;
        this.numeroKit = 1;
    }

    public SistemaSaludMunicipal() {
    }

    public SistemaSaludMunicipal(LinkedHashSet<Persona> registros, int cantidadReactivos, int numeroKit) {
        this.registros = registros;
        this.cantidadReactivos = cantidadReactivos;
        this.numeroKit = numeroKit;
    }

    public LinkedHashSet<Persona> getRegistros() {
        return registros;
    }

    public void setRegistros(LinkedHashSet<Persona> registros) {
        this.registros = registros;
    }

    public int getCantidadReactivos() {
        return cantidadReactivos;
    }

    public void setCantidadReactivos(int cantidadReactivos) {
        this.cantidadReactivos = cantidadReactivos;
    }

    public int getNumeroKit() {
        return numeroKit;
    }

    public void setNumeroKit(int numeroKit) {
        this.numeroKit = numeroKit;
    }

    public void registrarPersona(String nombre, String apellido, int edad, String barrio, String dni, String ocupacion) throws SinReactivosException, DNIRepetidoException {
        if (cantidadReactivos <= 0) {
            throw new SinReactivosException("No hay reactivos disponibles");
        }

        if (existeDNI(dni)) {
            throw new DNIRepetidoException("El DNI ya ha sido registrado");
        }

        Persona nuevaPersona = new Persona(nombre, apellido, edad, barrio, dni, ocupacion, numeroKit);
        registros.add(nuevaPersona);

        cantidadReactivos--;
        numeroKit++;

        System.out.println("Persona registrada exitosamente. Número de kit asignado: " + nuevaPersona.getNumeroKit());
    }

    private boolean existeDNI(String dni) {
        for (Persona persona : registros) {
            if (persona.getDni().equals(dni)) {
                return true;
            }
        }
        return false;
    }


    public int consultarReactivosDisponibles() {
        System.out.println("Ingrese cantidad de kits extras disponibles (si existen)");
        Scanner scanner = new Scanner(System.in);
        int cant = scanner.nextInt();
        return cant;
    }

    public void ingresarNuevosTests(int cantidadTests) {
        if (cantidadTests > 0) {
            cantidadReactivos += cantidadTests;
            System.out.println("Se han ingresado " + cantidadTests + " nuevos tests.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SistemaSaludMunicipal{\n");
        sb.append("registros=\n");

        for (Persona persona : registros) {
            sb.append(persona.toString());
            sb.append("\n");
        }

        sb.append("cantidadReactivos=");
        sb.append(cantidadReactivos);
        sb.append(", numeroKit=");
        sb.append(numeroKit);
        sb.append("\n}");

        return sb.toString();
    }

    public Map<Integer, Map<String, Double>> testear(Collection<Persona> personas) {
        Map<Integer, Map<String, Double>> tablaResultados = new HashMap<>();

        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.00");

        for (Persona persona : personas) {
            int numeroKit = persona.getNumeroKit();
            String dni = persona.getDni();
            double temperatura = 36 + random.nextDouble() * (39 - 36);
            temperatura = Math.round(temperatura * 100.0) / 100.0;

            if (!tablaResultados.containsKey(numeroKit)) {
                tablaResultados.put(numeroKit, new HashMap<>());
            }
            tablaResultados.get(numeroKit).put(dni, temperatura);
        }

        return tablaResultados;
    }

    public void aislar(Map<Integer, Map<String, Double>> tablaResultados, LinkedHashSet<Persona> registrosOriginales) {
        for (Map.Entry<Integer, Map<String, Double>> entry : tablaResultados.entrySet()) {
            int numeroKit = entry.getKey();
            Map<String, Double> resultados = entry.getValue();

            for (Map.Entry<String, Double> resultado : resultados.entrySet()) {
                String dni = resultado.getKey();
                double temperatura = resultado.getValue();

                if (temperatura >= 38) {
                    Persona personaEncontrada = buscarPersonaPorDNI(registrosOriginales, dni);

                    try {
                        throw new TemperaturaAltaException(numeroKit, personaEncontrada.getBarrio());
                    } catch (TemperaturaAltaException e) {
                        System.out.println(e.toString());
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream("urgente.dat");
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(e);
                            objectOutputStream.close();
                            fileOutputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    }
                }
            }
    }


    public Persona buscarPersonaPorDNI(LinkedHashSet<Persona> registrosOriginales, String dni) {
        for (Persona persona : registrosOriginales) {
            if (persona.getDni().equals(dni)) {
                return persona;
            }
        }
        return null;
    }



}