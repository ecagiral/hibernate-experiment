package com.ecagiral.hibernate.graph;

import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Part;
import com.ecagiral.hibernate.base.data.Product;
import com.ecagiral.hibernate.base.data.ProductRef;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class GraphTest {

    public void test_retrieve_lazy(){

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Product topProduct = getProduct();

        session.save(topProduct);

        List<Integer> productList = new ArrayList<>();
        productList.add(topProduct.getId());
        session.getTransaction().commit();
        session.close();

        session = Repo.getInstance().getSession();
        session.beginTransaction();

        long start = System.currentTimeMillis();
        List<Product> products = session
                .createQuery("select p from Product p where p.id in :id",Product.class)
                .setParameter("id",productList)
                .getResultList();
        int edgeCount = products.get(0).getEdgeCount();
        long time = System.currentTimeMillis()-start;
        System.out.println("////////select lazy takes "+time+"ms");
        session.getTransaction().commit();
        session.close();

        assertEquals("Bom size",100, edgeCount);


    }

    private Product getProduct() {
        int comps = 10;
        Product topProduct = new Product("table");
        while(comps>0){
            Product comp = new Product("stick");
            Part leg = new Part();
            leg.setChild(comp);
            leg.setParent(topProduct);
            leg.setTimesUsed(1);
            topProduct.getBom().add(leg);
            int mats = 10;
            while(mats>0){
                Product wood  = new Product("wood");
                Part material = new Part();
                material.setChild(wood);
                material.setParent(comp);
                material.setTimesUsed(1);
                comp.getBom().add(material);
                mats--;
            }
            comps--;
        }
        return topProduct;
    }

    public void test_retrieve_graph(){

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Product topProduct = getProduct();

        session.save(topProduct);

        List<Integer> productList = new ArrayList<>();
        productList.add(topProduct.getId());
        session.getTransaction().commit();
        session.close();

        session = Repo.getInstance().getSession();
        session.beginTransaction();

        long start = System.currentTimeMillis();
        List<ProductRef> products = session
                .createNativeQuery("WITH RECURSIVE all_bom(id,name,timesused,child_id) AS ( " +
                        "    SELECT p.id,p.name,pp.timesused,pp.child_id " +
                        "    from product p " +
                        "    join part pp on pp.parent_id = p.id " +
                        "    where p.id in (:id) " +
                        "  UNION ALL " +
                        "    SELECT  p.id,p.name,pp.timesused,pp.child_id " +
                        "    from all_bom ab " +
                        "    join product p on p.id = ab.child_id " +
                        "    join part pp on pp.parent_id = p.id " +
                        ") " +
                        "select * from all_bom")
                .setResultTransformer(Transformers.aliasToBean(ProductRef.class))
                .setParameter("id",productList)
                .getResultList();

        session.getTransaction().commit();
        session.close();
        Product p = new Product();
        p.setId(productList.get(0));
        p.buildParts(products);
        int edgeCount = p.getEdgeCount();
        long time = System.currentTimeMillis()-start;
        System.out.println("////////select graph takes "+time+"ms");


        assertEquals("Bom size",100, edgeCount);

    }

}
