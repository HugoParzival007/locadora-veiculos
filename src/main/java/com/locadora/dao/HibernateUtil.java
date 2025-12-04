package com.locadora.dao;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Properties;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            System.out.println("🔧 Inicializando Hibernate...");

            // Carregar propriedades manualmente
            Properties properties = new Properties();
            properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/locadora_veiculos");
            properties.setProperty("hibernate.connection.username", "postgres");
            properties.setProperty("hibernate.connection.password", "Devastador007");
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.setProperty("hibernate.hbm2ddl.auto", "update");
            properties.setProperty("hibernate.show_sql", "true");
            properties.setProperty("hibernate.format_sql", "true");

            // Configurações do pool de conexão C3P0
            properties.setProperty("hibernate.c3p0.min_size", "5");
            properties.setProperty("hibernate.c3p0.max_size", "20");
            properties.setProperty("hibernate.c3p0.timeout", "300");
            properties.setProperty("hibernate.c3p0.idle_test_period", "3000");

            System.out.println("✅ Configurações carregadas");
            System.out.println("🔗 URL: " + properties.getProperty("hibernate.connection.url"));

            // Criar registro de serviço
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(properties)
                    .build();

            System.out.println("✅ ServiceRegistry criado");

            // Registra TODAS as classes de entidade
            MetadataSources metadataSources = new MetadataSources(standardRegistry);

            // Adiciona cada classe de entidade manualmente
            metadataSources.addAnnotatedClass(com.locadora.model.Marca.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Modelo.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Veiculo.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Categoria.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Manutencao.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Ocorrencia.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Locacao.class);
            metadataSources.addAnnotatedClass(com.locadora.model.ContratoLocacao.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Contato.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Endereco.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Usuario.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Cliente.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Funcionario.class);
            metadataSources.addAnnotatedClass(com.locadora.model.Pagamento.class);

            System.out.println("✅ " + metadataSources.getAnnotatedClasses().size() + " classes registradas");

            Metadata metadata = metadataSources.buildMetadata();
            sessionFactory = metadata.buildSessionFactory();

            System.out.println("✅ SessionFactory criada com sucesso!");
            System.out.println("✅ Banco: PostgreSQL");

        } catch (Exception e) {
            System.err.println("❌ ERRO ao inicializar Hibernate: " + e.getMessage());
            System.err.println("Detalhes do erro:");
            e.printStackTrace();
            throw new ExceptionInInitializerError("Falha ao criar SessionFactory: " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory não foi inicializada!");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("✅ SessionFactory fechada.");
        }
    }
}