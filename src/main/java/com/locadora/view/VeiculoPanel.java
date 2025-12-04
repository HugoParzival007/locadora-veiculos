package com.locadora.view;

import com.locadora.dao.GenericDAO;
import com.locadora.model.*;
import com.locadora.model.enums.StatusVeiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VeiculoPanel extends JPanel {
    private GenericDAO<Veiculo> veiculoDAO = new GenericDAO<>();
    private GenericDAO<Marca> marcaDAO = new GenericDAO<>();
    private JTable table;
    private DefaultTableModel model;

    public VeiculoPanel() {
        setLayout(new BorderLayout());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Adicionado botão Editar
        JButton btnNovo = new JButton("Novo Veículo"), btnEditar = new JButton("Editar"), btnExcluir = new JButton("Excluir"), btnStatus = new JButton("Status"), btnRef = new JButton("Atualizar Lista");
        bp.add(btnNovo); bp.add(btnEditar); bp.add(btnStatus); bp.add(btnExcluir); bp.add(btnRef);

        String[] col = {"ID", "Placa", "Modelo", "Marca", "Cor", "KM", "Chassi", "Renavam", "Status"};
        model = new DefaultTableModel(col, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        carregar();

        btnNovo.addActionListener(e -> novo());
        btnEditar.addActionListener(e -> editar()); // Ação Editar
        btnStatus.addActionListener(e -> status());
        btnExcluir.addActionListener(e -> excluir());
        btnRef.addActionListener(e -> carregar());

        add(bp, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void carregar() {
        model.setRowCount(0);
        for(Veiculo v : veiculoDAO.listarTodos(Veiculo.class)) {
            String mod = (v.getModelo()!=null) ? v.getModelo().getNome() : "-";
            String mar = (v.getModelo()!=null && v.getModelo().getMarca()!=null) ? v.getModelo().getMarca().getNome() : "-";
            model.addRow(new Object[]{v.getId(), v.getPlaca(), mod, mar, v.getCor(), v.getKm(), v.getChassi(), v.getRenavam(), v.getStatus()});
        }
    }

    // Lógica unificada de formulário (usada por Novo e Editar)
    private void showForm(Veiculo v, String titulo) {
        List<Marca> marcas = marcaDAO.listarTodos(Marca.class);
        if (marcas.isEmpty()) { JOptionPane.showMessageDialog(this, "Cadastre Marcas primeiro!"); return; }

        JComboBox<Marca> cmbMar = new JComboBox<>(marcas.toArray(new Marca[0]));
        JComboBox<Modelo> cmbMod = new JComboBox<>();

        cmbMar.setRenderer(ViewUtils.getRenderer(m -> ((Marca)m).getNome()));
        cmbMod.setRenderer(ViewUtils.getRenderer(m -> ((Modelo)m).getNome()));

        cmbMar.addActionListener(e -> {
            cmbMod.removeAllItems();
            Marca m = (Marca) cmbMar.getSelectedItem();
            if(m!=null) for(Modelo mod : m.getListModelo()) cmbMod.addItem(mod);
        });

        // Configura seleção inicial (se for edição)
        if(v.getModelo() != null && v.getModelo().getMarca() != null) {
            // Gambiarra necessária para selecionar o item correto no combo carregado do banco
            for(int i=0; i<cmbMar.getItemCount(); i++) {
                if(((Marca)cmbMar.getItemAt(i)).getId().equals(v.getModelo().getMarca().getId())) {
                    cmbMar.setSelectedIndex(i);
                    break;
                }
            }
            // Atualiza modelos e seleciona
            for(int i=0; i<cmbMod.getItemCount(); i++) {
                if(((Modelo)cmbMod.getItemAt(i)).getId().equals(v.getModelo().getId())) {
                    cmbMod.setSelectedIndex(i);
                    break;
                }
            }
        } else if (marcas.size() > 0) {
            cmbMar.setSelectedIndex(0);
        }

        JTextField placa = new JTextField(v.getPlaca()), cor = new JTextField(v.getCor()),
                km = new JTextField(v.getKm() != null ? String.valueOf(v.getKm()) : "0"),
                chassi = new JTextField(v.getChassi()), ren = new JTextField(v.getRenavam());

        Object[] msg = { "Marca:", cmbMar, "Modelo:", cmbMod, "Placa:", placa, "Cor:", cor, "KM:", km, "Chassi (*):", chassi, "Renavam:", ren };

        if(JOptionPane.showConfirmDialog(this, msg, titulo, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if(chassi.getText().isEmpty()) throw new Exception("Chassi obrigatório");
                int k = Integer.parseInt(km.getText());
                if(k > 0 && ren.getText().isEmpty()) throw new Exception("Para usados (KM>0), Renavam é obrigatório!");

                v.setModelo((Modelo)cmbMod.getSelectedItem());
                v.setPlaca(placa.getText()); v.setCor(cor.getText()); v.setKm(k);
                v.setChassi(chassi.getText()); v.setRenavam(ren.getText());
                if(v.getStatus() == null) v.setStatus(StatusVeiculo.DISPONIVEL);

                if(v.getId() == null) veiculoDAO.salvar(v); // Novo
                else veiculoDAO.atualizar(v); // Editar

                JOptionPane.showMessageDialog(this, "Salvo!");
                carregar();
            } catch(Exception ex) { ViewUtils.tratarErro(ex); }
        }
    }

    private void novo() {
        showForm(new Veiculo(), "Novo Veículo");
    }

    private void editar() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Selecione um veículo"); return; }
        Long id = (Long)model.getValueAt(r, 0);
        Veiculo v = veiculoDAO.buscar(Veiculo.class, id);
        if(v != null) showForm(v, "Editar Veículo");
    }

    private void status() {
        int r = table.getSelectedRow(); if(r==-1) return;
        Veiculo v = veiculoDAO.buscar(Veiculo.class, (Long)model.getValueAt(r,0));
        StatusVeiculo s = (StatusVeiculo)JOptionPane.showInputDialog(this, "Status", "Mudar", JOptionPane.QUESTION_MESSAGE, null, StatusVeiculo.values(), v.getStatus());
        if(s!=null) { v.setStatus(s); veiculoDAO.atualizar(v); carregar(); }
    }

    private void excluir() {
        int r = table.getSelectedRow(); if(r==-1) return;
        if(JOptionPane.showConfirmDialog(this, "Excluir?", "Confirma", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try { veiculoDAO.deletar(veiculoDAO.buscar(Veiculo.class, (Long)model.getValueAt(r,0))); carregar(); }
            catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }
}