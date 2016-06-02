package com.sm.query.delayedresolution;

import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.Column;
import com.sm.query.utils.QueryException;
import com.sm.query.utils.QueryUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * If a field path from the root is a key in remoteSourcesLoaded and that value is false,
 * then the DelayedResolvable returned will be unresolved.
 */
public class ObjectFieldContextVisitor {
    private final Map<String, Column> columnMap;
    private final Map<String, Boolean> remoteSourcesLoaded;

    private Map<Class, Map<String, Field>> classFieldCache = new HashMap<Class, Map<String, Field>>();

    public ObjectFieldContextVisitor(Map<String, Column> columnMap, Map<String, Boolean> remoteSourcesLoaded) {
        this.columnMap = columnMap;
        this.remoteSourcesLoaded = remoteSourcesLoaded;
    }

    DelayedResolvable visitObjectField(PredicateParser.ObjectFieldContext ctx, Object value) {
        final String canonicalObjectPath = ctx.getText();
        final String[] fields = canonicalObjectPath.split(QueryUtils.DOT4R);
        return traverseObjectField(ctx, fields, 0, value);
    }

    DelayedResolvable traverseObjectField(PredicateParser.ObjectFieldContext ctx, String[] objectFields, int currentPathPoint, Object value) {
        final StringBuilder poftBuilder = new StringBuilder();
        for (int i = 0; i < currentPathPoint; i++) {
            final String objectField = objectFields[i];
            poftBuilder.append(objectField);
            poftBuilder.append('.');
        }

        final String priorObjectFieldText = poftBuilder.toString();
        final String objectFieldText = objectFields[currentPathPoint];

        final String canonicalObjectFieldText = priorObjectFieldText + objectFieldText;

        if (columnMap.containsKey(canonicalObjectFieldText)) {
            Column column = columnMap.get(canonicalObjectFieldText);
            final byte[] target = (byte[]) value;

            final int offset = column.getOffset();
            final int endOffset = offset + column.getLen();
            if (target.length < endOffset)
                throw new RuntimeException("source.length " + target.length + " < " + endOffset);

            switch (column.getType()) {
                case INT:
                    int i = QueryUtils.getInt(target, offset);
                    return new DelayedResolvable(i);
                case BYTE:
                    final byte theByte = target[offset];
                    int b = theByte & 0xFF;
                    return new DelayedResolvable(b);
                case SHORT:
                    short s = QueryUtils.getShort(target, offset);
                    return new DelayedResolvable(s);
                case LONG:
                    long l = QueryUtils.getLong(target, offset);
                    return new DelayedResolvable(l);
                case DOUBLE:
                    double d = QueryUtils.getDouble(target, offset);
                    return new DelayedResolvable(d);
                case FLOAT:
                    float f = QueryUtils.getFloat(target, offset);
                    return new DelayedResolvable(f);
                case BOOLEAN:
                    boolean bl = QueryUtils.getBoolean(target, offset);
                    return new DelayedResolvable(bl);
                case STRING:
                    try {
                        String str = new String(target, offset, column.getLen(), "UTF-8");
                        return new DelayedResolvable(str);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                default:
                    throw new RuntimeException("unsupported type " + column.getType());
            }
        } else {
            if (remoteSourcesLoaded.containsKey(canonicalObjectFieldText)) {
                if (!remoteSourcesLoaded.get(canonicalObjectFieldText)) {
                    return new DelayedResolvable(canonicalObjectFieldText);
                }
            }

            final Class klass = value.getClass();
            final Map<String, Field> fieldCache = getFieldsCache(klass);
            if (!fieldCache.containsKey(objectFieldText)) {
                throw new QueryException("could not get field:" + objectFieldText + " from:" + value.getClass() + ", fullPath:" + canonicalObjectFieldText);
            }

            final Field field = fieldCache.get(objectFieldText);

            try {
                final Object nextValue = field.get(value);

                if (nextValue != null && currentPathPoint < objectFields.length - 1) {
                    return traverseObjectField(ctx, objectFields, currentPathPoint + 1, nextValue);
                } else {
                    return new DelayedResolvable(nextValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private Map<String, Field> getFieldsCache(Class klass) {
        if (klass == null || klass.equals(Object.class)) {
            return new HashMap<String, Field>();
        }

        if (!classFieldCache.containsKey(klass)) {
            final Class superclass = klass.getSuperclass();
            final Map<String, Field> fieldCache = getFieldsCache(superclass);

            final Field[] fields = klass.getDeclaredFields();

            for (Field field : fields) {
                final String fieldName = field.getName();
                field.setAccessible(true);
                fieldCache.put(fieldName, field);
            }

            classFieldCache.put(klass, fieldCache);
        }

        return classFieldCache.get(klass);
    }
}