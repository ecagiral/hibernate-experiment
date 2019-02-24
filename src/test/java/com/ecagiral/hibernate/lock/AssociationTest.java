package com.ecagiral.hibernate.lock;

import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Customer;
import com.ecagiral.hibernate.base.data.Sale;
import junit.framework.TestCase;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by garajyeri on 29.05.2018.
 */
public class AssociationTest extends TestCase {

    private Logger LOG = Logger.getLogger(AssociationTest.class.getName());
    private Integer saleId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Repo repo = Repo.getInstance();
        Session session = repo.getSession();
        session.beginTransaction();
        Customer customer = new Customer(UUID.randomUUID().toString());
        session.save(customer);
        Sale sale = new Sale(customer, BigDecimal.TEN);
        session.save(sale);
        saleId = sale.getId();
        session.getTransaction().commit();
        session.close();
    }
    public void testSimple(){
        Repo repo = Repo.getInstance();
        Session session = repo.getSession();
        session.beginTransaction();
        LOG.info("Retrieve Sale");
        Sale sale = session.find(Sale.class,saleId);
        LOG.info("Sale retrieved. "+sale.getId());
        String buyerName = sale.getBuyer().getName();
        LOG.info("Buyer Name: "+buyerName);
        session.getTransaction().commit();
        session.close();
        assertTrue(true);
    }
}
