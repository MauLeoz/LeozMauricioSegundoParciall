package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws SinReactivosException, DNIRepetidoException, JsonProcessingException {
        SistemaSaludMunicipal ssm = new SistemaSaludMunicipal(10);

        //Se cargan los datos segun las validaciones de kits y DNI
        try {
            ssm.registrarPersona("Mauricio", "Leoz", 33, "Nueva Pompeya", "34740499", "Estudiante");
            ssm.registrarPersona("Melisa", "Maidana", 34, "San Carlos", "34217441", "Contador");
            ssm.registrarPersona("Andrea", "Alvarez", 52, "Puerto", "20256887", "Empleado");
            ssm.registrarPersona("Mariano", "Torres", 18, "Chauvin", "45012257", "Estudiante");
            ssm.registrarPersona("Pedro", "Paz", 89, "Juramento", "1525688", "Jubilado");
        } catch (SinReactivosException e) {
            System.out.println("Error: " + e.getMessage());
        //Si no hay reactivos, salta la excepcion y piden ingresar mas (si es posible)
            int reactivosDisponibles = ssm.consultarReactivosDisponibles();
            if (reactivosDisponibles > 0) {
                System.out.println("El SSM cuenta con " + reactivosDisponibles + " tests extras.");
                ssm.ingresarNuevosTests(reactivosDisponibles);
              } else {
                System.out.println("El SSM no cuenta con más tests disponibles.");
            }
        } catch (DNIRepetidoException e) {
        //excepcion por DNI duplicado
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println(ssm.getCantidadReactivos());

        System.out.println(ssm);

        //Creo un mapa con los datos de la toma de temperatura
        Map<Integer, Map<String, Double>> registroTemperatura =  ssm.testear(ssm.getRegistros());
        System.out.println(registroTemperatura);

        //Indico si hay que aislar a los que tengan temperaturas mayores a 38 grados
        //Esto, a su vez, genera el archivo URGENTE.dat con los pacientes a aislar
        ssm.aislar(registroTemperatura,ssm.getRegistros());


        //creo 2 arrays para separar los pacientes sanos de los que haya que aislar
        List<Persona> sanos = new ArrayList<>();
        List<Persona> aislar = new ArrayList<>();

        //Recorro las colecciones para separar los datos
        for (Map.Entry<Integer, Map<String, Double>> entry : registroTemperatura.entrySet()) {
            Map<String, Double> resultados = entry.getValue();

            for (Map.Entry<String, Double> resultado : resultados.entrySet()) {
                String dni = resultado.getKey();
                double temperatura = resultado.getValue();

                Persona personaEncontrada = ssm.buscarPersonaPorDNI(ssm.getRegistros(), dni);

                if (temperatura > 38) {
                    aislar.add(personaEncontrada);
                } else {
                    sanos.add(personaEncontrada);
                }
            }
        }

       //creo un Mapa para guardar los 2 arreglos
        Map<String, List<Persona>> mapa = new HashMap<>();
        mapa.put("Sanos", sanos);
        mapa.put("Aislar",aislar);

        System.out.println(mapa);

        //Comienzo a crear los datos para pasar el mapa a un JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String rutaArchivo = "C:\\Users\\Leoz\\IdeaProjects\\LeozMauricioSegundoParciall\\SegundoParcial\\mapa.json";

        try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
            objectMapper.writeValue(fileWriter, mapa);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("JSON guardado en el archivo con exito" );
    }

}