package com.sm.query.delayedresolution;

import com.sm.query.utils.Column;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 4/25/16.
 */
public class DelayedResolutionPredicateVisitorImplTest {
    @Test
    public void testRunPredicate() throws Exception {
        final HashMap<String, Column> columnMap = new HashMap<String, Column>();
        final HashMap<String, Boolean> remoteSourcesLoaded = new HashMap<String, Boolean>();

        remoteSourcesLoaded.put("crm", false);
        remoteSourcesLoaded.put("ncs", false);
        remoteSourcesLoaded.put("mkts", false);

        String queryStr = "crm = false or (ncs = false and mkts = false)";
        DelayedResolutionPredicateRunner dsvf = new DelayedResolutionPredicateRunner(queryStr, columnMap, remoteSourcesLoaded);

        final Tester tester = new Tester();

        long startTime, endTime;
        DelayedResolvable test;

        startTime = System.currentTimeMillis();
        test = dsvf.runPredicate(tester);
        System.out.println(test.getMissingRemoteSources());
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        Assert.assertFalse(test.isResolved());

        remoteSourcesLoaded.put("crm", true);

        startTime = System.currentTimeMillis();
        test = dsvf.runPredicate(tester);
        System.out.println(test.getMissingRemoteSources());
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        Assert.assertFalse(test.isResolved());

        remoteSourcesLoaded.put("ncs", true);

        startTime = System.currentTimeMillis();
        test = dsvf.runPredicate(tester);
        System.out.println(test.getMissingRemoteSources());
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        // don't need to load mkts
        Assert.assertTrue(test.isResolved());
    }

    @Test
    public void testSubfield() throws Exception {
        final HashMap<String, Column> columnMap = new HashMap<String, Column>();
        final HashMap<String, Boolean> remoteSourcesLoaded = new HashMap<String, Boolean>();
        String queryStr = "count(subfield, crm, true) >= 3";
        DelayedResolutionPredicateRunner dsvf = new DelayedResolutionPredicateRunner(queryStr, columnMap, remoteSourcesLoaded);

        final SubFieldTester sft = new SubFieldTester();
        for (int i = 0; i < 3; i++) {
            sft.subfield.add(new Tester());
        }

        DelayedResolvable dr = dsvf.runPredicate(sft);
        Assert.assertTrue(dr.isResolved());
        Assert.assertTrue(dr.getPredicateResolution());
    }

    public static void main(String[] args) {
        long k = Long.MAX_VALUE;
        long m = Long.MIN_VALUE;
        int j = (int) k;
        boolean b = m < j;
        System.out.println(b);
    }

    static class Tester {
        private boolean crm = true;
        private boolean ncs = true;
        private boolean mkts = true;
    }

    static class SubFieldTester {
        List<Tester> subfield = new ArrayList<Tester>();
    }
}