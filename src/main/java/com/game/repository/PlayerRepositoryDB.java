package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "Maximyasniy11");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        this.sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        String sql = "select * from rpg.player";
        List<Player> result;
        try(Session session = sessionFactory.openSession()) {
            NativeQuery<Player> nativeQuery = session.createNativeQuery(sql, Player.class);
            nativeQuery.setFirstResult(pageNumber * pageSize);
            nativeQuery.setMaxResults(pageSize);
            result = nativeQuery.list();
        }
        return result;
    }

    @Override
    public int getAllCount() {
        Integer result;
        try(Session session = sessionFactory.openSession()) {
            Query<Integer> query = session.createNamedQuery("getAllCount", Integer.class);
            result = query.uniqueResult();
        }
        return result;
    }

    @Override
    public Player save(Player player) {
        Player result;
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Serializable save = session.save(player);
            result = (Player) save;

            transaction.commit();
        }
        return result;
    }

    @Override
    public Player update(Player player) {
        Player result;
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            result = (Player) session.merge(player);

            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public Optional<Player> findById(long id) {
        Optional<Player> result;
        try(Session session = sessionFactory.openSession()) {
            result = Optional.of(session.get(Player.class, id));
        }
        return result;
    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.remove(player);

            session.getTransaction().commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}