
package com.verisign.iot.discovery.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pmaresca <pmaresca@verisign.com>
 */
public class CompoundLabelTest
{

    private CompoundLabel type;

    public CompoundLabelTest() {}

    @Before
    public void setUp() { type = null; }

    @After
    public void tearDown() {}


    @Test
    public void testHasSubtype()
    {
        type = new CompoundLabel("coap");
        Assert.assertFalse(type.hasSubType());
        type = new CompoundLabel("coap", "subcoap");
        Assert.assertTrue(type.hasSubType());
    }

    @Test
    public void testPrefixString()
    {
        type = new CompoundLabel("coap");
        Assert.assertEquals("_coap._tcp.", type.prefixString());
        type = new CompoundLabel("coap", "subcoap", "udp");
        Assert.assertEquals("_subcoap._sub._coap._udp.", type.prefixString());
        type = new CompoundLabel("coap", "subcoap");
        Assert.assertEquals("_subcoap._sub._coap._tcp.", type.prefixString());
    }

    @Test
    public void testIsCompound()
    {
        Assert.assertFalse(CompoundLabel.isCompound("coap"));
        Assert.assertTrue(CompoundLabel.isCompound("coap:"));
        Assert.assertTrue(CompoundLabel.isCompound("coap:tcp"));
        Assert.assertTrue(CompoundLabel.isCompound("coap:subcoap:udp"));
        try {
            CompoundLabel.isCompound(":");
        } catch(Exception e) { /* expected */ }
    }

    @Test
    public void testlabelComponents()
    {
        String label = "coap";
        String[] test = null;
        try {
            test = CompoundLabel.labelComponents(label);
        } catch(Exception e) { /* expected */ }

        label = "coap:TCP";
        try {
            test = CompoundLabel.labelComponents(label);
            Assert.assertEquals("coap", test[0]);
            Assert.assertEquals("tcp", test[2]);
            Assert.assertEquals("", test[1]);
        } catch(Exception e) { Assert.fail("Expected a parsing of: " +label); }

        label = "coap:subcoap:UDP";
        try {
            test = CompoundLabel.labelComponents(label);
            Assert.assertEquals("coap", test[0]);
            Assert.assertEquals("udp", test[2]);
            Assert.assertEquals("subcoap", test[1]);
        } catch(Exception e) { Assert.fail("Expected a parsing of: " +label); }

        label = ":";
        try {
            test = CompoundLabel.labelComponents(label);
        } catch(Exception e) { /* expected */ }

        label = "a:";
        try {
            test = CompoundLabel.labelComponents(label);
            Assert.assertEquals("a", test[0]);
            Assert.assertEquals("", test[2]);
            Assert.assertEquals("", test[1]);
        } catch(Exception e) { Assert.fail("Expected a parsing of: " +label);  }

        label = ":a";
        try {
            test = CompoundLabel.labelComponents(label);
        } catch(Exception e) { /* expected */ }
    }

}
