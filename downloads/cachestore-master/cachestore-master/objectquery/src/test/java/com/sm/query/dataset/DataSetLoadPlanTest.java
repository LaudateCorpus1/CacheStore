package com.sm.query.dataset;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/26/16.
 */
public class DataSetLoadPlanTest {
    @Test
    public void testAll() {
        final ArrayList<RemoteSource> ascList = new ArrayList<RemoteSource>();

        ascList.add(new RemoteSource("ncs", 7L));
        ascList.add(new RemoteSource("crm", 12L));

        DataSetLoadPlan andPlan = new DataSetLoadPlan(ascList);

        List<RemoteSource> descList = new ArrayList<RemoteSource>();

        descList.add(new RemoteSource("mkts", 3L));
        descList.add(new RemoteSource("expr", 2L));

        DataSetLoadPlan orPlan = new DataSetLoadPlan(descList);

        final DataSetLoadPlan[] dataSetLoadPlans = {andPlan, orPlan};
        DataSetLoadPlan mergePlan = new DataSetLoadPlan(dataSetLoadPlans, "or");

        final int size = mergePlan.getRemoteSourcesList().size();
        Assert.assertEquals(size, 4);
    }
}