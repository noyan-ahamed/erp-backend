package com.erp.repositories;

import com.erp.enities.PartyLedgerEntry;
import com.erp.enums.PartyType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyLedgerEntryRepository extends JpaRepository<PartyLedgerEntry,Long> {
    List<PartyLedgerEntry> findByPartyTypeAndPartyIdOrderByEntryDateAscIdAsc(PartyType partyType, Long partyId);

    //new
    List<PartyLedgerEntry> findByPartyTypeAndPartyId(PartyType partyType, Long partyId, Sort sort);


    Optional<PartyLedgerEntry> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}

