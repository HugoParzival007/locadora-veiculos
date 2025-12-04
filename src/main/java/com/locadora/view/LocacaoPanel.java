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

        JButton btnNovo = new JButton("Novo Contrato");
        JButton btnEditar = new JButton("Editar Contrato"); // Adicionado
        JButton btnAddOcor = new JButton("Add Ocorrência");
        JButton btnVerOcor = new JButton("Ver/Excluir Ocorrências");
        JButton btnDel = new JButton("Excluir Contrato");
        JButton btnRef = new JButton("Atualizar");

        bp.add(btnNovo); bp.add(btnEditar); bp.add(btnAddOcor); bp.add(btnVerOcor); bp.add(btnDel); bp.add(btnRef);

        String[] col = {"ID", "Cliente", "Veículo", "Data Saída", "Data Volta", "Valor Total", "Qtd Ocorr."};
        model = new DefaultTableModel(col, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        carregar();

        btnNovo.addActionListener(e -> novo());
        btnEditar.addActionListener(e -> editar()); // Ação
        btnAddOcor.addActionListener(e -> addOcorrencia());
        btnVerOcor.addActionListener(e -> verOcorrencias());
        btnDel.addActionListener(e -> excluir());
        btnRef.addActionListener(e -> carregar());

        add(bp, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void carregar() {
        model.setRowCount(0);
        for(ContratoLocacao c : contratoDAO.listarTodos(ContratoLocacao.class)) {
            String vInfo = "-"; String dSaida = "-"; String dVolta = "-"; int qtdOcor = 0;
            if(!c.getListaLocacao().isEmpty()) {
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

    // Helper para carregar formulário
    private void showForm(ContratoLocacao con, String titulo) {
        List<Veiculo> veics = veiculoDAO.listarTodos(Veiculo.class); // Traz todos para permitir troca na edição
        List<Cliente> clientes = clienteDAO.listarTodos(Cliente.class);

        JComboBox<Veiculo> cmbV = new JComboBox<>(veics.toArray(new Veiculo[0]));
        JComboBox<Cliente> cmbC = new JComboBox<>(clientes.toArray(new Cliente[0]));
        JComboBox<TipoPagamento> cmbPg = new JComboBox<>(TipoPagamento.values());

        cmbV.setRenderer(ViewUtils.getRenderer(v -> {
            String status = ((Veiculo)v).getStatus() == StatusVeiculo.DISPONIVEL ? "" : " [LOCADO]";
            return ((Veiculo)v).getPlaca() + " - " + ((Veiculo)v).getModelo().getNome() + status;
        }));
        cmbC.setRenderer(ViewUtils.getRenderer(c -> "CNH: " + ((Cliente)c).getCnh()));

        // Preencher campos se for edição
        Locacao locAtual = null;
        if(con.getId() != null && !con.getListaLocacao().isEmpty()) {
            locAtual = con.getListaLocacao().get(0);

            // Selecionar Cliente
            for(int i=0; i<cmbC.getItemCount(); i++) if(((Cliente)cmbC.getItemAt(i)).getId().equals(con.getCliente().getId())) cmbC.setSelectedIndex(i);

            // Selecionar Veículo (mesmo que esteja locado)
            if(locAtual.getVeiculo() != null) {
                for(int i=0; i<cmbV.getItemCount(); i++) if(((Veiculo)cmbV.getItemAt(i)).getId().equals(locAtual.getVeiculo().getId())) cmbV.setSelectedIndex(i);
            }

            // Selecionar Pagamento
            if(con.getPagamento() != null) cmbPg.setSelectedItem(con.getPagamento().getTipoPagamento());
        } else {
            // Se for novo, filtrar só disponíveis no combo visualmente ou selecionar o primeiro disponível
            // (Mantive todos na lista para edição, mas no Novo o ideal seria filtrar. Simplifiquei para reutilizar).
        }

        JTextField dias = new JTextField("1");
        JTextField valor = new JTextField(locAtual != null ? String.valueOf(locAtual.getValorLocacao()) : "0"); // Valor aproximado diaria
        JTextField caucao = new JTextField(con.getValorCaucao() > 0 ? String.valueOf(con.getValorCaucao()) : "0");

        // Tentar calcular dias na edição
        if(locAtual != null && locAtual.getDataRetirada() != null && locAtual.getDataDevolucao() != null) {
            long diff = locAtual.getDataDevolucao().getTime() - locAtual.getDataRetirada().getTime();
            long days = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS);
            dias.setText(String.valueOf(days > 0 ? days : 1));
            // Tenta reverter valor diaria
            if(days > 0) valor.setText(String.valueOf(locAtual.getValorLocacao() / days));
        }

        Object[] msg = {"Veículo:", cmbV, "Cliente:", cmbC, "Dias:", dias, "Valor Diária (R$):", valor, "Pagamento:", cmbPg, "Caução (R$):", caucao};

        if(JOptionPane.showConfirmDialog(this, msg, titulo, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int nDias = Integer.parseInt(dias.getText());
                float vDiaria = Float.parseFloat(valor.getText());
                float vCaucao = Float.parseFloat(caucao.getText());
                float custoLocacao = vDiaria * nDias;
                float valorFinalContrato = custoLocacao + vCaucao;

                Date hoje = new Date();
                Calendar cal = Calendar.getInstance(); cal.setTime(hoje); cal.add(Calendar.DATE, nDias);
                Date volta = cal.getTime();

                // Lógica de Veículo (Troca de Status se mudou)
                Veiculo veiculoSelecionado = (Veiculo)cmbV.getSelectedItem();

                // Se for edição e trocou de carro
                if(con.getId() != null && locAtual != null) {
                    Veiculo veiculoAntigo = locAtual.getVeiculo();
                    if(!veiculoAntigo.getId().equals(veiculoSelecionado.getId())) {
                        // Libera o antigo
                        veiculoAntigo.setStatus(StatusVeiculo.DISPONIVEL);
                        veiculoDAO.atualizar(veiculoAntigo);
                        // Loca o novo
                        if(veiculoSelecionado.getStatus() == StatusVeiculo.LOCADO) throw new Exception("Veículo selecionado já está locado!");
                        veiculoSelecionado.setStatus(StatusVeiculo.LOCADO);
                        veiculoDAO.atualizar(veiculoSelecionado);
                        locAtual.setVeiculo(veiculoSelecionado);
                    }
                } else {
                    // Novo
                    if(veiculoSelecionado.getStatus() == StatusVeiculo.LOCADO) throw new Exception("Veículo já está locado!");
                    veiculoSelecionado.setStatus(StatusVeiculo.LOCADO);
                    veiculoDAO.atualizar(veiculoSelecionado);
                }

                // Atualiza Locação
                Locacao loc = (con.getId() == null) ? new Locacao() : con.getListaLocacao().get(0);
                if(con.getId() == null) loc.setVeiculo(veiculoSelecionado);

                loc.setDataRetirada(hoje);
                loc.setDataDevolucao(volta);
                loc.setValorLocacao(custoLocacao);

                // Atualiza Contrato
                con.setDataContrato(hoje);
                con.setCliente((Cliente)cmbC.getSelectedItem());
                con.setStatus(StatusContrato.ATIVO);
                con.setValorTotal(valorFinalContrato);
                con.setValorCaucao(vCaucao);
                if(con.getId() == null) con.setFuncionario(Sessao.getFuncionarioLogado());

                // Pagamento
                if(con.getPagamento() == null) con.setPagamento(new Pagamento());
                con.getPagamento().setTipoPagamento((TipoPagamento)cmbPg.getSelectedItem());
                con.getPagamento().setValorTotal(valorFinalContrato);

                if(con.getId() == null) con.getListaLocacao().add(loc);

                if(con.getId() == null) contratoDAO.salvar(con);
                else contratoDAO.atualizar(con);

                JOptionPane.showMessageDialog(this, "Salvo com Sucesso!");
                carregar();

            } catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void novo() {
        showForm(new ContratoLocacao(), "Novo Contrato");
    }

    private void editar() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Selecione um contrato"); return; }
        Long id = (Long)model.getValueAt(r, 0);
        ContratoLocacao c = contratoDAO.buscar(ContratoLocacao.class, id);
        if(c != null) showForm(c, "Editar Contrato");
    }

    // ... (Demais métodos: addOcorrencia, verOcorrencias, excluir permanecem iguais) ...
    private void addOcorrencia() {
        int r = table.getSelectedRow(); if(r == -1) { JOptionPane.showMessageDialog(this, "Selecione um contrato."); return; }
        Long idContrato = (Long)model.getValueAt(r,0);
        ContratoLocacao c = contratoDAO.buscar(ContratoLocacao.class, idContrato);
        if(c == null || c.getListaLocacao().isEmpty()) return;
        Locacao l = c.getListaLocacao().get(0);
        String d = JOptionPane.showInputDialog("Descrição:"); if(d == null) return;
        String vStr = JOptionPane.showInputDialog("Valor (R$):");
        try { l.addOcorrencia(new Ocorrencia(d, Float.parseFloat(vStr))); locacaoDAO.atualizar(l); JOptionPane.showMessageDialog(this, "Registrada!"); carregar(); } catch(Exception e) { ViewUtils.tratarErro(e); }
    }

    private void verOcorrencias() {
        int r = table.getSelectedRow(); if(r == -1) { JOptionPane.showMessageDialog(this, "Selecione um contrato."); return; }
        Long idContrato = (Long)model.getValueAt(r,0); ContratoLocacao c = contratoDAO.buscar(ContratoLocacao.class, idContrato);
        if (c == null) { carregar(); return; }
        if(c.getListaLocacao() == null || c.getListaLocacao().isEmpty()) { JOptionPane.showMessageDialog(this, "Sem locações."); return; }
        Locacao l = c.getListaLocacao().get(0); List<Ocorrencia> list = l.getListaOcorrencias();
        if(list == null || list.isEmpty()) { JOptionPane.showMessageDialog(this, "Sem ocorrências."); return; }
        JComboBox<Ocorrencia> box = new JComboBox<>(list.toArray(new Ocorrencia[0]));
        box.setRenderer(ViewUtils.getRenderer(o -> ((Ocorrencia)o).getDescricao() + " | R$ " + ((Ocorrencia)o).getValor()));
        if(JOptionPane.showConfirmDialog(this, box, "Excluir?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Ocorrencia o = (Ocorrencia)box.getSelectedItem();
            try { l.getListaOcorrencias().remove(o); ocorrenciaDAO.deletar(o); JOptionPane.showMessageDialog(this, "Excluído."); carregar(); } catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }

    private void excluir() {
        int r = table.getSelectedRow(); if(r == -1) return;
        if(JOptionPane.showConfirmDialog(this, "Excluir Contrato? Carro será liberado.", "Confirma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long)model.getValueAt(r,0); ContratoLocacao con = contratoDAO.buscar(ContratoLocacao.class, id);
                if(con != null) {
                    if(con.getListaLocacao() != null) for(Locacao l : con.getListaLocacao()) if(l.getVeiculo() != null) { l.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL); veiculoDAO.atualizar(l.getVeiculo()); }
                    contratoDAO.deletar(con); carregar(); JOptionPane.showMessageDialog(this, "Excluído.");
                } else carregar();
            } catch(Exception e) { ViewUtils.tratarErro(e); }
        }
    }
}