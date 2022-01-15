package com.example.sport_store.component;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс компонет - Корзина. Область видимости - сессия.
 */
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

    /**
     * Метод - добавить товарную позицию в корзину. Если позиция уже присутствует - увеличить её количество на 1.
     */
    public void addProductAttribute(Long id) {
        productAttributeList.put(id, productAttributeList.get(id) == null ? 1 : productAttributeList.get(id) + 1);
    }

    /**
     * Метод - удалить товар из корзины.
     */
    public void deleteProductAttribute(Long id) {
        productAttributeList.remove(id);
    }

    /**
     * Метод - очистить корзину. И обнулить ошибку.
     */
    public void clear() {
        quantityError = "";
        productAttributeList = new HashMap<>();
    }

    /**
     * Метод - получить все товары из корзины.
     */
    public Map<Long, Integer> getProductAttributeList() {
        return productAttributeList;
    }

    /**
     * Метод - полчить количество товаров в корзине.
     */
    public Integer getProductCount() {
        return productAttributeList.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Метод - получить ошибку, когда количество товаров в БД меньше чем уложено в корзину.
     */
    public String getQuantityError() {
        return quantityError;
    }

    /**
     * Метод - установить ошибку, когда количество товаров в БД меньше чем уложено в корзину.
     */
    public void setQuantityError(String quantityError) {
        this.quantityError = quantityError;
    }
}
