package com.sm.query.dataset;

import java.util.*;

public class DataSetLoadPlan {
    private static final Merger DESCENDER = new DescendingMerger();
    private static final Merger ASCENDER = new AscendingMerger();

    private final Collection<RemoteSource> remoteSourcesList;

    DataSetLoadPlan() {
        this(new ArrayList<RemoteSource>());
    }

    DataSetLoadPlan(Collection<RemoteSource> remoteSourceList) {
        this.remoteSourcesList = remoteSourceList;
    }

    DataSetLoadPlan(DataSetLoadPlan[] dataSetLoadPlan, String binaryOperator) {
        if (binaryOperator.equals("or")) {
            remoteSourcesList = DESCENDER.merge(dataSetLoadPlan);
        } else {
            remoteSourcesList = ASCENDER.merge(dataSetLoadPlan);
        }
    }

    public Collection<RemoteSource> getRemoteSourcesList() {
        return remoteSourcesList;
    }

    private static abstract class Merger {
        Collection<RemoteSource> merge(DataSetLoadPlan[] dataSetLoadPlans) {
            final Set<RemoteSource> mergedSet = new LinkedHashSet<RemoteSource>();

            final int planCount = dataSetLoadPlans.length;

            final Iterator<RemoteSource>[] iterators = new Iterator[planCount];
            for (int i = 0; i < planCount; i++) {
                final DataSetLoadPlan dataSetLoadPlan = dataSetLoadPlans[i];
                iterators[i] = dataSetLoadPlan.remoteSourcesList.iterator();
            }

            // N^2 noted
            final RemoteSource[] currentSources = new RemoteSource[planCount];
            boolean allEmpty = planCount == 0;
            while (!allEmpty) {
                for (int i = 0; i < planCount; i++) {
                    if (currentSources[i] == null) {

                    }
                }

                long bound = initializeBound();
                int boundaryIndex = 0;

                RemoteSource boundaryRemoteSource = null;
                for (int i = 0; i < planCount; i++) {
                    final RemoteSource currentChoosableSource = currentSources[i];

                    final RemoteSource chooseableSource;
                    if (currentChoosableSource == null) {
                        final Iterator<RemoteSource> iterator = iterators[i];
                        final boolean moreRemoteSources = iterator.hasNext();
                        if (moreRemoteSources) {
                            final RemoteSource next = iterator.next();
                            currentSources[i] = next;
                            chooseableSource = next;
                        } else {
                            chooseableSource = null;
                        }
                    } else {
                        chooseableSource = currentChoosableSource;
                    }

                    if (chooseableSource != null) {
                        final Long currentSize = chooseableSource.size;
                        if (compareCurrentToBound(currentSize, bound)) {
                            boundaryRemoteSource = chooseableSource;
                            bound = currentSize;
                            boundaryIndex = i;
                        }
                    }
                    // else skip
                }

                if (boundaryRemoteSource == null) {
                    // planCount == 0
                } else {
                    mergedSet.add(boundaryRemoteSource);
                }
                currentSources[boundaryIndex] = null;

                allEmpty = true;
                for (int i = 0; i < planCount && allEmpty; i++) {
                    final Iterator iterator = iterators[i];
                    final boolean moreRemoteSources = iterator.hasNext();
                    final boolean noCurrentSource = currentSources[i] == null;
                    allEmpty = !moreRemoteSources && noCurrentSource;
                }
            }

            return mergedSet;
        }

        protected abstract long initializeBound();

        protected abstract boolean compareCurrentToBound(Long currentSize, long bound);
    }

    static final class DescendingMerger extends Merger {
        @Override
        protected long initializeBound() {
            return 0;
        }

        @Override
        protected boolean compareCurrentToBound(Long currentSize, long bound) {
            return currentSize > bound;
        }
    }

    private static class AscendingMerger extends Merger {
        @Override
        protected long initializeBound() {
            return Long.MAX_VALUE;
        }

        @Override
        protected boolean compareCurrentToBound(Long currentSize, long bound) {
            return currentSize < bound;
        }
    }

    static final class Comparator implements java.util.Comparator<RemoteSource> {
        @Override
        public int compare(RemoteSource o1, RemoteSource o2) {
            // size should always be > 0
            final long diff = o1.size - o2.size;
            final int compared;
            if (diff < Integer.MIN_VALUE) {
                compared = Integer.MIN_VALUE;
            } else if (diff > Integer.MAX_VALUE) {
                compared = Integer.MAX_VALUE;
            } else {
                compared = (int) diff;
            }

            return compared;
        }
    }
}
