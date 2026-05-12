package com.erp.services;

import com.erp.dto.SupplierDTO;
import com.erp.enities.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<Supplier> getAllSupliers();
   Supplier createSupplier(Supplier supplier);

   void deleteSupplier(long id);
    Supplier updateSupplier(Long id, SupplierDTO dto);

    Optional<Supplier> getSupplierById(long id);

}
