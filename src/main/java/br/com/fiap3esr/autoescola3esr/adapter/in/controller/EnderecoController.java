package br.com.fiap3esr.autoescola3esr.adapter.in.controller;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.endereco.DadosConsultaCep;
import br.com.fiap3esr.autoescola3esr.application.service.ConsultaEnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Endereços", description = "Consulta de endereço por CEP através da API externa ViaCEP")
@RequiredArgsConstructor
public class EnderecoController {
    private final ConsultaEnderecoService service;

    @GetMapping("/cep/{cep}")
    @Operation(
            summary = "Consultar endereço por CEP",
            description = "Consome o WebService externo ViaCEP (https://viacep.com.br). " +
                    "Útil para preencher automaticamente o endereço no cadastro de alunos e instrutores.")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado")
    @ApiResponse(responseCode = "404", description = "CEP inválido ou inexistente")
    @ApiResponse(responseCode = "503", description = "Serviço ViaCEP indisponível")
    public ResponseEntity<DadosConsultaCep> consultarCep(@PathVariable String cep) {
        return ResponseEntity.ok(service.consultarPorCep(cep));
    }
}
