package com.sm.query.delayedresolution;

import com.sm.query.utils.QueryException;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created on 4/25/16.
 */
final public class DelayedResolvable {
    private final HashSet<String> missingObjectFields = new HashSet<String>();
    private Object resolution;

    private boolean invert = false;

    public DelayedResolvable(Object resolvedValue) {
        resolution = resolvedValue;
    }

    public DelayedResolvable(String missingObjectField) {
        resolution = null;
        missingObjectFields.add(missingObjectField);
    }

    public void invert() {
        invert = !invert;
    }

    public boolean isResolved() {
        return missingObjectFields.isEmpty();
    }

    public void setResolution(String value) {
        this.resolution = value;
    }

    protected Object getValue() {
        return resolution;
    }

    public Collection<String> getMissingRemoteSources() {
        return missingObjectFields;
    }

    public void mergeMissingRemoteSources(DelayedResolvable other) {
        final Collection<String> missingRemoteSources = other.getMissingRemoteSources();
        addMissingRemoteSources(missingRemoteSources);
    }

    private void addMissingRemoteSources(Collection<String> missingRemoteSources) {
        missingObjectFields.addAll(missingRemoteSources);
    }

    /**
     * Should only be called in predicate.
     *
     * @return
     */
    public boolean getPredicateResolution() {
        final Object resolution = getValue();
        if (resolution instanceof Boolean) {
            final Boolean asBool = (Boolean) resolution;
            return invert != asBool;
        } else {
            throw new QueryException("expected Boolean, got:" + resolution.getClass());
        }
    }

    /**
     * Please ensure that this.resolved == true before calling this method
     */
    public boolean resolveEquals(DelayedResolvable right) {
        final Object rightValue = right.getValue();
        final Object value = getValue();
        return value.equals(rightValue);
    }

    public int resolveEqualsOrCount(DelayedResolvable right) {
        final Object value = getValue();
        if (value instanceof Iterable) {
            return resolveCount(right);
        } else {
            return resolveEquals(right) ? 1 : 0;
        }
    }

    public int resolveCount(DelayedResolvable right) {
        final Iterable asIterable = getIterable();
        final Object rightValue = right.getValue();

        int c = 0;
        for (Object e : asIterable) {
            if (e.equals(rightValue)) {
                c++;
            }
        }

        return c;
    }

    /**
     * Please ensure that this.resolved == true before calling this method
     */
    public boolean resolveCompare(String operator, DelayedResolvable expression) {
        final boolean isEqual = resolveEquals(expression);
        if (operator.equals(DelayedResolutionPredicateVisitorImpl.EQUALS)) {
            return isEqual;
        } else if (operator.equals(DelayedResolutionPredicateVisitorImpl.NOT_EQUALS)) {
            return !isEqual;
        } else {
            final Comparable result;
            final Object value = getValue();
            if (value instanceof Comparable) {
                result = (Comparable) value;
            } else {
                throw new QueryException("expected Comparable, got:" + value.getClass());
            }

            final Object other = expression.getValue();

            final long compared;
            if (other instanceof Number) {
                final Number otherNumber = (Number) other;
                final long otherNumberAsLong = otherNumber.longValue();

                final Number number = getNumber();
                final long numberAsLong = number.longValue();

                compared = otherNumberAsLong - numberAsLong;
            } else {
                final Comparable otherComparable = (Comparable) other;
                // Dangerous..
                compared = result.compareTo(otherComparable);
            }

            if (operator.equals(DelayedResolutionPredicateVisitorImpl.LT)) {
                return compared < 0;
            } else if (operator.equals(DelayedResolutionPredicateVisitorImpl.LTE)) {
                return compared <= 0;
            } else if (operator.equals(DelayedResolutionPredicateVisitorImpl.GT)) {
                return compared > 0;
            } else if (operator.equals(DelayedResolutionPredicateVisitorImpl.GTE)) {
                return compared >= 0;
            } else {
                throw new QueryException("unexpected operator:" + operator);
            }
        }
    }

    /**
     * Please ensure that this.resolved == true before calling this method
     */
    public boolean resolveContains(DelayedResolvable right) {
        final Object resolution = getValue();
        if (resolution instanceof Collection) {
            return ((Collection) resolution).contains(right);
        } else {
            throw new QueryException("expected Collection, got:" + resolution.getClass());
        }
    }

    public boolean resolveEqualsOrContains(DelayedResolvable right) {
        final Object value = getValue();

        if (value instanceof Collection) {
            return resolveContains(right);
        } else {
            return resolveEquals(right);
        }
    }

    /**
     * Please ensure that this.resolved == true before calling this method
     */
    public boolean isNull() {
        return getValue() == null;
    }

    public String getString() {
        final Object resolution = getValue();
        if (resolution instanceof String) {
            return (String) resolution;
        } else {
            return String.valueOf(resolution);
        }
    }

