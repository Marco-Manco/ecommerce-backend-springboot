package com.ecommerce.ecommerce.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "variante_producto")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VarianteProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(length = 50)
    private String color;

    @Column(length = 20)
    private String tamanio;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "stock_reservado", nullable = false)
    private Integer stockReservado = 0;

    @Column(nullable = false)
    private Boolean activo = true;

    public VarianteProducto(Producto producto, String sku, String color, String tamanio, BigDecimal precio, Integer stock) {
        this.producto = producto;
        this.sku = sku;
        this.color = color;
        this.tamanio = tamanio;
        this.precio = precio;
        this.stock = stock;
    }
    @Transient
    public Integer getStockDisponible() {
        return stock - stockReservado;
    }
}
