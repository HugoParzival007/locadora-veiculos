package com.locadora.view;

import com.locadora.dao.GenericDAO;
import com.locadora.model.*;
import com.locadora.model.enums.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

public class LocacaoPanel extends JPanel {
    // DAOs necessários para montar o processo
    private GenericDAO<ContratoLocacao> contratoDAO = new GenericDAO<>();
    private GenericDAO<Veiculo> veiculoDAO = new GenericDAO<>();
    private GenericDAO<Cliente> clienteDAO = new GenericDAO<>();
    private GenericDAO<Locacao> locacaoDAO = new GenericDAO<>();
    private GenericDAO<Ocorrencia> ocorrenciaDAO = new GenericDAO<>();

    private JTable table;
    private DefaultTableModel model;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public LocacaoPanel() {
        setLayout(new BorderLayout());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Botões
        JButton btnNovo = new JButton("Novo Contrato");
        JButton btnAddOcor = new JButton("Add Ocorrência");
        JButton btnVerOcor = new JButton("Ver/Excluir Ocorrências");
        JButton btnDel = new JButton("Excluir Contrato");
        JButton btnRef = new JButton("Atualizar");

        bp.add(btnNovo);
        bp.add(btnAddOcor);
        bp.add(btnVerOcor);
        bp.add(btnDel);
        bp.add(btnRef);

        // Colunas da Tabela
        String[] col = {"ID", "Cliente", "Veículo", "Data Saída", "Data Volta", "Valor Total", "Qtd Ocorr."};
        model = new DefaultTableModel(col, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        carregar();

        // Ações
        btnNovo.addActionListener(e -> novo());
        btnAddOcor.addActionListener(e -> addOcorrencia());
        btnVerOcor.addActionListener(e -> verOcorrencias());
        btnDel.addActionListener(e -> excluir());
        btnRef.addActionListener(e -> carregar());

        add(bp, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void carregar() {
        model.setRowCount(0);
        // Lista Contratos, mas mostra detalhes da Locação interna
        for(ContratoLocacao c : contratoDAO.listarTodos(ContratoLocacao.class)) {
            String vInfo = "-";
            String dSaida = "-";
            String dVolta = "-";
            int qtdOcor = 0;

            // Pega dados da primeira locação do contrato
            if(c.getListaLocacao() != null && !c.getListaLocacao().isEmpty()) {
                Locacao l = c.getListaLocacao().get(0);
                if(l.getVeiculo() != null) vInfo = l.getVeiculo().getPlaca() + " (" + l.getVeiculo().getModelo().getNome() + ")";
                if(l.getDataRetirada() != null) dSaida = sdf.format(l.getDataRetirada());
                if(l.getDataDevolucao() != null) dVolta = sdf.format(l.getDataDevolucao());
                if(l.getListaOcorrencias() != null) qtdOcor = l.getListaOcorrencias().size();
            }

            String cli = (c.getCliente() != null) ? c.getCliente().getCnh() : "Sem Cliente";

            model.addRow(new Object[]{ c.getId(), cli, vInfo, dSaida, dVolta, "R$ " + c.getValorTotal(), qtdOcor });
        }
    }

    private void novo() {
        List<Veiculo> veics = new ArrayList<>();
        for(Veiculo v : veiculoDAO.listarTodos(Veiculo.class)) {
            if(v.getStatus() == StatusVeiculo.DISPONIVEL) veics.add(v);
        }

        if(veics.isEmpty()) { JOptionPane.showMessageDialog(this, "Não há veículos DISPONÍVEIS."); return; }
        List<Cliente> clientes = clienteDAO.listarTodos(Cliente.class);
        if(clientes.isEmpty()) { JOptionPane.showMessageDialog(this, "Cadastre um Cliente primeiro."); return; }

        JComboBox<Veiculo> cmbV = new JComboBox<>(veics.toArray(new Veiculo[0]));
        JComboBox<Cliente> cmbC = new JComboBox<>(clientes.toArray(new Cliente[0]));
        JComboBox<TipoPagamento> cmbPg = new JComboBox<>(TipoPagamento.values());

        cmbV.setRenderer(ViewUtils.getRenderer(v -> ((Veiculo)v).getPlaca() + " - " + ((Veiculo)v).getModelo().getNome()));
        cmbC.setRenderer(ViewUtils.getRenderer(c -> "CNH: " + ((Cliente)c).getCnh()));

        JTextField dias = new JTextField();
        JTextField valor = new JTextField();
        JTextField caucao = new JTextField("0");

        Object[] msg = {
                "Veículo:", cmbV,
                "Cliente:", cmbC,
                "Dias:", dias,
                "Valor Diária (R$):", valor,
                "Pagamento:", cmbPg,
                "Caução (R$):", caucao
        };

        if(JOptionPane.showConfirmDialog(this, msg, "Novo Contrato de Locação", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int nDias = Integer.parseInt(dias.getText());
                float vDiaria = Float.parseFloat(valor.getText());
                float vCaucao = Float.parseFloat(caucao.getText());
                float custoLocacao = vDiaria * nDias;
                float valorFinalContrato = custoLocacao + vCaucao;

                Date hoje = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(hoje);
                cal.add(Calendar.DATE, nDias);
                Date volta = cal.getTime();

                Locacao loc = new Locacao();
                loc.setVeiculo((Veiculo)cmbV.getSelectedItem());
                loc.setDataRetirada(hoje);
                loc.setDataDevolucao(volta);
                loc.setValorLocacao(custoLocacao);

                ContratoLocacao con = new ContratoLocacao();
                con.setDataContrato(hoje);
                con.setCliente((Cliente)cmbC.getSelectedItem());
                con.setStatus(StatusContrato.ATIVO);
                con.setValorTotal(valorFinalContrato);
                con.setValorCaucao(vCaucao);
                con.setFuncionario(Sessao.getFuncionarioLogado()); // Vínculo com funcionário logado

                con.setPagamento(new Pagamento((TipoPagamento)cmbPg.getSelectedItem(), valorFinalContrato));
                con.getListaLocacao().add(loc);

                Veiculo v = (Veiculo)cmbV.getSelectedItem();
                v.setStatus(StatusVeiculo.LOCADO);
                veiculoDAO.atualizar(v);

                contratoDAO.salvar(con);

                JOptionPane.showMessageDialog(this, "Contrato Gerado! Total: R$ " + valorFinalContrato);
                carregar();

            } catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void addOcorrencia() {
        int r = table.getSelectedRow(); if(r == -1) { JOptionPane.showMessageDialog(this, "Selecione um contrato."); return; }

        Long idContrato = (Long)model.getValueAt(r,0);
        ContratoLocacao c = contratoDAO.buscar(ContratoLocacao.class, idContrato);

        if(c == null || c.getListaLocacao().isEmpty()) return;
        Locacao l = c.getListaLocacao().get(0);

        String d = JOptionPane.showInputDialog("Descrição da Ocorrência:");
        if(d == null) return;
        String vStr = JOptionPane.showInputDialog("Valor (R$):");

        try {
            float val = Float.parseFloat(vStr);
            Ocorrencia o = new Ocorrencia(d, val);
            l.addOcorrencia(o);
            locacaoDAO.atualizar(l);
            JOptionPane.showMessageDialog(this, "Ocorrência registrada!");
            carregar();
        } catch(Exception e) { ViewUtils.tratarErro(e); }
    }

    private void verOcorrencias() {
        int r = table.getSelectedRow();
        if(r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contrato.");
            return;
        }

        Long idContrato = (Long)model.getValueAt(r,0);
        ContratoLocacao c = contratoDAO.buscar(ContratoLocacao.class, idContrato);

        // --- CORREÇÃO DO NULL POINTER ---
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Erro: Contrato não encontrado (talvez excluído). Atualizando lista...", "Erro", JOptionPane.ERROR_MESSAGE);
            carregar();
            return;
        }

        if(c.getListaLocacao() == null || c.getListaLocacao().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma locação associada a este contrato.");
            return;
        }

        Locacao l = c.getListaLocacao().get(0);
        List<Ocorrencia> list = l.getListaOcorrencias();

        if(list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma ocorrência registrada.");
            return;
        }

        JComboBox<Ocorrencia> box = new JComboBox<>(list.toArray(new Ocorrencia[0]));
        box.setRenderer(ViewUtils.getRenderer(o -> ((Ocorrencia)o).getDescricao() + " | R$ " + ((Ocorrencia)o).getValor()));

        if(JOptionPane.showConfirmDialog(this, box, "Excluir Ocorrência", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Ocorrencia o = (Ocorrencia)box.getSelectedItem();
            try {
                l.getListaOcorrencias().remove(o); // Remove da lista
                ocorrenciaDAO.deletar(o);          // Remove do banco
                JOptionPane.showMessageDialog(this, "Excluído com sucesso.");
                carregar();
            } catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void excluir() {
        int r = table.getSelectedRow(); if(r == -1) return;

        if(JOptionPane.showConfirmDialog(this, "Excluir Contrato?\nO veículo será liberado automaticamente.", "Confirma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long)model.getValueAt(r,0);
                ContratoLocacao con = contratoDAO.buscar(ContratoLocacao.class, id);

                // Libera os veículos antes de apagar
                if(con != null && con.getListaLocacao() != null) {
                    for(Locacao l : con.getListaLocacao()) {
                        if(l.getVeiculo() != null) {
                            l.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL);
                            veiculoDAO.atualizar(l.getVeiculo());
                        }
                    }
                }

                contratoDAO.deletar(con);
                carregar();
                JOptionPane.showMessageDialog(this, "Contrato excluído.");
            } catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }
}