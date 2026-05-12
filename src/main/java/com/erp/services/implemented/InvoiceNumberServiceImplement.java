package com.erp.services.implemented;

import com.erp.enities.InvoiceSequence;
import com.erp.repositories.InvoiceSequenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoiceNumberServiceImplement {
    private final InvoiceSequenceRepository sequenceRepo;

    @Transactional
    public String generate(String prefix){

        String yearMonth = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMM"));

        String key = prefix + "-" + yearMonth;

        InvoiceSequence seq = sequenceRepo.findById(key)
                .orElseGet(() -> {
                    InvoiceSequence newSeq = new InvoiceSequence();
                    newSeq.setSequenceKey(key);
                    newSeq.setCurrentValue(0L);
                    return newSeq;
                });

        Long nextValue = seq.getCurrentValue() + 1;
        seq.setCurrentValue(nextValue);
        sequenceRepo.save(seq);

        return prefix + "-" + yearMonth + "-"
                + String.format("%04d", nextValue);
    }
}