    public byte[] getByteArray() {
        final Object resolution = getValue();
        if (resolution instanceof byte[]) {
            return (byte[]) resolution;
        } else {
            throw new QueryException("expected byte[], got:" + resolution.getClass());
        }
    }

    public Number getNumber() {
        final Object resolution = getValue();
        if (resolution instanceof Number) {
            return (Number) resolution;
        } else {
            throw new QueryException("expected Number, got:" + resolution.getClass());
        }
    }

    public int getInteger() {
        final Number number = getNumber();
        return number.intValue();
    }

    public Iterable getIterable() {
        final Object resolution = getValue();
        if (resolution instanceof Iterable) {
            return (Iterable) resolution;
        } else {
            throw new QueryException("expected Iterable, got:" + resolution.getClass());
        }
    }

    public DelayedResolvable applyOperator(String binaryOperator, DelayedResolvable right) {
        final Number applied;
        final Number leftNumber = getNumber();
        final Number rightNumber = right.getNumber();

        if (binaryOperator.equals("+")) {
            if (rightNumber instanceof Double) {
                applied = leftNumber.doubleValue() + rightNumber.doubleValue();
            } else if (rightNumber instanceof Float) {
                applied = leftNumber.floatValue() + rightNumber.floatValue();
            } else if (rightNumber instanceof Integer) {
                applied = leftNumber.intValue() + rightNumber.intValue();
            } else if (rightNumber instanceof Long) {
                applied = leftNumber.longValue() + rightNumber.longValue();
            } else if (rightNumber instanceof Short) {
                applied = leftNumber.shortValue() + rightNumber.shortValue();
            } else {
                applied = leftNumber.byteValue() + rightNumber.byteValue();
            }
        } else if (binaryOperator.equals("-")) {
            if (rightNumber instanceof Double) {
                applied = leftNumber.doubleValue() - rightNumber.doubleValue();
            } else if (rightNumber instanceof Float) {
                applied = leftNumber.floatValue() - rightNumber.floatValue();
            } else if (rightNumber instanceof Integer) {
                applied = leftNumber.intValue() - rightNumber.intValue();
            } else if (rightNumber instanceof Long) {
                applied = leftNumber.longValue() - rightNumber.longValue();
            } else if (rightNumber instanceof Short) {
                applied = leftNumber.shortValue() - rightNumber.shortValue();
            } else {
                applied = leftNumber.byteValue() - rightNumber.byteValue();
            }
        } else if (binaryOperator.equals("*")) {
            if (rightNumber instanceof Double) {
                applied = leftNumber.doubleValue() * rightNumber.doubleValue();
            } else if (rightNumber instanceof Float) {
                applied = leftNumber.floatValue() * rightNumber.floatValue();
            } else if (rightNumber instanceof Integer) {
                applied = leftNumber.intValue() * rightNumber.intValue();
            } else if (rightNumber instanceof Long) {
                applied = leftNumber.longValue() * rightNumber.longValue();
            } else if (rightNumber instanceof Short) {
                applied = leftNumber.shortValue() * rightNumber.shortValue();
            } else {
                applied = leftNumber.byteValue() * rightNumber.byteValue();
            }
        } else if (binaryOperator.equals("/")) {
            if (rightNumber instanceof Double) {
                applied = leftNumber.doubleValue() / rightNumber.doubleValue();
            } else if (rightNumber instanceof Float) {
                applied = leftNumber.floatValue() / rightNumber.floatValue();
            } else if (rightNumber instanceof Integer) {
                applied = leftNumber.intValue() / rightNumber.intValue();
            } else if (rightNumber instanceof Long) {
                applied = leftNumber.longValue() / rightNumber.longValue();
            } else if (rightNumber instanceof Short) {
                applied = leftNumber.shortValue() / rightNumber.shortValue();
            } else {
                applied = leftNumber.byteValue() / rightNumber.byteValue();
            }
        } else if (binaryOperator.equals("%")) {
            if (rightNumber instanceof Double) {
                applied = leftNumber.doubleValue() % rightNumber.doubleValue();
            } else if (rightNumber instanceof Float) {
                applied = leftNumber.floatValue() % rightNumber.floatValue();
            } else if (rightNumber instanceof Integer) {
                applied = leftNumber.intValue() % rightNumber.intValue();
            } else if (rightNumber instanceof Long) {
                applied = leftNumber.longValue() % rightNumber.longValue();
            } else if (rightNumber instanceof Short) {
                applied = leftNumber.shortValue() % rightNumber.shortValue();
            } else {
                applied = leftNumber.byteValue() % rightNumber.byteValue();
            }
        } else {
            throw new QueryException("unexpected binaryOperator:" + binaryOperator);
        }

        return new DelayedResolvable(applied);
    }
}
