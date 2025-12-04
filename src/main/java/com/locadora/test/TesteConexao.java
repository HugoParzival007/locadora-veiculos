package com.locadora.test;

import com.locadora.dao.HibernateUtil;

public class TesteConexao {
    public static void main(String[] args) {
        System.out.println("🧪 TESTANDO CONEXÃO HIBERNATE + POSTGRESQL");
        System.out.println("==========================================");

        try {
            // Isso vai inicializar o HibernateUtil
            var factory = HibernateUtil.getSessionFactory();

            System.out.println("\n✅✅✅ SUCESSO TOTAL! ✅✅✅");
            System.out.println("SessionFactory: " + factory);
            System.out.println("Status: " + (factory.isClosed() ? "Fechada" : "Aberta"));

            // Testa abrir uma sessão
            var session = factory.openSession();
            System.out.println("✅ Sessão aberta com sucesso!");
            session.close();
            System.out.println("✅ Sessão fechada!");

            HibernateUtil.shutdown();

        } catch (ExceptionInInitializerError e) {
            System.err.println("\n❌❌❌ ERRO DE INICIALIZAÇÃO ❌❌❌");
            System.err.println("O HibernateUtil falhou ao iniciar.");
            System.err.println("Provável causa: Configuração errada ou senha incorreta.");

        } catch (Exception e) {
            System.err.println("\n❌❌❌ ERRO GERAL ❌❌❌");
            e.printStackTrace();
        }
    }
}