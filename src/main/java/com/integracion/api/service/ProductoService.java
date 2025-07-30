package com.integracion.api.service;

import com.integracion.api.model.Producto;
import com.integracion.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public Producto saveProducto(Producto producto){
        return productoRepository.save(producto);
    }

    public List<Producto> getProductos(){
        return productoRepository.getAllProductos();
    }

    public Producto getProductoById(int id){
        return productoRepository.findById(id);
    }

    public String deleteProducto(int id){
        productoRepository.delete(id);
        return "Producto eliminado !!" + id;
    }

    public Producto updateProducto(Producto  producto){
        return productoRepository.update(producto);
    }
}
