package com.integracion.api.model;


public class Calculadora {

    private int resultado;

    public void sumar(int a, int b) {
        resultado = a + b;
    }

    public void restar(int a, int b) {
        resultado = a - b;
    }

    public void multiplicar(int a, int b) {
        resultado = a * b;
    }

    public void dividir(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException(
                    "El divisor no puede ser cero");
        }
        resultado = a / b;
    }

    public int getResultado() {
        return resultado;
    }

    public void operacionLarga() {
        for (int i = 0; i < 100; i++) {
            procesarNivel(i);
        }
    }

    private void procesarNivel(int i) {
        for (int j = 0; j < 100; j++) {
            procesarSubNivel(j);
        }
    }

    private void procesarSubNivel(int j) {
        for (int k = 0; k < 100; k++) {
            // procesamiento
        }
    }
}
