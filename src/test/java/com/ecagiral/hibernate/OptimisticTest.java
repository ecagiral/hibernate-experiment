package com.ecagiral.hibernate;

import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Customer;
import com.ecagiral.hibernate.base.service.PaymentJob;
import com.ecagiral.hibernate.base.service.ReportJob;
import junit.framework.TestCase;
import org.hibernate.Session;

import javax.persistence.LockModeType;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptimisticTest extends TestCase {

    private Repo repo;

    private Logger LOG = Logger.getLogger(OptimisticTest.class.getName());
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
     * Start 3 different sessions
     * First session reads data then second session updates data and finally third session read data again
     * First session reads old data third session reads new data.
     * No OptimisticLockException since first and third sessions just read operations
     */
    public void testOptimisticWriteWhileRead()
    {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new PaymentJob(20l,0l,1,customerId, LockModeType.OPTIMISTIC));
        Future<Integer> before = executor.submit(new ReportJob(2,10l,0l,customerId, LockModeType.OPTIMISTIC));
        Future<Integer> after = executor.submit(new ReportJob(3,60l,0l,customerId, LockModeType.OPTIMISTIC));

        executor.shutdown();
        try {
            boolean term = executor.awaitTermination(20000, TimeUnit.MILLISECONDS);
            if(!term){
                LOG.info("TIMEOUT!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Integer beforeResult = -1;
        Integer afterResult = -1;
        try {
            beforeResult = before.get();
            afterResult = after.get();
        }catch (Exception e){

        }
        assertEquals(0, beforeResult.intValue());
        assertEquals(1, afterResult.intValue());
    }

    /**
     * Start 2 different sessions
     * First session updates data then second session updates data and then first session flush and finally second session flush
     * OptimisticLockException fired because there is difference on version for second sessions read and write operation
     */
    public void testOptimisticWriteWhileWrite()
    {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Future<Integer> before = executor.submit(new PaymentJob(10l,10l,1,customerId, LockModeType.OPTIMISTIC));
        Future<Integer> after = executor.submit(new PaymentJob(10l,20l,1,customerId, LockModeType.OPTIMISTIC));

        executor.shutdown();
        try {
            boolean term = executor.awaitTermination(20000, TimeUnit.MILLISECONDS);
            if(!term){
                LOG.info("TIMEOUT!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Integer beforeResult = -1;
        Integer afterResult = -1;
        try {
            beforeResult = before.get();
            afterResult = after.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        assertEquals(1, beforeResult.intValue());
        assertEquals(1, afterResult.intValue());
    }
}
