package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.CategoryType;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.CategoryTypeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryTypeServiceImpl implements CategoryTypeService {
    private final CategoryTypeRepository repository;

    public CategoryTypeServiceImpl(CategoryTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CategoryType> findAll() {
        return repository.findAll();
    }

    @Override
    public CategoryType findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("CategoryType not found with id: " + id));
    }

    @Override
    public CategoryType create(CategoryType entity) {
        return repository.save(entity);
    }

    @Override
    public CategoryType update(UUID id, CategoryType entity) {
        CategoryType existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("CategoryType not found with id: " + id));
        BeanUtils.copyProperties(entity, existing, "id");
        return repository.save(existing);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
