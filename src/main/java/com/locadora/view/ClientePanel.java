package com.locadora.view;

import com.locadora.dao.GenericDAO;
import com.locadora.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientePanel extends JPanel {
    private GenericDAO<Cliente> clienteDAO = new GenericDAO<>();
    private JTable table;
    private DefaultTableModel model;

    public ClientePanel() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnNovo = new JButton("Novo Cliente");
        JButton btnEditar = new JButton("Editar Cliente");
        JButton btnExcluir = new JButton("Excluir Cliente");
        JButton btnRefresh = new JButton("Atualizar");

        buttonPanel.add(btnNovo); buttonPanel.add(btnEditar); buttonPanel.add(btnExcluir); buttonPanel.add(btnRefresh);

        String[] colunas = {"ID", "Nome", "CPF", "CNH", "Email", "Cidade"};
        model = new DefaultTableModel(colunas, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        carregarTabela();

        btnNovo.addActionListener(e -> cadastrar());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnRefresh.addActionListener(e -> carregarTabela());

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        model.setRowCount(0);
        List<Cliente> clientes = clienteDAO.listarTodos(Cliente.class);
        for (Cliente c : clientes) {
            String email = (c.getContato() != null) ? c.getContato().getEmail() : "N/A";
            String cidade = (c.getEndereco() != null) ? c.getEndereco().getLogradouro() : "N/A";
            model.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getCnh(), email, cidade});
        }
    }

    private void cadastrar() {
        JTextField[] f = criarCampos();
        JPanel panel = montarPainel(f);

        if (JOptionPane.showConfirmDialog(this, panel, "Novo Cliente", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Contato contato = new Contato(f[3].getText(), f[4].getText(), f[5].getText());
                Endereco endereco = new Endereco(f[6].getText(), f[7].getText(), f[9].getText(), f[8].getText(), f[10].getText());
                Cliente c = new Cliente(f[0].getText(), f[1].getText(), f[2].getText(), contato, endereco);
                clienteDAO.salvar(c);
                JOptionPane.showMessageDialog(this, "Salvo!");
                carregarTabela();
            } catch (Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void editar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecione um cliente"); return; }
        Long id = (Long) model.getValueAt(row, 0);
        Cliente c = clienteDAO.buscar(Cliente.class, id);

        JTextField[] f = criarCampos();
        preencherCampos(f, c);
        JPanel panel = montarPainel(f);

        if (JOptionPane.showConfirmDialog(this, panel, "Editar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                atualizarObjeto(c, f);
                clienteDAO.atualizar(c);
                JOptionPane.showMessageDialog(this, "Atualizado!");
                carregarTabela();
            } catch (Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void excluir() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecione um cliente"); return; }
        Long id = (Long) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Excluir?", "Confirma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Cliente c = clienteDAO.buscar(Cliente.class, id);
                clienteDAO.deletar(c);
                carregarTabela();
            } catch (Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    // Auxiliares visuais
    private JTextField[] criarCampos() {
        JTextField[] f = new JTextField[11];
        for(int i=0; i<11; i++) f[i] = new JTextField();
        return f;
    }

    private JPanel montarPainel(JTextField[] f) {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("=== DADOS ===")); p.add(new JLabel(""));
        p.add(new JLabel("Nome:")); p.add(f[0]); p.add(new JLabel("CPF:")); p.add(f[1]); p.add(new JLabel("CNH:")); p.add(f[2]);
        p.add(new JLabel("=== CONTATO ===")); p.add(new JLabel(""));
        p.add(new JLabel("Email:")); p.add(f[3]); p.add(new JLabel("Tel:")); p.add(f[4]); p.add(new JLabel("Cel:")); p.add(f[5]);
        p.add(new JLabel("=== ENDEREÇO ===")); p.add(new JLabel(""));
        p.add(new JLabel("CEP:")); p.add(f[6]); p.add(new JLabel("Logradouro:")); p.add(f[7]); p.add(new JLabel("Número:")); p.add(f[8]);
        p.add(new JLabel("Complemento:")); p.add(f[9]); p.add(new JLabel("Referência:")); p.add(f[10]);
        return p;
    }

    private void preencherCampos(JTextField[] f, Cliente c) {
        f[0].setText(c.getNome()); f[1].setText(c.getCpf()); f[2].setText(c.getCnh());
        if(c.getContato()!=null){ f[3].setText(c.getContato().getEmail()); f[4].setText(c.getContato().getTelefone()); f[5].setText(c.getContato().getCelular()); }
        if(c.getEndereco()!=null){ f[6].setText(c.getEndereco().getCep()); f[7].setText(c.getEndereco().getLogradouro()); f[8].setText(c.getEndereco().getNumero()); f[9].setText(c.getEndereco().getComplemento()); f[10].setText(c.getEndereco().getReferencia()); }
    }

    private void atualizarObjeto(Cliente c, JTextField[] f) {
        c.setNome(f[0].getText()); c.setCpf(f[1].getText()); c.setCnh(f[2].getText());
        if(c.getContato()==null) c.setContato(new Contato());
        c.getContato().setEmail(f[3].getText()); c.getContato().setTelefone(f[4].getText()); c.getContato().setCelular(f[5].getText());
        if(c.getEndereco()==null) c.setEndereco(new Endereco());
        c.getEndereco().setCep(f[6].getText()); c.getEndereco().setLogradouro(f[7].getText()); c.getEndereco().setNumero(f[8].getText());
        c.getEndereco().setComplemento(f[9].getText()); c.getEndereco().setReferencia(f[10].getText());
    }
}