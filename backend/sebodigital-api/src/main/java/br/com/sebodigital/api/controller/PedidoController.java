package br.com.sebodigital.api.controller;

import br.com.sebodigital.api.dto.pedido.CriarPedidoRequest;
import br.com.sebodigital.api.dto.pedido.PedidoResponse;
import br.com.sebodigital.api.dto.pedido.StatusPedidoRequest;
import br.com.sebodigital.api.service.PedidoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(
            Authentication authentication,
            @Valid @RequestBody CriarPedidoRequest request) {
        PedidoResponse response = pedidoService.criar(authentication.getName(), request);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication authentication) {
        return pedidoService.listar(authentication.getName());
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(Authentication authentication, @PathVariable Long id) {
        return pedidoService.buscar(authentication.getName(), id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PedidoResponse atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusPedidoRequest request) {
        return pedidoService.atualizarStatus(id, request.status());
    }
}
