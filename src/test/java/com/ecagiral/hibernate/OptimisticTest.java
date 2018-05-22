package com.ecagiral.hibernate;

import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Customer;
import com.ecagiral.hibernate.base.service.PaymentJob;
import junit.framework.TestCase;
import org.hibernate.Session;

import javax.persistence.LockModeType;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptimisticTest extends TestCase {

    private Repo repo;

    private Logger LOG = Logger.getLogger(OptimisticTest.class.getName());
    private final int THREAD_COUNT = 3;
    private Integer customerId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        repo = Repo.getInstance();
        Session session = repo.getSession();
        session.beginTransaction();

        Customer customer = new Customer(UUID.randomUUID().toString());
        session.save(customer);
        customerId = customer.getId();

        session.getTransaction().commit();
        session.close();
    }

    public void testOptimistic()
    {
        LOG.info("Optimistic Lock -> checks version and throws exception if any other thread changes data before committing");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for(int i = 0;i<THREAD_COUNT;i++) {
            executor.submit(new PaymentJob(i,customerId, LockModeType.OPTIMISTIC));
        }
        executor.shutdown();
        try {
            boolean term = executor.awaitTermination(20000, TimeUnit.MILLISECONDS);
            if(!term){
                LOG.info("TIMEOUT!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Session session2 = repo.getSession();
        session2.beginTransaction();
        assertEquals(1, session2.find(Customer.class,customerId).getPayment().intValue());
        session2.getTransaction().commit();
        session2.close();
    }
}
