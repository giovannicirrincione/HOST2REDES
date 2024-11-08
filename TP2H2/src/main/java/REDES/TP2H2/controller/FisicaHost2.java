package REDES.TP2H2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/host2")
public class FisicaHost2 {

    @Autowired
    EnlaceHost2 enlaceHost2;

    @PostMapping("/receive")
    public String recibirPaquete(@RequestBody String paqueteEntramadoConCRC) {
       String respuestaEnlace = enlaceHost2.recibirPaqueteCapaFisica(paqueteEntramadoConCRC);
       return respuestaEnlace;
    }

}
