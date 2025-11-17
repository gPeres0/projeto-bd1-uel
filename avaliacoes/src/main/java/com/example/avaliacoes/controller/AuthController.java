package com.example.avaliacoes.controller;

import com.example.avaliacoes.model.Usuario;
import com.example.avaliacoes.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class AuthController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Mapeia para a URL raiz ou /login (GET)
     * Exibe o formulário de login
     */
    @GetMapping("/login")
    public String exibirFormularioLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login-usuario";
    }

    /**
     * Mapeia para a URL /login (POST)
     * Processa o envio do formulário e tenta autenticar
     */
    @PostMapping("/login")
    public String realizarLogin(@ModelAttribute Usuario usuario, RedirectAttributes attributes) {
        
        Optional<Usuario> autenticado = usuarioService.autenticar(usuario.getEmail(), usuario.getSenha());

        if (autenticado.isPresent()) {
            // Login bem-sucedido
            attributes.addFlashAttribute("mensagemSucesso", "Bem-vindo, " + autenticado.get().getNome() + "!");
            return "redirect:/dashboard";
        } else {
            // Login falhou
            attributes.addFlashAttribute("mensagemErro", "Email ou senha inválidos.");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /**
     * Mapeia para a URL /cadastro (GET)
     * Exibe o formulário de cadastro
     */
    @GetMapping("/cadastro")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro-usuario";
    }

    /**
     * Mapeia para a URL /cadastro (POST)
     * Recebe e salva o novo Usuário
     */
    @PostMapping("/cadastro")
    public String salvarCadastro(@ModelAttribute Usuario usuario, RedirectAttributes attributes) {        
        Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
        attributes.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso! Faça o login.");
        return "redirect:/login";
    }
}