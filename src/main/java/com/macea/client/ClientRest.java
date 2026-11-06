package com.macea.client;

import com.macea.dto.ProductoDto;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class ClientRest {
    private static final String URL = "http://localhost:8080/api/productos";

    public ProductoDto consultarProducto(String codigo) {
        Client cliente = ClientBuilder.newClient();
        try {
            Response response = cliente.target(URL).path(codigo)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(ProductoDto.class);
            }
            return null; // no encontrado u otro error
        } finally {
            cliente.close();
        }
    }

    public List<ProductoDto> consultarTodosLosProductos() {
        Client cliente = ClientBuilder.newClient();
        try {
            Response response = cliente.target(URL)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<ProductoDto>>() {});
            }
            return null;
        } finally {
            cliente.close();
        }
    }

    public ProductoDto insertarProducto(ProductoDto producto) {
        Client cliente = ClientBuilder.newClient();
        try {
            Response response = cliente.target(URL)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(producto, MediaType.APPLICATION_JSON));
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(ProductoDto.class);
            }
            return null;
        } finally {
            cliente.close();
        }
    }

    public ProductoDto actualizarProducto(String codigo, ProductoDto producto) {
        Client cliente = ClientBuilder.newClient();
        try {
            Response response = cliente.target(URL).path(codigo)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(producto, MediaType.APPLICATION_JSON));
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(ProductoDto.class);
            }
            return null;
        } finally {
            cliente.close();
        }
    }

    public boolean eliminarProducto(String codigo) {
        Client cliente = ClientBuilder.newClient();
        try {
            Response response = cliente.target(URL).path(codigo)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();
            return response.getStatus() == Response.Status.NO_CONTENT.getStatusCode();
        } finally {
            cliente.close();
        }
    }
}