package com.ecagiral.hibernate;

import com.ecagiral.hibernate.base.service.Startup;
import junit.framework.TestCase;

import java.io.IOException;

public class StartupTest extends TestCase {

    public void testInitDbFromFile(){
        try {
            Startup.initDbFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
