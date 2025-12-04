package com.locadora.view;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.locadora.dao.GenericDAO;
import com.locadora.model.Contato;
import com.locadora.model.Endereco;
import com.locadora.model.Funcionario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginGUI extends JFrame {

    private GenericDAO<Funcionario> funcionarioDAO = new GenericDAO<>();

    public LoginGUI() {
        // Verifica se precisa criar um admin padrão
        verificarAdminPadrao();

        setTitle("Login - Locadora");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Painel Principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Título
        JLabel lblTitulo = new JLabel("Acesso ao Sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Campos
        JTextField txtLogin = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setBackground(new Color(0, 102, 204));
        btnEntrar.setForeground(Color.WHITE);

        // Adicionando componentes
        gbc.gridy = 0; panel.add(lblTitulo, gbc);

        gbc.gridy = 1; panel.add(new JLabel("Login:"), gbc);
        gbc.gridy = 2; panel.add(txtLogin, gbc);

        gbc.gridy = 3; panel.add(new JLabel("Senha:"), gbc);
        gbc.gridy = 4; panel.add(txtSenha, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(btnEntrar, gbc);

        add(panel, BorderLayout.CENTER);

        // Ação do Botão
        btnEntrar.addActionListener(e -> {
            String login = txtLogin.getText();
            String senha = new String(txtSenha.getPassword());
            autenticar(login, senha);
        });

        // Atalho para dar Enter e logar
        getRootPane().setDefaultButton(btnEntrar);
    }

    private void autenticar(String login, String senha) {
        List<Funcionario> lista = funcionarioDAO.listarTodos(Funcionario.class);

        for (Funcionario f : lista) {
            if (f.getLogin().equals(login) && f.getSenha().equals(senha)) {
                // Sucesso! Salva na sessão e abre o sistema
                Sessao.setFuncionarioLogado(f);

                JOptionPane.showMessageDialog(this, "Bem-vindo, " + f.getNome() + "!");
                new MainGUI().setVisible(true);
                this.dispose(); // Fecha tela de login
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Login ou Senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void verificarAdminPadrao() {
        List<Funcionario> lista = funcionarioDAO.listarTodos(Funcionario.class);
        if (lista.isEmpty()) {
            // Cria um usuário padrão se o banco estiver vazio
            Funcionario admin = new Funcionario();
            admin.setNome("Administrador");
            admin.setCpf("000.000.000-00");
            admin.setCargo("Gerente");
            admin.setLogin("admin");
            admin.setSenha("admin");
            admin.setContato(new Contato("admin@locadora.com", "", ""));
            admin.setEndereco(new Endereco("00000-000", "Rua Admin", "", "1", "Sede"));

            funcionarioDAO.salvar(admin);
            System.out.println("⚠️ Usuário 'admin' criado com senha 'admin'.");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatMacDarkLaf()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}