package com.locadora.view;

import com.locadora.model.Funcionario;

public class Sessao {
    private static Funcionario funcionarioLogado;

    public static Funcionario getFuncionarioLogado() {
        return funcionarioLogado;
    }

    public static void setFuncionarioLogado(Funcionario funcionario) {
        funcionarioLogado = funcionario;
    }
}