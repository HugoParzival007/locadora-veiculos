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

    // Labels globais para poder atualizar o texto dinamicamente
    private JLabel lblTotalVeiculos = new JLabel("0", SwingConstants.CENTER);
    private JLabel lblAlugados = new JLabel("0", SwingConstants.CENTER);
    private JLabel lblFaturamento = new JLabel("R$ 0,00", SwingConstants.CENTER);

    public MainGUI() {
        setTitle("Sistema de Locação - Hugo Guilherme");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Adiciona as abas
        tabs.addTab("📊 Dashboard", createDashboard());
        tabs.addTab("🚗 Veículos", new VeiculoPanel());
        tabs.addTab("🏢 Marcas", new MarcaPanel());
        tabs.addTab("👥 Clientes", new ClientePanel());
        tabs.addTab("📝 Contratos", new LocacaoPanel());

        // --- A MÁGICA ACONTECE AQUI ---
        // Adiciona um ouvinte: Toda vez que mudar de aba, executa isso
        tabs.addChangeListener(e -> {
            // Se a aba selecionada for a 0 (Dashboard), atualiza os dados
            if (tabs.getSelectedIndex() == 0) {
                atualizarDadosDashboard();
            }
        });

        // Carrega os dados na primeira abertura
        atualizarDadosDashboard();

        add(tabs);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        // Título e Usuário
        JLabel title = new JLabel("Visão Geral da Locadora", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));

        String usuarioInfo = "Usuário: " + (Sessao.getFuncionarioLogado() != null ? Sessao.getFuncionarioLogado().getNome() : "Desconhecido");
        JLabel lblUser = new JLabel(usuarioInfo, SwingConstants.RIGHT);
        lblUser.setBorder(new EmptyBorder(0,0,0,20));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(title, BorderLayout.CENTER);
        northPanel.add(lblUser, BorderLayout.EAST);
        panel.add(northPanel, BorderLayout.NORTH);

        // Painel de Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Configura a fonte dos labels de valor (que foram criados lá em cima)
        configurarLabelValor(lblTotalVeiculos);
        configurarLabelValor(lblAlugados);
        configurarLabelValor(lblFaturamento);

        // Cria os cards passando os Labels que já existem
        cardsPanel.add(createCard("Total Veículos", lblTotalVeiculos, new Color(60, 63, 65)));
        cardsPanel.add(createCard("Veículos Alugados", lblAlugados, new Color(0, 102, 204)));
        cardsPanel.add(createCard("Faturamento Total", lblFaturamento, new Color(40, 167, 69)));

        panel.add(cardsPanel, BorderLayout.CENTER);

        // Rodapé informativo simples
        JLabel footer = new JLabel("Os dados são atualizados automaticamente ao acessar esta aba.", SwingConstants.CENTER);
        footer.setBorder(new EmptyBorder(20,0,20,0));
        footer.setForeground(Color.GRAY);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // Método novo que vai no banco e atualiza os textos
    private void atualizarDadosDashboard() {
        // Usa Threads para não travar a tela enquanto busca no banco
        new SwingWorker<Void, Void>() {
            long totalVeiculos = 0;
            long alugados = 0;
            double faturamento = 0;

            @Override
            protected Void doInBackground() {
                GenericDAO<Veiculo> vDao = new GenericDAO<>();
                GenericDAO<ContratoLocacao> cDao = new GenericDAO<>();

                List<Veiculo> veiculos = vDao.listarTodos(Veiculo.class);
                List<ContratoLocacao> contratos = cDao.listarTodos(ContratoLocacao.class);

                totalVeiculos = veiculos.size();
                alugados = veiculos.stream().filter(v -> v.getStatus() == StatusVeiculo.LOCADO).count();
                faturamento = contratos.stream().mapToDouble(ContratoLocacao::getValorTotal).sum();
                return null;
            }

            @Override
            protected void done() {
                // Atualiza a interface gráfica
                lblTotalVeiculos.setText(String.valueOf(totalVeiculos));
                lblAlugados.setText(String.valueOf(alugados));
                lblFaturamento.setText(String.format("R$ %.2f", faturamento));
            }
        }.execute();
    }

    private void configurarLabelValor(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 40));
    }

    private JPanel createCard(String titulo, JLabel labelValor, Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTitulo.setBorder(new EmptyBorder(10,0,0,0));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(labelValor, BorderLayout.CENTER); // Adiciona o label global aqui

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
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}