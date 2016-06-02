package com.sm.query.utils;


import com.sm.query.Result;
import com.sm.query.Result.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.utils.Pair;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.sm.query.utils.QueryUtils.Comparator.Equal;
import static com.sm.query.utils.QueryUtils.Comparator.NotEqual;
import static com.sm.query.utils.QueryUtils.Comparator.getComparator;

/**
 * Created by mhsieh on 12/22/14.
 */
public class QueryUtils {
    private static Log logger = LogFactory.getLog(QueryUtils.class);
    public final static String[] CLASS_PREFIX_ARY = { "com.sm.", "com.specificmedia.","test" };
    public static final String DOT4R = "\\." ;    //for regular express of split



    public static  Pair<Object, String> findSource(String text, Object[] source, List<String> classNameList ) {
        String[] ary = text.split(DOT4R);
        //more than 1 source object
        if ( source.length > 1 ) {
            if ( ary.length > 1) {
                Object obj = findInList(ary[0], source, classNameList);
                //if (obj == null) throw new QueryException("can not find source object for " + ary[0]);
                return new Pair<Object, String>(obj, getRestOf(ary));
            }
            else
                throw new QueryException("sources has "+source.length+" but without alias prefix");
        }
        else {
            if (ary.length > 1) {
                if (ary[0].equals( classNameList.get(0)))
                    return new Pair<Object, String>(source[0], getRestOf(ary));
                else
                    return new Pair<Object, String>(source[0], text);
            }
            else {
                return new Pair<Object, String>(source[0], text);
            }
        }
    }

    public static String getRestOf(String[] ary) {
        StringBuilder sb = new StringBuilder();
        for (int i =1; i < ary.length ; i++) {
            if ( i > 1 && i < ary.length )
                sb.append("."+ary[i]);
            else
                sb.append(ary[i]);
        }
        return sb.toString();
    }

    public static  Object findInList(String className, Object[] source,  List<String> classNameList ) {
        for ( int i = 0; i < classNameList.size() ; i++) {
            if (classNameList.get(i) == null )
                return null;
            if ( classNameList.get(i).equals( className) )
                return source[i];
        }
        return null;
    }

