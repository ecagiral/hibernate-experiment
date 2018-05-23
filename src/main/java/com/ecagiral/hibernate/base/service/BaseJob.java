package com.ecagiral.hibernate.base.service;


import com.ecagiral.hibernate.base.Repo;
import org.hibernate.Session;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public abstract class BaseJob implements Callable<Integer>{

    private Long preDelay;
    private Long postDelay;
    protected Session session;
    private Integer jobId;

    protected Logger LOG = Logger.getLogger(this.getClass().getName());

    public BaseJob(Integer id,Long pre, Long post){
        this.jobId = id;
        this.preDelay = pre;
        this.postDelay = post;
    }

    public Integer call() {
        Integer result = null;
        try {
            session = Repo.getInstance().getSession();
            session.beginTransaction();
            LOG.info(this.getClass().getName()+" "+this.jobId+" begin transaction");
            Thread.sleep(preDelay);
            result = execute();
            LOG.info(this.getClass().getName()+" "+this.jobId+" executed");
            Thread.sleep(postDelay);
            session.getTransaction().commit();
            LOG.info(this.getClass().getName()+" "+this.jobId+" commit transaction");
            session.close();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public abstract Integer execute() throws Exception;

}
