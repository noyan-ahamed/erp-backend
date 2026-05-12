package com.erp.controllers;

import com.erp.enities.PartyLedgerEntry;
import com.erp.services.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {
    private final LedgerService ledgerService;

    @GetMapping("")
    public List<PartyLedgerEntry> getAllLedger(){
        return ledgerService.getAllLedger();
    }

}