    public static  Pair<Object,FieldInfo> findObjectId(String objectId, Object source, Map<String, ClassInfo> metaData){
        if (source == null ) {
            throw new ObjectIdException("source is null");
        }
        ClassInfo classInfo = findClassInfo( source, metaData);
        //
        String[] fields = objectId.split(DOT4R);
        if (fields.length == 1 ) {
            return findField( objectId, source, classInfo);
        }
        else {
            Pair<Object, FieldInfo> pair = findObjectId( fields[0], source, metaData);
            try {
                Object obj = pair.getSecond().getField().get( pair.getFirst());
                if ( obj != null ) {
                    String id = extract(fields);
                    return findObjectId( id, obj, metaData);
                }
                else {
                    return pair;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }


    public static String extract(String[] fields) {
        if (fields.length < 1 ) throw new RuntimeException("field length "+fields.length+ " less than 1 ");
        StringBuilder sb = new StringBuilder( );
        for ( int i = 1; i < fields.length ; i++ ) {
            if ( i == 1)
                sb.append( fields[i]);
            else
                sb.append(".").append( fields[i]);
        }
        return sb.toString();
    }

    public static Pair<Object,FieldInfo> findField(String fieldName, Object source, ClassInfo classInfo) {
        if (source.getClass().isPrimitive() || classInfo == null ) {
            throw new RuntimeException(source.getClass().getName() + " is primitive or classInfo is null");
        }
        for (FieldInfo each : classInfo.getFieldInfos() ) {
            if ( each.getField().getName().equals( fieldName)) {
                return new Pair(source, each);
            }
        }
        throw  new ObjectIdException("no such field "+fieldName);
    }

    public static Object createInstance(Class<?> cls) throws Exception {
        Constructor<?> constructor = null;
        Object[] constructorArgs = null;
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        //take the first constructor
        if (constructors.length > 0 ) {
            constructor= constructors[0];
            constructor.setAccessible(true);
            Class<?> []params = constructor.getParameterTypes();
            constructorArgs = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                constructorArgs[i] = getParamArg(params[i]);
            }
            return constructor.newInstance(constructorArgs);
        }
        else
            return cls.newInstance();
    }


    public static Object getParamArg(Class<?> cl){
        if (! cl.isPrimitive())
            return null;
        else if (boolean.class.equals(cl))
            return Boolean.FALSE;
        else if (byte.class.equals(cl))
            return new Byte((byte) 0);
        else if (short.class.equals(cl))
            return new Short((short) 0);
        else if (char.class.equals(cl))
            return new Character((char) 0);
        else if (int.class.equals(cl))
            return Integer.valueOf(0);
        else if (long.class.equals(cl))
            return Long.valueOf(0);
        else if (float.class.equals(cl))
            return Float.valueOf(0);
        else if (double.class.equals(cl))
            return Double.valueOf(0);
        else
            throw new UnsupportedOperationException();
    }

    public static enum Comparator {
        Equal, Greater, Less, NotEqual, GreaterEq, LessEq, In, Range;
        public static Comparator getComparator(String value) {
            if ( value.equals("=")) return Equal;
            else if ( value.equals(">")) return Greater;
            else if ( value.equals("<")) return Less;
            else if ( value.equals("!=")) return NotEqual;
            else if ( value.equals(">=")) return GreaterEq;
            else if ( value.equals("<=")) return LessEq;
            else if ( value.equals("in")) return In;
            else if ( value.endsWith("[]")) return Range;
            else throw new QueryException("invalidate operator "+value);
        }
    }

    public static enum BinaryOp {
        Plus, Minus, Multiply, Divide, Mod;
        public static BinaryOp getBinaryOp(String value) {
            if ( value.equals("+")) return Plus;
            else if ( value.equals("-")) return Minus;
            else if ( value.equals("*")) return Multiply;
            else if ( value.equals("/")) return Divide;
            else if ( value.equals("%")) return Mod;
            else throw new QueryException("invalidate binary op "+value);
        }
    }

    public static boolean isInList(Result result, List<Result> list) {
        for ( Result each : list) {
            if (compare( result, "=", each))
                return true;
        }
        return false;
    }

    public static boolean compareResults(Result left, Result right){
        if ( left.getType() == Type.STRING)
            return ( (String) left.getValue()).equals(((String) right.getValue()));

        else {
            if ( determineType( left, right) == Type.LONG)
                return convertLong(left) == convertLong( right) ;
            else
                return convertDouble( left) == convertDouble( right);
        }
    }

    public static Result binaryOp(Result left, String binaryOperator, Result right) {
        Type type = determineType( left, right);
        if ( type == Type.LONG)
            return new Result (binaryOpLong( left, binaryOperator, right));
        else
            return new Result( binaryOpDouble( left, binaryOperator, right));
    }

    public static Double binaryOpDouble(Result left, String binaryOperator, Result right){
        BinaryOp binaryOp = BinaryOp.getBinaryOp( binaryOperator);
        double lf = convertDouble( left);
        double rt = convertDouble( right);
        switch (binaryOp) {
            case Plus: return lf + rt;
            case Minus: return lf - rt;
            case Multiply: return lf * rt ;
            case Divide: return lf / rt;
            case Mod: return  lf % rt;
            default: throw new QueryException("invalidate binary op "+binaryOperator);
        }
    }

    public static long binaryOpLong(Result left, String binaryOperator, Result right){
        BinaryOp binaryOp = BinaryOp.getBinaryOp( binaryOperator);

        long lf = convertLong( left);
        long rt = convertLong( right);
        switch (binaryOp) {
            case Plus: return lf + rt;
            case Minus: return lf - rt;
            case Multiply: return lf * rt;
            case Divide: return lf /rt ;
            case Mod : return lf % rt ;
            default: throw new QueryException("invalidate binary op "+binaryOperator);
        }
    }

    public static boolean compare(Result left, String operator, Result right) {
        Comparator comparator = getComparator(operator);
        if (left.getType() == Type.STRING ) {
            if ( left.getValue() != null && right.getValue() != null) {
                String leftValue = (String) left.getValue();
                String rightValue = (String) right.getValue();
                switch (comparator) {
                    case Equal:
                        return leftValue.equals(rightValue);
                    case NotEqual:
                        return !leftValue.equals(rightValue);
                    case Greater:
                        return (leftValue.compareTo(rightValue) > 0);
                    case GreaterEq:
                        return (leftValue.compareTo(rightValue) >= 0);
                    case Less:
                        return (leftValue.compareTo(rightValue) < 0);
                    case LessEq:
                        return (leftValue.compareTo(rightValue) <= 0);
                    default:
                        throw new QueryException("invalid " + comparator.toString() + " for String");
                }
            }
            else if (left.getValue() == null && right.getValue() == null ) {
                if (comparator == Equal)
                    return true;
                else
                    return false;
            }
            else {
                if ( comparator == NotEqual)
                    return true;
                else
                    return false;
            }
        }
        else if ( left.getType() == Type.NULL || right.getType() == Type.NULL) {
            switch ( comparator){
                case Equal: return ( left.getValue() == null && right.getValue() == null );
                case NotEqual: return ! ( left.getValue() == null && right.getValue() == null );
                // return false,instead of exception
                default: return false ;
            }
        }
        else if ( left.getType() == Type.BOOLEAN || left.getType() == Type.BOOLEANS) {
            switch ( comparator){
                case Equal: return ((Boolean) left.getValue()).equals( ((Boolean) right.getValue()) );
                case NotEqual: return ! ((Boolean) left.getValue()).equals( ((Boolean) right.getValue()) );
                default: throw new QueryException("invalid "+comparator.toString()+" for boolean") ;
            }
        }
        else if ( left.getType() == Type.ARRAY || right.getType() == Type.ARRAY) {
            switch (comparator) {
                case Equal:
                    return Arrays.equals( (byte[]) left.getValue(), (byte[]) right.getValue());
                case NotEqual:
                    return !Arrays.equals( (byte[]) left.getValue(), (byte[]) right.getValue());
                default:
                    throw new QueryException("invalid " + comparator.toString() + " for byte arrays");
            }
        }
        else {
            if (  isObjectType(left.getType() ))
                throw new QueryException("comparator not for "+ left.getType().toString());
            else { //this is no number type
                if (determineType( left, right) == Type.LONG) {
                    long lf = convertLong( left);
                    long rt = convertLong( right);
                    long diff = lf -rt;
                    return deterMineLong( diff, comparator);
                }
                else {
                    double lf = convertDouble( left);
                    double rt = convertDouble( right);
                    double diff = lf - rt;
                    return deterMineDouble( diff, comparator);
                }

            }

        }
    }


    public static boolean deterMineLong(long diff, Comparator comparator) {
        if ( diff == 0) {
            switch ( comparator) {
                case Equal:
                case GreaterEq:
                case LessEq:
                    return true;
                default:
                    return false;
            }
        }
        else if ( diff < 0 ) {
            switch ( comparator) {
                case Less:
                case LessEq:
                case NotEqual:
                    return true;
                default:
                    return false;
            }
        }
        else {
            switch (comparator) {
                case Greater:
                case GreaterEq:
                case NotEqual:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static boolean deterMineDouble(double diff, Comparator comparator) {
        if ( diff == 0) {
            switch ( comparator) {
                case Equal:
                case GreaterEq:
                case LessEq:
                    return true;
                default:
                    return false;
            }
        }
        else if ( diff < 0 ) {
            switch ( comparator) {
                case Less:
                case LessEq:
                    return true;
                default:
                    return false;
            }
        }
        else {
            switch (comparator) {
                case Greater:
                case GreaterEq:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static Object convertType(Type type, String value){
        switch (type) {
            case STRING:
                return value;
            case INTS:
            case INT:
                return Integer.valueOf(value);
            case SHORT:
            case SHORTS:
                return Short.valueOf(value);
            case LONG:
            case LONGS:
                return Long.valueOf(value);
            case NUMBER:
                return Long.valueOf(value);
            default:
                throw new ObjectIdException("wrong type to convert long " + type);
        }
    }

    public static long convertLong(Result result){
        Type type = result.getType();
        switch (type) {
            case INTS:
            case  INT:
                return (long) (Integer) result.getValue();
            case SHORT:
            case SHORTS:
                return (long) (Short) result.getValue();
            case LONG:
            case LONGS:
                return (Long) result.getValue();
            case NUMBER:
                return new Long( (String) result.getValue());
            default:
                throw new ObjectIdException("wrong type to convert long "+type);
        }

    }


    public static double convertDouble(Result result) {
        Type type = result.getType();
        switch (type) {
            case FLOAT:
            case FLOATS:
                return (double) (Float) result.getValue();
            case DOUBLE:
            case DOUBLES:
                return (Double) result.getValue();
            case NUMBER:
                return new Double( (String) result.getValue() );
            default:
                throw new ObjectIdException("wrong type to convert long "+type);
        }
    }

    public static Type determineType(Result left, Result right){
        Type type = left.getType();
        switch (type) {
            case DOUBLE:
            case DOUBLES:
            case FLOAT:
            case FLOATS:
                return Type.DOUBLE;
            case NUMBER:
                if ( right.getType() == Type.NUMBER) {
                    if (((String) left.getValue()).indexOf(".") < 0 || ((String) right.getValue()).indexOf(".") < 0)
                        return Type.LONG;
                    else
                        return Type.DOUBLE;
                }
                else
                    break;
            default:
                return Type.LONG;
        }
        type = right.getType();
        switch (type) {
            case DOUBLE:
            case DOUBLES:
            case FLOAT:
            case FLOATS:
                return Type.DOUBLE;
            case NUMBER:
                if (((String) right.getValue()).indexOf(".") < 0 )
                    return Type.LONG;
                else
                    return Type.DOUBLE;

            default:
                return Type.LONG;
        }

    }

    public static boolean isObjectType(Type type) {
        if ( type == Type.ARRAY || type == Type.HASHSET || type == Type.OBJECT || type == Type.JOBJECT
                || type == Type.MAP || type == Type.LIST )
            return true;
        else
            return false;
    }
    /**
     * @param object
     * @param metaData  - hashmap of ClassInfo
     * @return ClassInfo which represent
     * use getSimpleName() instead of getName()
     */
    public static ClassInfo findClassInfo(Object object, Map<String, ClassInfo> metaData) {
        String className  = object.getClass().getName();
        //getSimpleName without package name space
        String keyName = object.getClass().getSimpleName();
        ClassInfo info = metaData.get(keyName);
        if ( info == null) {
            Type type = getType(className);
            Object obj;
            Collection values;
            switch (type) {
                case ARRAY :
                    obj = Array.get(object, 0);
                    //info = findClassInfo(object, cacheMetaData);
                    assert( obj != null );
                    info = findClassInfo(obj, metaData);
                    break;
                case MAP :
                    values = ((Map) object).values();
                    obj = values.iterator().next();
                    assert( obj != null );
                    info = findClassInfo(obj, metaData);
                    break;
                case HASHSET :
                case LIST :
                    Iterator v = ((Collection) object).iterator();
                    obj = v.next();
                    info = findClassInfo(obj, metaData);
                    break;
                case OBJECT :
                    List<FieldInfo> list = new ArrayList<FieldInfo>();
                    Class<?> cls = object.getClass();
                    while ( cls != null && cls != Object.class ) {
                        Field[] fields = cls.getDeclaredFields() ;
                        for ( Field field : fields) {
                            if ( ! isSkipField(field)) {
                                field.setAccessible( true);
                                String fieldClsName = field.getType().getName() ;
                                Type fieldType = getType(fieldClsName);
                                list.add( new FieldInfo( field, fieldType, fieldClsName));
                            }
                        }
                        cls = cls.getSuperclass();
                    }
                    if ( list.size() > 0 ) {
                        FieldInfo[] fieldArray = new FieldInfo[ list.size()];
                        fieldArray = list.toArray( fieldArray);
                        info = new ClassInfo( className, type, fieldArray);
                    }
                    break;
                default:
                    logger.error("Wrong type ="+ type+" in createClassInfo object className "+className+" simple name "+keyName);
            } // switch

            metaData.put(keyName, info);
        }
        return info;

    }

    public final static Map<String, Type> typeMap = new HashMap<String, Type>();
    static {
        typeMap.put("short", Type.SHORT);
        typeMap.put("S", Type.SHORT);
        typeMap.put("java.lang.Short", Type.SHORTS);
        typeMap.put("int", Type.INT);
        typeMap.put("I", Type.INT);
        typeMap.put("java.lang.Integer", Type.INTS);
        typeMap.put("long", Type.LONG);
        typeMap.put("L", Type.LONG);
        typeMap.put("java.lang.Long", Type.LONGS);
        typeMap.put("float", Type.FLOAT);
        typeMap.put("F", Type.FLOAT);
        typeMap.put("java.lang.Float", Type.FLOATS);
        typeMap.put("double", Type.DOUBLE);
        typeMap.put("D", Type.DOUBLE);
        typeMap.put("java.lang.Double", Type.DOUBLES);
        typeMap.put("char", Type.CHAR);
        typeMap.put("C", Type.CHAR);
        typeMap.put("java.lang.Character", Type.CHARS);
        typeMap.put("byte", Type.BYTE);
        typeMap.put("B", Type.BYTE);
        typeMap.put("java.lang.Byte", Type.BYTES);
        typeMap.put("boolean", Type.BOOLEAN);
        typeMap.put("Z", Type.BOOLEAN);
        typeMap.put("java.lang.Boolean", Type.BOOLEANS);
        typeMap.put("java.lang.String", Type.STRING);
        typeMap.put("java.lang.Object", Type.OBJECT);
        typeMap.put("java.util.ArrayList", Type.LIST);
        typeMap.put("java.util.List", Type.LIST);
        typeMap.put("java.util.concurrent.CopyOnWriteArrayList", Type.LIST);
        typeMap.put("java.util.HashMap", Type.MAP);
        typeMap.put("java.util.Map", Type.MAP);
        typeMap.put("java.util.concurrent.ConcurrentHashMap", Type.MAP);
        typeMap.put("java.util.HashSet", Type.HASHSET);
        typeMap.put("java.util.Set", Type.HASHSET);
    }

    public static Map<String, Type> getTypeMap() {
        return typeMap;
    }

    public final static char ARRAY_CHAR = '[';

    public static Type getType(String typeName) {
        Type type = Type.OBJECT;
        if ( typeName.charAt(0) ==  ARRAY_CHAR ) {
            // do not check class_prefix
            //if ( typeName.indexOf(CacheEnum.CLASS_PREFIX) >= 0)
            type = Type.ARRAY;
        }
        else {
            // this look up map for predefine Type
            type = getTypeMap().get(typeName);
            if ( type == null) {
                // check if it is class from us to be explored
                // skip third party and anything else
                // add support for special case for Object[]
                if ( isCustomObject(typeName) )
                    type = Type.OBJECT ;
                else
                    type = Type.SKIP ;
            }
        }
        return type;
    }

    public static boolean isPrimitive(Type type) {
        if ( type == Type.OBJECT || type == Type.SKIP || type == Type.JOBJECT ||
                type == Type.MAP || type == Type.LIST || type == Type.ARRAY || type == Type.HASHSET || type == Type.STRING )
            return false;
        else
            return true;

    }


    /**
     * @param field
     * @return true - for write able field
     */
    public static boolean isSkipField(Field field) {
        int modifier = field.getModifiers();
        if (Modifier.isFinal(modifier) || Modifier.isStatic(modifier) || Modifier.isNative(modifier) ||
                Modifier.isTransient(modifier))
            return true;
        else
            return false;
    }

    public static boolean isCustomObject(String typename) {
        // next scan prefix_array
//        for ( String each : CLASS_PREFIX_ARY )  {
//            if ( typename.indexOf( each ) >= 0)
//                return true;
//        }
        //always return true
        return true;
    }

    public static boolean isSameType(Type left, Type right ) {
        if ( left == right )
            return true;
        else if ( left.toString().contains( right.toString()) || right.toString().contains( left.toString()))
            return true;
        else
            return false;
    }

    public static Object convert(Type type, Result result) {
        if ( isSameType( type, result.getType() ) ) return result.getValue();
        switch (type ) {
            case INT :
            case INTS:
                return Integer.valueOf( (String) result.getValue() );
            case SHORT:
            case SHORTS:
                return Short.valueOf( (String) result.getValue() );
            case LONG:
            case LONGS:
                return Long.valueOf( (String) result.getValue());
            case FLOAT:
            case FLOATS:
                return Float.valueOf( (String) result.getValue() );
            case DOUBLE:
            case DOUBLES:
                return Double.valueOf( (String) result.getValue() );
            case BOOLEAN:
            case BOOLEANS:
                return Boolean.valueOf( (String) result.getValue() );
            default:
                throw new QueryException( type.toString()+" is not supported yet");
        }
    }

    public static Result inferValue(Result left, String text) {
        switch (left.getType()) {
            case BOOLEANS:
                return new Result( Boolean.valueOf(text));
            case SHORT:
            case SHORTS:
            case INT:
            case INTS:
            case LONG:
            case LONGS:
                return new Result( Long.valueOf(text));
            case FLOAT:
            case FLOATS:
            case DOUBLE:
            case DOUBLES:
                return new Result( Double.valueOf( text));
            case STRING:
                String str = text.substring(1, text.length() - 1);
                return new Result(str);
            default:
                throw new QueryException("wrong type "+left.getType());
        }
    }

    public static Collection collectObjectField(String fieldId, Collection collection,  Map<String, FieldInfo> idMap,
                                          Map<String, ClassInfo> classInfoMap )  {
        //first is object, second is field
        Collection list = new ArrayList();
        Iterator iterator = collection.iterator();
        while ( iterator.hasNext()) {
            Pair<Object, FieldInfo> pair = findObjectId(fieldId, iterator.next(), classInfoMap);
            idMap.put(fieldId, pair.getSecond());
            try {
                list.add(pair.getSecond().getField().get(pair.getFirst()));
            } catch (IllegalAccessException e) {
                throw new ObjectIdException(e.getMessage(), e);
            }
        }
        return list;
    }

    public static boolean existInIt(Collection collection, Result right) {
        boolean toReturn = false;
        Iterator iterator = collection.iterator();
        while ( iterator.hasNext()) {
            Result lt = null ;
            Object obj = iterator.next();
            if ( obj instanceof Collection ) {
                //check for HashSet
                if ( obj instanceof Set) {
                    if (((Set) obj).contains( right.getValue() ))
                        return true;
                    else
                        return false;
                }
                else { //go through loop
                    Iterator inside = ((Collection) obj).iterator();
                    while (inside.hasNext()) {
                        lt = new Result(inside.next());
                        if (QueryUtils.compare(lt, "=", right))
                            return true;
                    }
                    //none above match
                    return false;
                } //else HashSet
            }
            else  //else non collection, primitive attribute
                lt = new Result( obj);
            if ( collection instanceof Set ) {
                if ( collection.contains( determineColValue(obj, right) ))
                    return true;
                else
                    return false;
            }
            else { //go through loop
                if (QueryUtils.compare(lt, "=", right)) {
                    toReturn = true;
                    break;
                }
            }
        }
        return toReturn;
    }

    public static Object determineColValue(Object object, Result right) {
        Type type = determineTrueType( object );
        return convert( type, right );
    }

    public static Type determineTrueType(Object object) {
        if ( object instanceof Integer )
            return Type.INTS;
        else if (object instanceof Short)
            return Type.SHORTS;
        else if ( object instanceof Long)
            return Type.LONGS;
        else if ( object instanceof Double)
            return Type.DOUBLES;
        else if ( object instanceof Float)
            return Type.FLOATS;
        else if ( object instanceof String )
            return Type.STRING;
        else if ( object instanceof HashSet)
            return Type.HASHSET;
        else if ( object instanceof Map)
            return Type.MAP;
        else if ( object instanceof List)
            return Type.LIST;
        else
            return Type.OBJECT;
    }

    public static Result existInIterator(Iterator iterator, Result right) {
        return new Result( existInIt( iterator, right));
    }

    public static boolean existInIt(Iterator iterator, Result right) {
        boolean toReturn = false;
        while ( iterator.hasNext()) {
            Result lt = null ;
            Object obj = iterator.next();
            if ( obj instanceof Collection ) {
                //check for HashSet
                if ( obj instanceof Map) {
                    if (((Map) obj).containsKey( determineColValue(obj, right) ))
                        return true;
                    else
                        return false;
                }
                else { //go through loop
                    Iterator inside = ((Collection) obj).iterator();
                    while (inside.hasNext()) {
                        lt = new Result(inside.next());
                        if (QueryUtils.compare(lt, "=", right))
                            return true;
                    }
                    //none above match
                    return false;
                } //else HashSet
            }
            else  //else non collection, primitive attribute
                lt = new Result( obj);
            if ( iterator instanceof Map ) {
                if (((Map) obj).containsKey( determineColValue(obj, right) ))
                    return true;
                else
                    return false;
            }
            else { //go through loop
                if (QueryUtils.compare(lt, "=", right)) {
                    toReturn = true;
                    break;
                }
            }
        }
        return toReturn;
    }


    public static boolean getBoolean(byte[] b, int off) {
        return b[off] != 0;
    }

    static char getChar(byte[] b, int off) {
        return (char) (((b[off + 1] & 0xFF) << 0) +
                ((b[off + 0]) << 8));
    }

    public static short getShort(byte[] b, int off) {
        return (short) (((b[off + 1] & 0xFF) << 0) +
                ((b[off + 0]) << 8));
    }

    public static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF) << 0) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off + 0]) << 24);
    }

    public static float getFloat(byte[] b, int off) {
        int i = ((b[off + 3] & 0xFF) << 0) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off + 0]) << 24);
        return Float.intBitsToFloat(i);
    }

    public static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL) << 0) +
                ((b[off + 6] & 0xFFL) << 8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off + 0]) << 56);
    }

    public static double getDouble(byte[] b, int off) {
        long j = ((b[off + 7] & 0xFFL) << 0) +
                ((b[off + 6] & 0xFFL) << 8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off + 0]) << 56);
        return Double.longBitsToDouble(j);
    }


    public static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }

    public static void putChar(byte[] b, int off, char val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
    }

    public static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
    }

    public static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val >>> 0);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off + 0] = (byte) (val >>> 24);
    }

    public static void putFloat(byte[] b, int off, float val) {
        int i = Float.floatToIntBits(val);
        b[off + 3] = (byte) (i >>> 0);
        b[off + 2] = (byte) (i >>> 8);
        b[off + 1] = (byte) (i >>> 16);
        b[off + 0] = (byte) (i >>> 24);
    }

    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val >>> 0);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off + 0] = (byte) (val >>> 56);
    }

    public static void putDouble(byte[] b, int off, double val) {
        long j = Double.doubleToLongBits(val);
        b[off + 7] = (byte) (j >>> 0);
        b[off + 6] = (byte) (j >>> 8);
        b[off + 5] = (byte) (j >>> 16);
        b[off + 4] = (byte) (j >>> 24);
        b[off + 3] = (byte) (j >>> 32);
        b[off + 2] = (byte) (j >>> 40);
        b[off + 1] = (byte) (j >>> 48);
        b[off + 0] = (byte) (j >>> 56);
    }
}

