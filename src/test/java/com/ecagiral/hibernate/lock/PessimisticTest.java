package com.ecagiral.hibernate.lock;

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

public class PessimisticTest extends TestCase {

    private Repo repo;

    private Logger LOG = Logger.getLogger(PessimisticTest.class.getName());
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

    public void testPessimisticWrite()
    {
        LOG.info("PessimisticWrite Lock -> threads wait for others to finish transaction");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for(int i = 0;i<THREAD_COUNT;i++) {
            executor.submit(new PaymentJob(20l,20l,i,customerId, LockModeType.PESSIMISTIC_WRITE));
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
        assertEquals(3, session2.find(Customer.class,customerId).getPayment().intValue());
        session2.getTransaction().commit();
        session2.close();
    }

    public void testPessimisticRead()
    {
        LOG.info("None Lock -> Threads start reading but wait others to finish transaction before write. DEADLOCK!!!");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for(int i = 0;i<THREAD_COUNT;i++) {
            executor.submit(new PaymentJob(20l,20l,i,customerId, LockModeType.PESSIMISTIC_READ));
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
