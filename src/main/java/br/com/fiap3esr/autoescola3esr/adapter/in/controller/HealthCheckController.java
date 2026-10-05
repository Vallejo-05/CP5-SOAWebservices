package br.com.fiap3esr.autoescola3esr.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
@Tag(name = "Health Check")
public class HealthCheckController {
    @GetMapping
    @Operation(summary = "Verificar se a API está no ar")
    public String healthCheck() {
        return "Verificação de integridade da API da Auto Escola 3ESR!";
    }
}
