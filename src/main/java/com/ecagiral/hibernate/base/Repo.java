package com.ecagiral.hibernate.base;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Repo {

    private static Repo instance;
    private static SessionFactory factory;

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure() // configures settings from hibernate.cfg.xml
            .build();

    public Repo(){
        //Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        try {
            factory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    public static Repo getInstance(){
        if(instance==null){
            instance = new Repo();
        }
        return instance;
    }

    public Session getSession(){
        return factory.openSession();
    }


}
