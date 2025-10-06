package com.integracion.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    //Este es un nuevo comentario
    //Agregamos algo nuevo 2
    //Agregamos algo nuevo 3
    //Agregamos algo nuevo 4
    //Agregamos algo nuevo 5
    //Agregamos algo nuevo 6
    @GetMapping("/hello")
    public String saludar(){
        return "Hola Mundo con REST en Spring Boot";
    }

    //Este es otro comentario
    //Agregamos algo 1
    @GetMapping("/hello2")
    public String saludar2(){
        return "Hola Mundo con REST en Spring Boot";
    }


    @GetMapping("/hello3")
    public String saludar3(){
        return "Hola Mundo con REST en Spring Boot";
    }


    @GetMapping("/hello4")
    public String saludar4(){
        return "Hola Mundo con REST en Spring Boot";
    }


    //Otro comentario
    @GetMapping("/hello6")
    public String saludar6(){
        return "Hola Mundo con REST en Spring Boot";
    }

    //Otro comentario
    @GetMapping("/hello7")
    public String saludar7(){
        return "Hola Mundo con REST en Spring Boot";
    }

    //Otro comentario
    @GetMapping("/hello8")
    public String saludar8(){
        return "Hola Mundo con REST en Spring Boot Cambiado 1";
    }

    //Otro comentario
    @GetMapping("/hello9")
    public String saludar9(){
        return "Hola Mundo con REST en Spring Boot Cambiado 3";
    }
}
