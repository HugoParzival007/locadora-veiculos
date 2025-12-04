package com.locadora.view;

import javax.swing.*;
import java.awt.*;
import org.hibernate.exception.ConstraintViolationException;
import jakarta.persistence.PersistenceException;

public class ViewUtils {

    // Tratamento de erro aprimorado para encontrar a causa raiz
    public static void tratarErro(Exception e) {
        Throwable cause = e;
        boolean isConstraint = false;

        // Procura recursivamente pela ConstraintViolationException
        while (cause != null) {
            if (cause instanceof ConstraintViolationException ||
                    (cause.getMessage() != null && cause.getMessage().contains("violates foreign key"))) {
                isConstraint = true;
                break;
            }
            cause = cause.getCause();
        }

        if(isConstraint) {
            JOptionPane.showMessageDialog(null,
                    "⛔ AÇÃO BLOQUEADA!\n\n" +
                            "Este registro está sendo usado em outra parte do sistema.\n" +
                            "(Ex: Veículo alugado, Cliente com contrato ativo).\n" +
                            "Não é possível excluir enquanto houver vínculos.",
                    "Proteção de Dados", JOptionPane.ERROR_MESSAGE);
        } else {
            e.printStackTrace(); // Mantém no console para debug
            JOptionPane.showMessageDialog(null, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Renderizador genérico
    public static DefaultListCellRenderer getRenderer(java.util.function.Function<Object, String> textMapper) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(textMapper.apply(value));
                }
                return this;
            }
        };
    }
}