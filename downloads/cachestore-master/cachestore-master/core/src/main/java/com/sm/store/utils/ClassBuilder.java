/*
 *
 *  * Copyright 2012-2015 Viant.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package com.sm.store.utils;

import java.lang.reflect.Constructor;

public class ClassBuilder {
    public static final String UNISON_DELTA ="com.sm.delta.impl.UnisonDelta";

    public static Object createInstance(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return createInstance(cls);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("class not found "+className, ex);
        }

    }

    public static Object createInstance(Class<?> cls) {
        Constructor<?> constructor = null;
        Object[] constructorArgs = null;
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        //take the first constructor
        try {
            if (constructors.length > 0) {
                constructor = constructors[0];
                constructor.setAccessible(true);
                Class<?>[] params = constructor.getParameterTypes();
                constructorArgs = new Object[params.length];
                for (int i = 0; i < params.length; i++) {
                    constructorArgs[i] = getParamArg(params[i]);
                }
                return constructor.newInstance(constructorArgs);
            } else
                return cls.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
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

}
