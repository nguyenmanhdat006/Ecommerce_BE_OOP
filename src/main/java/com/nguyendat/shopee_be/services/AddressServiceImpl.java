package com.nguyendat.shopee_be.services;

import com.nguyendat.shopee_be.entities.Address;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.AddressRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository repository;

    public AddressServiceImpl(AddressRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Address> findAll() {
        return repository.findAll();
    }

    @Override
    public Address findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Address not found with id: " + id));
    }

    @Override
    public Address create(Address address) {
        return repository.save(address);
    }

    @Override
    public Address update(UUID id, Address address) {
        Address existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Address not found with id: " + id));
        BeanUtils.copyProperties(address, existing, "id");
        return repository.save(existing);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
