package com.xtremelabs.robolectric.bytecode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"UnusedDeclaration"})
public class RobolectricInternals {
    // initialized via magic by RobolectricContext
    private static ClassHandler classHandler;
    private static final Map<Class, Field> shadowFieldMap = new HashMap<Class, Field>();

    public static ClassHandler getClassHandler() {
        return classHandler;
    }

    public static <T> T newInstanceOf(Class<T> clazz) {
        try {
            Constructor<T> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<T> clazz, Class[] parameterTypes, Object[] params) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(parameterTypes);
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance(params);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T directlyOn(T shadowedObject) {
        Vars vars = getVars();

        if (vars.callDirectly != null) {
            Object expectedInstance = vars.callDirectly;
            vars.callDirectly = null;
            throw new RuntimeException("already expecting a direct call on <" + expectedInstance + "> but here's a new request for <" + shadowedObject + ">", vars.stackTraceThrowable);
        }

        vars.callDirectly = shadowedObject;
        vars.stackTraceThrowable = new Throwable("original call to directlyOn()");
        return shadowedObject;
    }

    private static Vars getVars() {
        return Vars.ALL_VARS.get();
    }

    public static boolean shouldCallDirectly(Object directInstance) {
        Vars vars = getVars();
        if (vars.callDirectly != null) {
            if (vars.callDirectly != directInstance) {
                Object expectedInstance = vars.callDirectly;
                vars.callDirectly = null;
                throw new RuntimeException("expected to perform direct call on " + desc(expectedInstance)
                        + " but got " + desc(directInstance), vars.stackTraceThrowable);
            } else {
                vars.callDirectly = null;
            }
            return true;
        } else {
            return false;
        }
    }

    private static String desc(Object expectedInstance) {
        return (expectedInstance instanceof Class) ? "class " + ((Class) expectedInstance).getName() : "instance of " + expectedInstance.getClass().getName();
    }

    public static Field getShadowField(Object instance) {
        Class clazz = instance.getClass();
        Field field = shadowFieldMap.get(clazz);
        if (field == null) {
            try {
                field = clazz.getField(AndroidTranslator.CLASS_HANDLER_DATA_FIELD_NAME);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(instance.getClass().getName() + " has no shadow field", e);
            }
            shadowFieldMap.put(clazz, field);
        }
        return field;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void classInitializing(Class clazz) throws Exception {
        classHandler.classInitializing(clazz);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object methodInvoked(Class clazz, String methodName, Object instance, String[] paramTypes, Object[] params) throws Throwable {
        try {
          return classHandler.methodInvoked(clazz, methodName, instance, paramTypes, params);
        } catch(java.lang.LinkageError e) {
          throw new Exception(e);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(Object o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(boolean o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(byte o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(char o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(short o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(int o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(long o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(float o) {
        return o;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static Object autobox(double o) {
        return o;
    }
}
