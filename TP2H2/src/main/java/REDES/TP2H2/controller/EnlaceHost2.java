package REDES.TP2H2.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/host2")
public class EnlaceHost2 {

    private static final String FLAG = "01111110";  // Definir el valor de FLAG
    private static final String ESC = "01111101";   // Definir el valor de ESC (escape)

    static final String polinomioGenerador = "1101";

    private String CRCRecibido;

    @PostMapping("/receive")
    public String recibirPaquete(@RequestBody String paqueteEntramadoConCRC) {

        // Paso 1: Desentramar el paquete recibido
        System.out.println("Paquete recibido en Host 2 (con CRC): " + paqueteEntramadoConCRC);

        // Buscar el índice del último espacio
        int indexUltimoEspacio = paqueteEntramadoConCRC.lastIndexOf(' ');

        // Obtener el substring que está después del último espacio
        CRCRecibido = paqueteEntramadoConCRC.substring(indexUltimoEspacio + 1); // +1 para obtener después del espacio
        System.out.println("El CRC recibido es: "+ CRCRecibido);

        // Buscar el índice del último espacio
        int indexUltimoEspacio2 = paqueteEntramadoConCRC.lastIndexOf(' ');

        // Obtener el substring que está antes del último espacio
        String paqueteEntramadoSinCRC = paqueteEntramadoConCRC.substring(0, indexUltimoEspacio2);

        System.out.println("el paquete entramado sin CRC es: " + paqueteEntramadoSinCRC);

        String paqueteDesentramado = desentramarPaquete(paqueteEntramadoSinCRC);
        System.out.println("Paquete desentramado (sin FLAG ni ESC): " + paqueteDesentramado);

        // Inserta aquí el código de verificación del CRC
        System.out.println("Paquete desentramado completo (con CRC): " + paqueteDesentramado);



        // Paso 3: Calcular el CRC de los datos recibidos
        String datosConPadding = paqueteEntramadoSinCRC + "0".repeat(polinomioGenerador.length() - 1); // Agregamos ceros
        String crcCalculado = aplicarCrc(datosConPadding , polinomioGenerador);
        System.out.println("CRC calculado: " + crcCalculado);

        // Paso 4: Comparar el CRC calculado con el CRC recibido
        boolean crcValido = crcCalculado.equals(CRCRecibido);
        System.out.println("CRC válido: " + crcValido);

        // Paso 5: Enviar acuse de recibo a Host 1 según el resultado de la verificación
        String respuesta = crcValido ? "ACK" : "NACK";
        enviarAcuseRecibo(respuesta);

        return respuesta;
    }

    public static String desentramarPaquete(String paqueteEntramado) {
        StringBuilder tramaSinStuffing = new StringBuilder();

        // Eliminamos el primer y último FLAG
        String trama = paqueteEntramado.substring(FLAG.length(), paqueteEntramado.length() - FLAG.length());

        boolean escapado = false;

        for (int i = 0; i < trama.length(); i += 8) {
            String byteActual = trama.substring(i, Math.min(i + 8, trama.length()));

            if (byteActual.equals(ESC)) {
                escapado = true;
                continue; // Saltar al siguiente byte
            }

            if (escapado) {
                escapado = false; // Procesar el byte escapado
            } else {
                tramaSinStuffing.append(byteActual);  // Agregar el byte desentramado
            }
        }

        return tramaSinStuffing.toString();
    }

    public static String aplicarCrc(String paquete, String divisor){
        int longitudDivisor = divisor.length();
        String temp = paquete.substring(0, longitudDivisor);

        for (int i = longitudDivisor; i < paquete.length(); i++) {
            if (temp.charAt(0) == '1') {
                StringBuilder tempBuilder = new StringBuilder(temp);
                for (int j = 0; j < longitudDivisor; j++) {
                    tempBuilder.setCharAt(j, (tempBuilder.charAt(j) == divisor.charAt(j)) ? '0' : '1');
                }
                temp = tempBuilder.toString();
            }
            temp = temp.substring(1) + paquete.charAt(i);
        }

        // Última operación XOR si el primer bit es 1
        if (temp.charAt(0) == '1') {
            StringBuilder tempBuilder = new StringBuilder(temp);
            for (int j = 0; j < longitudDivisor; j++) {
                tempBuilder.setCharAt(j, (tempBuilder.charAt(j) == divisor.charAt(j)) ? '0' : '1');
            }
            temp = tempBuilder.toString();
        }

        return temp.substring(1); // Retornamos el residuo (sin el bit más significativo)
    }

    public static void enviarAcuseRecibo(String respuesta) {
        RestTemplate restTemplate = new RestTemplate();
        String urlHost1 = "http://localhost:8080/host1/ack";

        // Enviar acuse de recibo a Host 1
        restTemplate.postForObject(urlHost1, respuesta, String.class);
        System.out.println("Acuse de recibo enviado a Host 1: " + respuesta);
    }
}