package com.ecagiral.hibernate.base.service;


import com.ecagiral.hibernate.base.Repo;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;

import java.io.FileInputStream;
import java.io.IOException;

public class Startup {

    public static void initDbFromFile() throws IOException {
        FileInputStream fis = new FileInputStream("src/main/resources/init.sql");
        String data = IOUtils.toString(fis, "UTF-8");
        Repo repo = Repo.getInstance();
        Session session = repo.getSession();
        session.beginTransaction();
        session.createNativeQuery(data).executeUpdate();
        session.getTransaction().commit();
        session.close();

    }
}
