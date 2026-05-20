package com.ecommerce.ecommerce.infrastructure.payment;

import com.ecommerce.ecommerce.application.port.out.PagoPort;
import com.ecommerce.ecommerce.domain.model.ItemPedido;
import com.ecommerce.ecommerce.domain.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MercadoPagoAdapter implements PagoPort {

    private final RestClient restClient;
    private final String accessToken;

    public MercadoPagoAdapter(RestClient.Builder builder,
                              @Value("${mercado-pago.access-token}") String accessToken) {
        this.restClient = builder.baseUrl("https://api.mercadopago.com").build();
        this.accessToken = accessToken;
    }

    @Override
    public String generarLinkPago(Pedido pedido) {
        Map<String, Object> body = buildPreference(pedido);

        Map<String, Object> response = restClient.post()
                .uri("/checkout/preferences")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        return (String) response.get("init_point");
    }

    private Map<String, Object> buildPreference(Pedido pedido) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (ItemPedido item : pedido.getItems()) {
            Map<String, Object> mpItem = new HashMap<>();
            mpItem.put("title", item.getProductoNombre());
            mpItem.put("description", item.getVarianteDescripcion() != null ? item.getVarianteDescripcion() : "");
            mpItem.put("quantity", item.getCantidad());
            mpItem.put("currency_id", "ARS");
            mpItem.put("unit_price", item.getPrecioUnitario());
            items.add(mpItem);
        }

        Map<String, Object> backUrls = new HashMap<>();
        String baseUrl = "http://localhost:8080/api/pagos/confirmar?pedidoId=" + pedido.getId();
        backUrls.put("success", baseUrl);
        backUrls.put("failure", baseUrl);
        backUrls.put("pending", baseUrl);

        Map<String, Object> preference = new HashMap<>();
        preference.put("items", items);
        preference.put("external_reference", pedido.getId().toString());
        preference.put("back_urls", backUrls);

        return preference;
    }
}
