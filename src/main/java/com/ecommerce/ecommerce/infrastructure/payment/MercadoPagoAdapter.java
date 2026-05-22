package com.ecommerce.ecommerce.infrastructure.payment;

import com.ecommerce.ecommerce.application.port.out.PagoPort;
import com.ecommerce.ecommerce.domain.model.ItemPedido;
import com.ecommerce.ecommerce.domain.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MercadoPagoAdapter implements PagoPort {

    private final RestClient restClient;
    private final String accessToken;
    private final String baseUrl;

    public MercadoPagoAdapter(RestClient.Builder builder,
                              @Value("${mercado-pago.access-token}") String accessToken,
                              @Value("${app.base-url:http://localhost:5173}") String baseUrl) {
        this.restClient = builder.baseUrl("https://api.mercadopago.com").build();
        this.accessToken = accessToken;
        this.baseUrl = baseUrl;
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

    @Override
    public boolean verificarPagoAprobado(Long externalReference) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/payments/search")
                            .queryParam("external_reference", externalReference.toString())
                            .build())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return false;

            var results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty()) return false;

            return results.stream().anyMatch(p ->
                    "approved".equals(p.get("status")));
        } catch (Exception e) {
            return false;
        }
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

        String confirmUrl = baseUrl + "/api/pagos/confirmar?pedidoId=" + pedido.getId();

        Map<String, Object> backUrls = new HashMap<>();
        backUrls.put("success", confirmUrl);
        backUrls.put("failure", confirmUrl);
        backUrls.put("pending", confirmUrl);

        Map<String, Object> preference = new HashMap<>();
        preference.put("items", items);
        preference.put("external_reference", pedido.getId().toString());
        preference.put("back_urls", backUrls);
        preference.put("auto_return", "approved");

        return preference;
    }
}
