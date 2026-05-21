package com.ecommerce.ecommerce.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.ecommerce.application.port.out.AlmacenamientoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CloudinaryAdapter implements AlmacenamientoPort {

    private final Cloudinary cloudinary;

    public CloudinaryAdapter(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public String subirImagen(byte[] bytes, String nombreArchivo) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                    "public_id", "ecommerce/" + nombreArchivo,
                    "overwrite", true
            ));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen a Cloudinary", e);
        }
    }

    @Override
    public void eliminarImagen(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar imagen de Cloudinary", e);
        }
    }
}
