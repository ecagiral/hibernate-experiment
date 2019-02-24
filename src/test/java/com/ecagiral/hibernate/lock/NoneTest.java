package com.ecagiral.hibernate.lock;

import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Customer;
import com.ecagiral.hibernate.base.service.PaymentJob;
import junit.framework.TestCase;
import org.hibernate.Session;

import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NoneTest extends TestCase {

    private Repo repo;

    private Logger LOG = Logger.getLogger(NoneTest.class.getName());
    private final int threadCount = 3;
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

    /**
     * Multiple sessions updates data at the same time without knowing each other
     * So result is same for all
     */
    public void testNone(){
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futureList = new ArrayList<Future<Integer>>();
        for(int i = 0;i<threadCount;i++) {
            futureList.add(executor.submit(new PaymentJob(20l,20l,i,customerId, LockModeType.NONE)));
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

        for(Future<Integer> aFuture:futureList){
            try {
                assertEquals(1,aFuture.get().intValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
