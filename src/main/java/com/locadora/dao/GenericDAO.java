package com.locadora.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.locadora.dao.HibernateUtil;
import java.io.Serializable;
import java.util.List;

public class GenericDAO<T> {

    public void salvar(T entidade) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(entidade);
        tx.commit();
        session.close();
    }

    public T buscar(Class<T> clazz, Serializable id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        T obj = session.find(clazz, id);
        session.close();
        return obj;
    }

    public void atualizar(T entidade) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(entidade);
        tx.commit();
        session.close();
    }

    public void deletar(Object entidade) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.remove(entidade);
        tx.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public List<T> listarTodos(Class<T> clazz) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<T> lista = session.createQuery("from " + clazz.getSimpleName()).list();
        session.close();
        return lista;
    }
}
