package com.erp.services.implemented;

import com.erp.dto.SupplierDTO;
import com.erp.enities.Supplier;
import com.erp.repositories.SupplierRepository;
import com.erp.services.SupplierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImplement implements SupplierService {

    private final SupplierRepository supplierRepo;


    @Override
    public List<Supplier> getAllSupliers() {
        return supplierRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Override
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    @Override
    public void deleteSupplier(long id) {
        supplierRepo.deleteById(id);
    }

    @Override
    public Supplier updateSupplier(Long id, SupplierDTO dto) {

        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setName(dto.getName());
        supplier.setMobileNumber(dto.getMobileNumber());
        supplier.setAddress(dto.getAddress());
        supplier.setCompanyName(dto.getCompanyName());
        supplier.setTinNumber(dto.getTinNumber());
        supplier.setPaymentTerms(dto.getPaymentTerms());

// ✅ FIX: email set kora hocche
        supplier.setEmail(dto.getEmail());

        supplier.setRating(dto.getRating());
        supplier.setBankAccount(dto.getBankAccount());
        supplier.setBkashNo(dto.getBkashNo());

        return supplierRepo.save(supplier);
    }

    @Override
    public Optional<Supplier> getSupplierById(long id) {
        return supplierRepo.findById(id);
    }

}
