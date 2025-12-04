package com.locadora.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestPostgreSQLConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/locadora_veiculos";
        String user = "postgres";
        String password = "Devastador007"; // SUA SENHA REAL!

        System.out.println("🔍 Testando conexão com PostgreSQL...");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ CONEXÃO BEM-SUCEDIDA!");
                System.out.println("✅ Banco de Dados: " + conn.getCatalog());
                System.out.println("✅ PostgreSQL " + conn.getMetaData().getDatabaseProductVersion());
            }
        } catch (Exception e) {
            System.err.println("❌ FALHA NA CONEXÃO:");
            System.err.println("URL: " + url);
            System.err.println("Usuário: " + user);
            System.err.println("Erro: " + e.getMessage());

            // Dicas de troubleshooting
            System.err.println("\n🔧 DICAS PARA RESOLVER:");
            System.err.println("1. Verifique se PostgreSQL está rodando");
            System.err.println("2. Confira a senha no pgAdmin 4");
            System.err.println("3. Teste a conexão no pgAdmin primeiro");
            System.err.println("4. Porta padrão do PostgreSQL: 5432");
        }
    }
}