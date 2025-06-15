package com.teatroabc.admin.infraestrutura.ui_swing.controllers;

import com.teatroabc.admin.aplicacao.dto.LoginDTO;
import com.teatroabc.admin.aplicacao.interfaces.IAutenticacaoServico;
import com.teatroabc.admin.dominio.entidades.Usuario;
import com.teatroabc.admin.infraestrutura.ui_swing.telas.TelaLogin;
import com.teatroabc.admin.infraestrutura.ui_swing.telas.TelaPrincipalAdmin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Classe responsável pela lógica de controle da Tela de Login.
 * Gerencia as ações do usuário, valida os dados e interage com o serviço de autenticação.
 */
public class ControladorLogin {

    private final IAutenticacaoServico autenticacaoServico;
    private final TelaLogin telaLoginView;
    private final JTextField txtUsuario;
    private final JPasswordField txtSenha;
    private final JButton btnEntrar;
    private final JLabel lblMensagem;
    private final Consumer<Usuario> onLoginSuccess;

    public ControladorLogin(IAutenticacaoServico autenticacaoServico, TelaLogin telaLoginView,
                            JTextField txtUsuario, JPasswordField txtSenha, JButton btnEntrar,
                            JLabel lblMensagem, Consumer<Usuario> onLoginSuccess) {
        this.autenticacaoServico = autenticacaoServico;
        this.telaLoginView = telaLoginView;
        this.txtUsuario = txtUsuario;
        this.txtSenha = txtSenha;
        this.btnEntrar = btnEntrar;
        this.lblMensagem = lblMensagem;
        this.onLoginSuccess = onLoginSuccess;
    }

    /**
     * Anexa os listeners de eventos aos componentes da UI.
     */
    public void registrarAcoes() {
        btnEntrar.addActionListener(e -> realizarLogin());
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        });
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSenha.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Executa o processo de login.
     */
    private void realizarLogin() {
        String username = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (username.isEmpty() || senha.isEmpty()) {
            definirMensagem("Preencha todos os campos", true);
            return;
        }

        // Desativa a UI e mostra feedback
        setUiAtiva(false);
        definirMensagem("Autenticando...", false);
        telaLoginView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Usa SwingWorker para não travar a UI
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                // Simula um pequeno delay para melhor experiência do usuário
                Thread.sleep(500);
                LoginDTO loginDTO = new LoginDTO(username, senha);
                return autenticacaoServico.autenticar(loginDTO);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        // Notifica o sucesso do login
                        onLoginSuccess.accept(usuario);
                    } else {
                        definirMensagem("Usuário ou senha incorretos", true);
                        txtSenha.setText("");
                        txtSenha.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    definirMensagem("Erro na comunicação com o serviço.", true);
                    ex.printStackTrace();
                } finally {
                    // Reativa a UI
                    setUiAtiva(true);
                    telaLoginView.setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
    
    private void setUiAtiva(boolean ativa) {
        btnEntrar.setEnabled(ativa);
        txtUsuario.setEnabled(ativa);
        txtSenha.setEnabled(ativa);
    }
    
    private void definirMensagem(String texto, boolean isErro) {
        lblMensagem.setText(texto);
        lblMensagem.setForeground(isErro ? new Color(231, 76, 60) : new Color(180, 180, 180));
    }
}
