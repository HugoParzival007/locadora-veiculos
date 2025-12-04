package com.locadora.view;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.locadora.dao.GenericDAO;
import com.locadora.model.*;
import com.locadora.model.enums.StatusVeiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("Sistema de Locação - Hugo Guilherme");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("📊 Dashboard", createDashboard());
        tabs.addTab("🚗 Veículos", new VeiculoPanel());
        tabs.addTab("🏢 Marcas", new MarcaPanel());
        tabs.addTab("👥 Clientes", new ClientePanel());
        tabs.addTab("📝 Contratos", new LocacaoPanel());

        add(tabs);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Visão Geral da Locadora", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Exibir quem está logado
        String usuarioInfo = "Usuário: " + (Sessao.getFuncionarioLogado() != null ? Sessao.getFuncionarioLogado().getNome() : "Desconhecido");
        JLabel lblUser = new JLabel(usuarioInfo, SwingConstants.RIGHT);
        lblUser.setBorder(new EmptyBorder(0,0,0,20));
        panel.add(lblUser, BorderLayout.NORTH);

        // Adiciona o título no norte mas dentro de um painel para ter o user junto
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(title, BorderLayout.CENTER);
        northPanel.add(lblUser, BorderLayout.EAST);
        panel.add(northPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GenericDAO<Veiculo> vDao = new GenericDAO<>();
        GenericDAO<ContratoLocacao> cDao = new GenericDAO<>();

        List<Veiculo> veiculos = vDao.listarTodos(Veiculo.class);
        List<ContratoLocacao> contratos = cDao.listarTodos(ContratoLocacao.class);

        long totalVeiculos = veiculos.size();
        long alugados = veiculos.stream().filter(v -> v.getStatus() == StatusVeiculo.LOCADO).count();
        double faturamento = contratos.stream().mapToDouble(ContratoLocacao::getValorTotal).sum();

        cardsPanel.add(createCard("Total Veículos", String.valueOf(totalVeiculos), new Color(60, 63, 65)));
        cardsPanel.add(createCard("Veículos Alugados", String.valueOf(alugados), new Color(0, 102, 204)));
        cardsPanel.add(createCard("Faturamento Total", String.format("R$ %.2f", faturamento), new Color(40, 167, 69)));

        JButton btnRefresh = new JButton("Atualizar Dados");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnRefresh.addActionListener(e -> {
            new MainGUI().setVisible(true);
            this.dispose();
        });

        JPanel footer = new JPanel();
        footer.add(btnRefresh);

        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(String titulo, String valor, Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 40));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Alterado para iniciar pelo Login
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}