package br.com.ramiralvesmelo.pkcs.appresource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public String hello() {
        return "Olá, recurso protegido da API!";
    }
}
