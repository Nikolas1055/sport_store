package com.example.sport_store.component;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Cart implements Serializable {
    private static final long serialVersionUID = -1947132804983588610L;
    private Map<Long, Integer> productAttributeList;
    private String quantityError;

    public Cart() {
        productAttributeList = new HashMap<>();
        quantityError = "";
    }

    public void addProductAttribute(Long id) {
        productAttributeList.put(id, productAttributeList.get(id) == null ? 1 : productAttributeList.get(id) + 1);
    }

    public void deleteProductAttribute(Long id) {
        productAttributeList.remove(id);
    }

    public void clear() {
        quantityError = "";
        productAttributeList = new HashMap<>();
    }

    public Map<Long, Integer> getProductAttributeList() {
        return productAttributeList;
    }

    public Integer getProductCount() {
        return productAttributeList.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String getQuantityError() {
        return quantityError;
    }

    public void setQuantityError(String quantityError) {
        this.quantityError = quantityError;
    }
}
