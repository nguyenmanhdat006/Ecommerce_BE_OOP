package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.CategoryType;
import java.util.List;
import java.util.UUID;

public interface CategoryTypeService {
    List<CategoryType> findAll();
    CategoryType findById(UUID id);
    CategoryType create(CategoryType entity);
    CategoryType update(UUID id, CategoryType entity);
    void deleteById(UUID id);
}
