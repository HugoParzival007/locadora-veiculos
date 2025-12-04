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
        JButton btnNovo = new JButton("Novo Veículo"), btnExcluir = new JButton("Excluir"), btnStatus = new JButton("Status"), btnRef = new JButton("Atualizar Lista");
        bp.add(btnNovo); bp.add(btnStatus); bp.add(btnExcluir); bp.add(btnRef);

        // Colunas completas conforme o Model
        String[] col = {"ID", "Placa", "Modelo", "Marca", "Cor", "KM", "Chassi", "Renavam", "Status"};
        model = new DefaultTableModel(col, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        carregar();

        btnNovo.addActionListener(e -> novo());
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
            // Adicionado Chassi na visualização para ficar completo
            model.addRow(new Object[]{v.getId(), v.getPlaca(), mod, mar, v.getCor(), v.getKm(), v.getChassi(), v.getRenavam(), v.getStatus()});
        }
    }

    private void novo() {
        List<Marca> marcas = marcaDAO.listarTodos(Marca.class);
        JComboBox<Marca> cmbMar = new JComboBox<>(marcas.toArray(new Marca[0]));
        JComboBox<Modelo> cmbMod = new JComboBox<>();

        cmbMar.setRenderer(ViewUtils.getRenderer(m -> ((Marca)m).getNome()));
        cmbMod.setRenderer(ViewUtils.getRenderer(m -> ((Modelo)m).getNome()));

        cmbMar.addActionListener(e -> {
            cmbMod.removeAllItems();
            Marca m = (Marca) cmbMar.getSelectedItem();
            if(m!=null) for(Modelo mod : m.getListModelo()) cmbMod.addItem(mod);
        });
        if(marcas.size()>0) cmbMar.setSelectedIndex(0);

        JTextField placa=new JTextField(), cor=new JTextField(), km=new JTextField("0"), chassi=new JTextField(), ren=new JTextField();
        Object[] msg = {"Marca:", cmbMar, "Modelo:", cmbMod, "Placa:", placa, "Cor:", cor, "KM:", km, "Chassi:", chassi, "Renavam (Opcional 0km):", ren};

        if(JOptionPane.showConfirmDialog(this, msg, "Novo", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                if(chassi.getText().isEmpty()) throw new Exception("Chassi obrigatório");
                int k = Integer.parseInt(km.getText());
                if(k > 0 && ren.getText().isEmpty()) throw new Exception("Para usados (KM>0), Renavam é obrigatório!");

                Veiculo v = new Veiculo();
                v.setModelo((Modelo)cmbMod.getSelectedItem());
                v.setPlaca(placa.getText()); v.setCor(cor.getText()); v.setKm(k);
                v.setChassi(chassi.getText()); v.setRenavam(ren.getText()); v.setStatus(StatusVeiculo.DISPONIVEL);

                veiculoDAO.salvar(v);
                JOptionPane.showMessageDialog(this, "Salvo!");
                carregar();
            } catch(Exception ex) { ViewUtils.tratarErro(ex); }
        }
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