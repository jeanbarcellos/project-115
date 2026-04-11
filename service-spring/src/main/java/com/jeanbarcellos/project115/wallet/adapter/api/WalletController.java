package com.jeanbarcellos.project115.wallet.adapter.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @GetMapping
    public ResponseEntity<List<WalletResponse>> findByAll() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<WalletResponse> create(
            @RequestBody WalletCreateRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    // Deposito
    @PostMapping("/{id}/deposit")
    public WalletResponse deposit(
            @PathVariable Long id,
            @RequestBody WalletOperationRequest request) {
        return service.deposit(id, request);
    }

    // Retirado
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long id,
            @RequestHeader("If-Match") Long version,
            @RequestBody WalletOperationRequest request) {

        WalletResponse response = service.withdrraw(id, request, version);

        return ResponseEntity.ok()
                .eTag(response.getVersion().toString())
                .body(response);
    }
}