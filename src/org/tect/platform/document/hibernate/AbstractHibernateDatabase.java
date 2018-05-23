/**
 * Copyright (C) 2016 Fredrique. Samuels.
 * All rights reserved.  Email: fredriquesamuels@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of EITHER:
 *   (1) The GNU Lesser General Public License as published by the Free
 *       Software Foundation; either version 2.1 of the License, or (at
 *       your option) any later version. The text of the GNU Lesser
 *       General Public License is included with this library in the
 *       file LICENSE.TXT.
 *   (2) The BSD-style license that is included with this library in
 *       the file LICENSE-BSD.TXT.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files
 * LICENSE.TXT and LICENSE-BSD.TXT for more details.
 *
 * @Author: Fredrique Samuels fredriquesamuels@gmail.com
 */
package org.tect.platform.document.hibernate;


import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.List;
import java.util.function.Function;

/**
 * Created by fred on 2017/05/17.
 */
public class AbstractHibernateDatabase {
    private static final Logger logger = Logger.getLogger(AbstractHibernateDatabase.class);
    private final SessionFactory factory;

    public AbstractHibernateDatabase(Class[] classes) {
        factory = createSessionFactory(null, new String[]{}, classes, null);
    }

    public AbstractHibernateDatabase(DbCredentials credentials, Class ...classes) {
        factory = createSessionFactory(null, new String[]{}, classes, credentials);
    }

    public AbstractHibernateDatabase(String hbFile, Class[] classes) {
        factory = createSessionFactory(hbFile, new String[]{}, classes, null);
    }

    public AbstractHibernateDatabase(String hbFile, String[] packages, Class[] classes) {
        factory = createSessionFactory(hbFile, packages, classes, null);
    }

    public final <T> T get(Long id, Class<T> aClass) {
        Function<Session,T> transaction = (session) -> aClass.cast(session.get(aClass, id));
        return executeTransaction(transaction);
    }

    public final void updateObject(Object o) {
        Function<Session, Void> transaction = session -> {session.update(o);return null;};
        executeTransaction(transaction);
    }

    public final List getObjectList(Function<Session, Query> queryFactory) {
        Function<Session,List> transaction = (session) -> queryFactory.apply(session).list();
        return executeTransaction(transaction);
    }

    public final Object getUniqueResult(Function<Session, Query> queryFactory) {
        Function<Session,Object> transaction = (session) -> queryFactory.apply(session).uniqueResult();
        return executeTransaction(transaction);
    }

    public final List getCriteriaObjectList(Function<Session, Criteria> queryFactory) {
        Function<Session,List> transaction = (session) -> queryFactory.apply(session).list();
        return executeTransaction(transaction);
    }

    public final Object getCriteriaUniqueResult(Function<Session, Criteria> queryFactory) {
        Function<Session,Object> transaction = (session) -> queryFactory.apply(session).uniqueResult();
        return executeTransaction(transaction);
    }

    public final Long saveObject(Object object) {
        Function<Session,Long> transaction = (session) -> (Long) session.save(object);
        return executeTransaction(transaction);
    }

    public final void upsert(HibernateData hibernateData) {
        if(hibernateData.isPersisted()) {
            updateObject(hibernateData);
            hibernateData.getRelationShipData()
                    .forEach(c -> upsert(c));
        } else {
            saveObject(hibernateData);
            hibernateData.getRelationShipData()
                    .forEach(c -> upsert(c));
        }
    }

    public final <T> void deleteObject(T o) {
        if(o==null) return;
        Function<Session, Void> transaction = (session) -> {session.delete(o);return null;};
        executeTransaction(transaction);
    }

    private <T> T executeTransaction(Function<Session, T> transaction) {
        Transaction tx = null;
        Session session = factory.openSession();
        T result = null;
        try{
            tx = session.beginTransaction();
            result = transaction.apply(session);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            throw new RuntimeException(e);
        }finally {
            session.close();
        }
        return result;
    }

    private SessionFactory createSessionFactory(String hbFile, String[] packages, Class[] classes, DbCredentials credentials) {
        SessionFactory factory;
        try{
            AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
            if(hbFile!=null) {
                annotationConfiguration = annotationConfiguration.configure(hbFile);
            } else if(credentials!=null) {
                annotationConfiguration.setProperty("hibernate.connection.url", credentials.getUrl());
                annotationConfiguration = annotationConfiguration.setProperty("show_sql", "true");
                annotationConfiguration = annotationConfiguration.setProperty("format_sql", "true");
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.username", credentials.getUser());
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.password", credentials.getPassword());
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.dialect", getDialect(credentials));
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.driver_class", credentials.getDriver());
            } else {
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/default_db");
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/default_db");
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/default_db");
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.username", "root");
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.password", "");
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.dialect", getDialect(credentials));
                annotationConfiguration = annotationConfiguration.setProperty("hibernate.connection.driver_class", credentials.getDriver());
            }

            for (String packageName : packages ) {
                annotationConfiguration = annotationConfiguration.addPackage(packageName);
            }
            for (Class c : classes){
                annotationConfiguration = annotationConfiguration.addAnnotatedClass(c);
            }
            logger.info("Building hibernate config : " + hbFile);
            factory = annotationConfiguration.buildSessionFactory();
        }catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
        return factory;
    }

    private String getDialect(DbCredentials credentials) {
        if("org.sqlite.JDBC".equals(credentials.getDriver())) {
            return "com.applerao.hibernatesqlite.dialect.SQLiteDialect";
        }
        return "org.hibernate.dialect.MySQLDialect";
    }
}
