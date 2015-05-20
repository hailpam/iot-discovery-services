
package com.verisign.iot.discovery.utils;

import com.verisign.iot.discovery.domain.Fqdn;
import junit.framework.Assert;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author pmaresca
 */
public class ValidatorUtilTest {
    
    public ValidatorUtilTest() {
    }
    
    @Before
    public void setUp() 
    {
    }
    
    @After
    public void tearDown() 
    {
    }
    
    @Test
    public void validFqdn() 
    {
        Fqdn validName = new Fqdn("", "iot.end-points.verisigninc.com");
        validName = new Fqdn("", "Service\\032Test.iot.end-points.verisigninc.com");
        validName = new Fqdn("", "Service\\ Test.iot.end-points.verisigninc.com");
        validName = new Fqdn("", "service.blahhh");
        validName = new Fqdn("", "123\\ service.iot.end-points.verisigninc.com");
        validName = new Fqdn("", "123\\ service.iot.end-points.verisigninc.com");
    }
    
    @Test
    public void unvalidFqdn() 
    {
        Fqdn invalidName = null;
        try {
            invalidName = new Fqdn("", "yup'ok.iot.end-points.verisigninc.com");
            fail("Expected a FQDN validation failure " +invalidName);
            invalidName = new Fqdn("", "yup\\ok.i$ot.end-points.verisigninc.com");
            fail("Expected a FQDN validation failure " +invalidName);
            invalidName = new Fqdn("", "yup-ok.i&ot.end-points.verisigninc.com");
            fail("Expected a FQDN validation failure " +invalidName);
        }catch(Exception e) {
            Assert.assertTrue(true);
        }
    }
    
    @Test
    public void dnsSdFqdn() 
    {
        Fqdn invalidName = null;
        try {
            invalidName = new Fqdn("", "Service\\032Test.iot.end-points.verisigninc.com");
            String query = invalidName.fqdnWithPrefix("_bip");
            Assert.assertTrue(query.contains("\""));
        }catch(Exception e) {
            fail("Expected a FQDN validatio failure " +invalidName);
        }
    }
    
}
