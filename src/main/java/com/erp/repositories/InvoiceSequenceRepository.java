package com.erp.repositories;

import com.erp.enities.InvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, String> {
}
