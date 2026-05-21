package com.ecommerce.ecommerce.application.port.out;

public interface AlmacenamientoPort {
    String subirImagen(byte[] bytes, String nombreArchivo);
    void eliminarImagen(String publicId);
}
