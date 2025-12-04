package com.locadora.view;

import com.locadora.dao.GenericDAO;
import com.locadora.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarcaPanel extends JPanel {
    private GenericDAO<Marca> marcaDAO = new GenericDAO<>();
    private GenericDAO<Modelo> modeloDAO = new GenericDAO<>();
    private JTable table;
    private DefaultTableModel model;
    // Formatador para o ano (ex: "2024")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

    public MarcaPanel() {
        setLayout(new BorderLayout());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo = new JButton("Nova Marca"), btnGerenciar = new JButton("Gerenciar Modelos"), btnExcluir = new JButton("Excluir Marca"), btnRef = new JButton("Atualizar");
        bp.add(btnNovo); bp.add(btnGerenciar); bp.add(btnExcluir); bp.add(btnRef);

        String[] col = {"ID", "Nome", "Qtd Modelos"};
        model = new DefaultTableModel(col, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        carregar();

        btnNovo.addActionListener(e -> {
            String n = JOptionPane.showInputDialog("Nome:");
            if(n!=null && !n.trim().isEmpty()) { marcaDAO.salvar(new Marca(n)); carregar(); }
        });
        btnGerenciar.addActionListener(e -> gerenciar());
        btnExcluir.addActionListener(e -> excluir());
        btnRef.addActionListener(e -> carregar());

        add(bp, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void carregar() {
        model.setRowCount(0);
        for(Marca m : marcaDAO.listarTodos(Marca.class))
            model.addRow(new Object[]{m.getId(), m.getNome(), m.getListModelo().size()});
    }

    private void excluir() {
        int r = table.getSelectedRow(); if(r==-1) return;
        if(JOptionPane.showConfirmDialog(this, "Excluir Marca e TODOS os Modelos?", "Confirma", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try {
                Long id = (Long)model.getValueAt(r, 0);
                Marca m = marcaDAO.buscar(Marca.class, id);
                if(m != null) {
                    marcaDAO.deletar(m);
                    carregar();
                }
            }
            catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void gerenciar() {
        int r = table.getSelectedRow(); if(r==-1) { JOptionPane.showMessageDialog(this, "Selecione uma marca"); return; }
        Long idMarca = (Long)model.getValueAt(r,0);
        Marca marcaRef = marcaDAO.buscar(Marca.class, idMarca);

        // Wrapper para permitir atualização da referência
        final Marca[] m = { marcaRef };

        JDialog d = new JDialog((Frame)null, "Gerenciar " + m[0].getNome(), true);
        d.setSize(500, 400); d.setLocationRelativeTo(this); d.setLayout(new BorderLayout());

        JPanel pTop = new JPanel(); JTextField txtNome = new JTextField(m[0].getNome(), 20); JButton btnSalvar = new JButton("Salvar Nome");
        pTop.add(new JLabel("Nome:")); pTop.add(txtNome); pTop.add(btnSalvar);

        DefaultListModel<Modelo> lm = new DefaultListModel<>();
        m[0].getListModelo().forEach(lm::addElement);
        JList<Modelo> list = new JList<>(lm);

        // Renderizador melhorado para mostrar o ano na lista
        list.setCellRenderer(ViewUtils.getRenderer(mod -> {
            Modelo md = (Modelo)mod;
            String anoStr = (md.getAno() != null) ? sdf.format(md.getAno()) : "-";
            return md.getNome() + " (" + anoStr + ")";
        }));

        JPanel pBtns = new JPanel();
        JButton add = new JButton("Add"), edit = new JButton("Edit"), del = new JButton("Del");
        pBtns.add(add); pBtns.add(edit); pBtns.add(del);

        btnSalvar.addActionListener(e -> {
            m[0].setNome(txtNome.getText());
            marcaDAO.atualizar(m[0]);
            JOptionPane.showMessageDialog(d, "Atualizado");
        });

        // --- LÓGICA DE ADICIONAR COM ANO ---
        add.addActionListener(e -> {
            JTextField fNome = new JTextField();
            JTextField fAno = new JTextField();

            // Painel personalizado
            JPanel inputPanel = new JPanel(new GridLayout(0, 1));
            inputPanel.add(new JLabel("Nome Modelo:")); inputPanel.add(fNome);
            inputPanel.add(new JLabel("Ano (yyyy):")); inputPanel.add(fAno);

            int result = JOptionPane.showConfirmDialog(d, inputPanel, "Novo Modelo", JOptionPane.OK_CANCEL_OPTION);

            if(result == JOptionPane.OK_OPTION) {
                try {
                    String nome = fNome.getText();
                    Date ano = sdf.parse(fAno.getText()); // Converte texto para Data

                    if(!nome.isEmpty()) {
                        Modelo mod = new Modelo(nome, ano);
                        mod.setQtModelo(0); // Padrão 0 conforme pedido
                        m[0].addModelo(mod);

                        marcaDAO.atualizar(m[0]);

                        // Recarrega para manter sincronia
                        m[0] = marcaDAO.buscar(Marca.class, idMarca);
                        lm.clear();
                        m[0].getListModelo().forEach(lm::addElement);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "Erro no Ano (use formato yyyy): " + ex.getMessage());
                }
            }
        });

        // --- LÓGICA DE EDITAR COM ANO ---
        edit.addActionListener(e -> {
            Modelo sel = list.getSelectedValue();
            if(sel!=null) {
                JTextField fNome = new JTextField(sel.getNome());
                JTextField fAno = new JTextField((sel.getAno() != null) ? sdf.format(sel.getAno()) : "");

                JPanel inputPanel = new JPanel(new GridLayout(0, 1));
                inputPanel.add(new JLabel("Nome Modelo:")); inputPanel.add(fNome);
                inputPanel.add(new JLabel("Ano (yyyy):")); inputPanel.add(fAno);

                int result = JOptionPane.showConfirmDialog(d, inputPanel, "Editar Modelo", JOptionPane.OK_CANCEL_OPTION);

                if(result == JOptionPane.OK_OPTION) {
                    try {
                        sel.setNome(fNome.getText());
                        sel.setAno(sdf.parse(fAno.getText()));

                        modeloDAO.atualizar(sel);

                        // Refresh visual
                        m[0] = marcaDAO.buscar(Marca.class, idMarca);
                        lm.clear();
                        m[0].getListModelo().forEach(lm::addElement);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(d, "Erro no Ano: " + ex.getMessage());
                    }
                }
            }
        });

        del.addActionListener(e -> {
            Modelo sel = list.getSelectedValue();
            if(sel!=null) {
                try {
                    m[0].getListModelo().remove(sel);
                    marcaDAO.atualizar(m[0]);
                    m[0] = marcaDAO.buscar(Marca.class, idMarca);
                    lm.clear();
                    m[0].getListModelo().forEach(lm::addElement);
                }
                catch(Exception ex) { ViewUtils.tratarErro(ex); }
            }
        });

        d.add(pTop, BorderLayout.NORTH);
        d.add(new JScrollPane(list), BorderLayout.CENTER); d.add(pBtns, BorderLayout.SOUTH);
        d.setVisible(true);
        carregar();
    }
}