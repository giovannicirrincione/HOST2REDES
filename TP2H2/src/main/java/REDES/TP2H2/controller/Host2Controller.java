package REDES.TP2H2.controller;

import REDES.TP2H2.Package;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/host2")
public class Host2Controller {

    @GetMapping("/message")
    public String getMessage() {
        return "Hello from Host 2";
    }

    @PostMapping("/receive")
    public String receivePackage(@RequestBody String receivedPackage) {
        System.out.println(receivedPackage);
        return "Host 2 received package with content: " + receivedPackage;
    }
}