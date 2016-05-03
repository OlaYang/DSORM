package com.meiqi.data.util;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-14
 * Time: 下午7:33
 * To change this template use File | Settings | File Templates.
 */
class UnSerializeResult {
    protected Object value;
    protected int hv;

    public UnSerializeResult() {
    }

    public UnSerializeResult(Object value, int hv) {
        this.value = value;
        this.hv = hv;
    }
}

public class PHPSerializer {
    private static Package[] __packages = Package.getPackages();
    private static final byte __Quote = 34;
    private static final byte __0 = 48;
    private static final byte __1 = 49;
    private static final byte __Colon = 58;
    private static final byte __Semicolon = 59;
    private static final byte __C = 67;
    private static final byte __N = 78;
    private static final byte __O = 79;
    private static final byte __R = 82;
    private static final byte __U = 85;
    private static final byte __Slash = 92;
    private static final byte __a = 97;
    private static final byte __b = 98;
    private static final byte __d = 100;
    private static final byte __i = 105;
    private static final byte __r = 114;
    private static final byte __s = 115;
    private static final byte __LeftB = 123;
    private static final byte __RightB = 125;
    private static final String __NAN = "NAN";
    private static final String __INF = "INF";
    private static final String __NINF = "-INF";


    private static final String UTF8 = "UTF-8";

    private PHPSerializer() {
    }

    public static byte[] serialize(Object obj) {
        return serialize(obj, UTF8);
    }

    public static byte[] serialize(Object obj, String charset) {
        HashMap<Integer, Integer> ht = new HashMap<Integer, Integer>();
        int hv = 1;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        hv = serialize(stream, obj, ht, hv, charset);
        byte[] result = stream.toByteArray();

        try {
            stream.close();
        } catch (Exception e) {
            //  LOG.error("", e);
        }
        return result;
    }

    public static int serialize(ByteArrayOutputStream stream, Object obj,
                                Map<Integer, Integer> ht, int hv, String charset) {
        if (obj == null) {
            hv++;
            writeNull(stream);
        } else {
            if (obj instanceof Boolean) {
                hv++;
                writeBoolean(stream, ((Boolean) obj).booleanValue() ? __1 : __0);
            } else if ((obj instanceof Byte) || (obj instanceof Short)
                    || (obj instanceof Integer)) {
                hv++;
                writeInteger(stream, getBytes(obj));
            } else if (obj instanceof Long) {
                hv++;
                writeDouble(stream, getBytes(obj));
            } else if (obj instanceof Float) {
                hv++;
                Float f = (Float) obj;

                if (f.isNaN()) {
                    writeDouble(stream, getBytes(__NAN));
                } else if (f.isInfinite() && f.floatValue() > 0) {
                    writeDouble(stream, getBytes(__INF));
                } else if (f.isInfinite() && f.floatValue() <= 0) {
                    writeDouble(stream, getBytes(__NINF));
                } else {
                    writeDouble(stream, getBytes(f));
                }
            } else if (obj instanceof Double) {
                hv++;
                Double d = (Double) obj;

                if (d.isNaN()) {
                    writeDouble(stream, getBytes(__NAN));
                } else if (d.isInfinite() && d.doubleValue() > 0) {
                    writeDouble(stream, getBytes(__INF));
                } else if (d.isInfinite() && d.doubleValue() <= 0) {
                    writeDouble(stream, getBytes(__NINF));
                } else {
                    writeDouble(stream, getBytes(d));
                }
            } else if ((obj instanceof Character) || (obj instanceof String)) {
                hv++;
                writeString(stream, getBytes(obj, charset));
            } else if (obj.getClass().isArray()
                    && ht.containsKey(Integer.valueOf(obj.hashCode()))) {
                writePointRef(stream,
                        getBytes(ht.get(Integer.valueOf(obj.hashCode()))));
            } else if (obj.getClass().isArray()
                    && !ht.containsKey(Integer.valueOf(obj.hashCode()))) {
                ht.put(Integer.valueOf(obj.hashCode()), Integer.valueOf(hv++));
                hv = writeArray(stream, obj, ht, hv, charset);
            } else if (obj instanceof ArrayList
                    && ht.containsKey(Integer.valueOf(obj.hashCode()))) {
                writePointRef(stream,
                        getBytes(ht.get(Integer.valueOf(obj.hashCode()))));
            } else if (obj instanceof ArrayList
                    && !ht.containsKey(Integer.valueOf(obj.hashCode()))) {
                ht.put(Integer.valueOf(obj.hashCode()), Integer.valueOf(hv++));
                hv = writeArrayList(stream, (ArrayList<?>) obj, ht, hv, charset);
            } else if (obj instanceof HashMap) {
                if (ht.containsKey(Integer.valueOf(obj.hashCode()))) {
                    writePointRef(stream,
                            getBytes(ht.get(Integer.valueOf(obj.hashCode()))));
                } else {
                    ht.put(Integer.valueOf(obj.hashCode()),
                            Integer.valueOf(hv++));
                    hv = writeHashMap(stream, (HashMap<?, ?>) obj, ht, hv,
                            charset);
                }
            } else {
                if (ht.containsKey(Integer.valueOf(obj.hashCode()))) {
                    hv++;
                    writeRef(stream,
                            getBytes(ht.get(Integer.valueOf(obj.hashCode()))));
                } else {
                    ht.put(Integer.valueOf(obj.hashCode()),
                            Integer.valueOf(hv++));
                    hv = writeObject(stream, obj, ht, hv, charset);
                }
            }
        }
        return hv;
    }

    private static void writeNull(ByteArrayOutputStream stream) {
        stream.write(__N);
        stream.write(__Semicolon);
    }

    private static void writeRef(ByteArrayOutputStream stream, byte[] r) {
        stream.write(__r);
        stream.write(__Colon);
        stream.write(r, 0, r.length);
        stream.write(__Semicolon);
    }

    private static void writePointRef(ByteArrayOutputStream stream, byte[] p) {
        stream.write(__R);
        stream.write(__Colon);
        stream.write(p, 0, p.length);
        stream.write(__Semicolon);
    }

    private static void writeBoolean(ByteArrayOutputStream stream, byte b) {
        stream.write(__b);
        stream.write(__Colon);
        stream.write(b);
        stream.write(__Semicolon);
    }

    private static void writeInteger(ByteArrayOutputStream stream, byte[] i) {
        stream.write(__i);
        stream.write(__Colon);
        stream.write(i, 0, i.length);
        stream.write(__Semicolon);
    }

    private static void writeDouble(ByteArrayOutputStream stream, byte[] d) {
        stream.write(__d);
        stream.write(__Colon);
        stream.write(d, 0, d.length);
        stream.write(__Semicolon);
    }

    private static void writeString(ByteArrayOutputStream stream, byte[] s) {
        byte[] slen = getBytes(Integer.valueOf(s.length));
        stream.write(__s);
        stream.write(__Colon);
        stream.write(slen, 0, slen.length);
        stream.write(__Colon);
        stream.write(__Quote);
        stream.write(s, 0, s.length);
        stream.write(__Quote);
        stream.write(__Semicolon);
    }

    private static int writeArray(ByteArrayOutputStream stream, Object a,
                                  Map<Integer, Integer> ht, int hv, String charset) {
        int len = Array.getLength(a);
        byte[] alen = getBytes(Integer.valueOf(len));
        stream.write(__a);
        stream.write(__Colon);
        stream.write(alen, 0, alen.length);
        stream.write(__Colon);
        stream.write(__LeftB);
        for (int i = 0; i < len; i++) {
            writeInteger(stream, getBytes(Integer.valueOf(i)));
            hv = serialize(stream, Array.get(a, i), ht, hv, charset);
        }
        stream.write(__RightB);
        return hv;
    }

    private static int writeArrayList(ByteArrayOutputStream stream, List<?> a,
                                      Map<Integer, Integer> ht, int hv, String charset) {
        int len = a.size();
        byte[] alen = getBytes(Integer.valueOf(len));

        stream.write(__a);
        stream.write(__Colon);
        stream.write(alen, 0, alen.length);
        stream.write(__Colon);
        stream.write(__LeftB);
        for (int i = 0; i < len; i++) {
            writeInteger(stream, getBytes(Integer.valueOf(i)));
            hv = serialize(stream, a.get(i), ht, hv, charset);
        }
        stream.write(__RightB);
        return hv;
    }

    private static int writeHashMap(ByteArrayOutputStream stream, Map<?, ?> h,
                                    Map<Integer, Integer> ht, int hv, String charset) {
        int len = h.size();
        byte[] hlen = getBytes(Integer.valueOf(len));

        stream.write(__a);
        stream.write(__Colon);
        stream.write(hlen, 0, hlen.length);
        stream.write(__Colon);
        stream.write(__LeftB);
        for (Iterator<?> keys = h.keySet().iterator(); keys.hasNext(); ) {
            Object key = keys.next();

            if ((key instanceof Byte) || (key instanceof Short)
                    || (key instanceof Integer)) {
                writeInteger(stream, getBytes(key));
            } else if (key instanceof Boolean) {
                writeInteger(
                        stream,
                        new byte[]{((Boolean) key).booleanValue() ? __1 : __0});
            } else {
                writeString(stream, getBytes(key, charset));
            }
            hv = serialize(stream, h.get(key), ht, hv, charset);
        }
        stream.write(__RightB);
        return hv;
    }

    private static int writeObject(ByteArrayOutputStream stream, Object obj,
                                   Map<Integer, Integer> ht, int hv, String charset) {
        Class<?> cls = obj.getClass();

        if (obj instanceof java.io.Serializable) {
            byte[] className = getBytes(getClassName(cls), charset);
            byte[] classNameLen = getBytes(Integer.valueOf(className.length));

            if (obj instanceof Serializable) {
                byte[] cs = ((Serializable) obj).serialize();
                byte[] cslen = getBytes(Integer.valueOf(cs.length));

                stream.write(__C);
                stream.write(__Colon);
                stream.write(classNameLen, 0, classNameLen.length);
                stream.write(__Colon);
                stream.write(__Quote);
                stream.write(className, 0, className.length);
                stream.write(__Quote);
                stream.write(__Colon);
                stream.write(cslen, 0, cslen.length);
                stream.write(__Colon);
                stream.write(__LeftB);
                stream.write(cs, 0, cs.length);
                stream.write(__RightB);
            } else {
                Method __sleep;

                try {
                    __sleep = cls.getMethod("__sleep", new Class[0]);
                } catch (Exception e) {
                    // LOG.error("", e);
                    __sleep = null;
                }
                Field[] f;

                if (__sleep != null) {
                    String[] fieldNames;

                    try {
                        __sleep.setAccessible(true);
                        fieldNames = (String[]) __sleep.invoke(obj,
                                new Object[0]);
                    } catch (Exception e) {
                        // LOG.error("", e);
                        fieldNames = null;
                    }
                    f = getFields(obj, fieldNames);
                } else {
                    f = getFields(obj);
                }
                AccessibleObject.setAccessible(f, true);
                byte[] flen = getBytes(Integer.valueOf(f.length));

                stream.write(__O);
                stream.write(__Colon);
                stream.write(classNameLen, 0, classNameLen.length);
                stream.write(__Colon);
                stream.write(__Quote);
                stream.write(className, 0, className.length);
                stream.write(__Quote);
                stream.write(__Colon);
                stream.write(flen, 0, flen.length);
                stream.write(__Colon);
                stream.write(__LeftB);
                for (int i = 0, len = f.length; i < len; i++) {
                    int mod = f[i].getModifiers();

                    if (Modifier.isPublic(mod)) {
                        writeString(stream, getBytes(f[i].getName(), charset));
                    } else if (Modifier.isProtected(mod)) {
                        writeString(stream,
                                getBytes("\0*\0" + f[i].getName(), charset));
                    } else {
                        writeString(
                                stream,
                                getBytes(
                                        "\0"
                                                + getClassName(f[i]
                                                .getDeclaringClass())
                                                + "\0" + f[i].getName(),
                                        charset));
                    }
                    Object o;

                    try {
                        o = f[i].get(obj);
                    } catch (Exception e) {
                        //  LOG.error("", e);
                        o = null;
                    }
                    hv = serialize(stream, o, ht, hv, charset);
                }
                stream.write(__RightB);
            }
        } else {
            writeNull(stream);
        }
        return hv;
    }

    private static byte[] getBytes(Object obj) {
        try {
            return obj.toString().getBytes("US-ASCII");
        } catch (Exception e) {
            // LOG.error("", e);
            return obj.toString().getBytes();
        }
    }

    private static byte[] getBytes(Object obj, String charset) {
        try {
            return obj.toString().getBytes(charset);
        } catch (Exception e) {
            //  LOG.error("", e);
            return obj.toString().getBytes();
        }
    }

    private static String getString(byte[] data, String charset) {
        try {
            return new String(data, charset);
        } catch (Exception e) {
            // LOG.error("", e);
            return new String(data);
        }
    }

    private static Class<?> getClass(String className) {
        try {
            Class<?> cls = Class.forName(className);

            return cls;
        } catch (Exception e) {
            // LOG.error("", e);
        }
        for (int i = 0; i < __packages.length; i++) {
            try {
                Class<?> cls = Class.forName(__packages[i].getName() + "."
                        + className);
                return cls;
            } catch (Exception e) {
                // LOG.error("", e);
            }
        }
        return null;
    }

    private static String getClassName(Class<?> cls) {
        return cls.getName().substring(cls.getPackage().getName().length() + 1);
    }

    private static Field getField(Object obj, String fieldName) {
        Class<?> cls = obj.getClass();

        while (cls != null) {
            try {
                Field result = cls.getDeclaredField(fieldName);
                int mod = result.getModifiers();

                if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
                    return null;
                }
                return result;
            } catch (Exception e) {
                //LOG.error("", e);
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static Field[] getFields(Object obj, String[] fieldNames) {
        if (fieldNames == null) {
            return getFields(obj);
        }
        int n = fieldNames.length;
        ArrayList<Field> fields = new ArrayList<Field>(n);

        for (int i = 0; i < n; i++) {
            Field f = getField(obj, fieldNames[i]);

            if (f != null) {
                fields.add(f);
            }
        }
        return fields.toArray(new Field[0]);
    }

    private static Field[] getFields(Object obj) {
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> cls = obj.getClass();

        while (cls != null) {
            Field[] fs = cls.getDeclaredFields();

            for (int i = 0; i < fs.length; i++) {
                int mod = fs[i].getModifiers();

                if (!Modifier.isFinal(mod) && !Modifier.isStatic(mod)) {
                    fields.add(fs[i]);
                }
            }
            cls = cls.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    public static Object newInstance(Class<?> cls) {
        try {
            Constructor<?> ctor = cls.getConstructor(new Class[0]);
            int mod = ctor.getModifiers();

            if (Modifier.isPublic(mod)) {
                return ctor.newInstance(new Object[0]);
            }
        } catch (Exception e) {
            /// LOG.error("", e);
        }
        try {
            Constructor<?> ctor = cls
                    .getConstructor(new Class[]{Integer.TYPE});
            int mod = ctor.getModifiers();

            if (Modifier.isPublic(mod)) {
                return ctor.newInstance(new Object[]{Integer.valueOf(0)});
            }
        } catch (Exception e) {
            // LOG.error("", e);
        }
        try {
            Constructor<?> ctor = cls
                    .getConstructor(new Class[]{Boolean.TYPE});
            int mod = ctor.getModifiers();

            if (Modifier.isPublic(mod)) {
                return ctor.newInstance(new Object[]{Boolean.FALSE});
            }
        } catch (Exception e) {
            //LOG.error("", e);
        }
        try {
            Constructor<?> ctor = cls
                    .getConstructor(new Class[]{String.class});
            int mod = ctor.getModifiers();

            if (Modifier.isPublic(mod)) {
                return ctor.newInstance(new Object[]{""});
            }
        } catch (Exception e) {
            // LOG.error("", e);
        }
        Field[] f = cls.getFields();

        for (int i = 0; i < f.length; i++) {
            if (f[i].getType() == cls && Modifier.isStatic(f[i].getModifiers())) {
                try {
                    return f[i].get(null);
                } catch (Exception e) {
                    //  LOG.error("", e);
                }
            }
        }
        Method[] m = cls.getMethods();

        for (int i = 0; i < m.length; i++) {
            if (m[i].getReturnType() == cls
                    && Modifier.isStatic(m[i].getModifiers())) {
                try {
                    return m[i].invoke(null, new Object[0]);
                } catch (Exception e) {
                    // LOG.error("", e);
                }
                try {
                    return m[i].invoke(null,
                            new Object[]{Integer.valueOf(0)});
                } catch (Exception e) {
                    // LOG.error("", e);
                }
                try {
                    return m[i].invoke(null, new Object[]{Boolean.FALSE});
                } catch (Exception e) {
                    // LOG.error("", e);
                }
                try {
                    return m[i].invoke(null, new Object[]{""});
                } catch (Exception e) {
                    // LOG.error("", e);
                }
            }
        }
        return null;
    }

    public static Number cast(Number n, Class<?> destClass) {
        if (destClass == Byte.class) {
            return new Byte(n.byteValue());
        }
        if (destClass == Short.class) {
            return new Short(n.shortValue());
        }
        if (destClass == Integer.class) {
            return Integer.valueOf(n.intValue());
        }
        if (destClass == Long.class) {
            return new Long(n.longValue());
        }
        if (destClass == Float.class) {
            return new Float(n.floatValue());
        }
        if (destClass == Double.class) {
            return new Double(n.doubleValue());
        }
        return n;
    }

    public static Object cast(Object obj, Class<?> destClass) {
        if (obj == null || destClass == null) {
            return obj;
        } else if (obj.getClass() == destClass) {
            return obj;
        } else if (obj instanceof Number) {
            return cast((Number) obj, destClass);
        } else if ((obj instanceof String) && destClass == Character.class) {
            return new Character(((String) obj).charAt(0));
        } else if ((obj instanceof ArrayList) && destClass.isArray()) {
            return toArray((ArrayList<?>) obj, destClass.getComponentType());
        } else if ((obj instanceof ArrayList) && destClass == Map.class) {
            return toMap((ArrayList<?>) obj);
        } else {
            return obj;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map<Integer, ?> toMap(List<?> a) {
        int n = a.size();
        Map h = new HashMap(n);

        for (int i = 0; i < n; i++) {
            h.put(Integer.valueOf(i), a.get(i));
        }
        return h;
    }

    private static Object toArray(List<?> obj, Class<?> componentType) {
        int n = obj.size();
        Object a = Array.newInstance(componentType, n);

        for (int i = 0; i < n; i++) {
            Array.set(a, i, cast(obj.get(i), componentType));
        }
        return a;
    }

    private static int getPos(ByteArrayInputStream stream) {
        try {
            Field pos = stream.getClass().getDeclaredField("pos");

            pos.setAccessible(true);
            return pos.getInt(stream);
        } catch (Exception e) {
            // LOG.error("", e);
            return 0;
        }
    }

    private static void setPos(ByteArrayInputStream stream, int p) {
        try {
            Field pos = stream.getClass().getDeclaredField("pos");

            pos.setAccessible(true);
            pos.setInt(stream, p);
        } catch (Exception e) {
            // LOG.error("", e);
        }
    }

    public static Object unserialize(byte[] ss) throws IllegalAccessException {
        return unserialize(ss, null, UTF8);
    }

    public static Object unserialize(byte[] ss, String charset)
            throws IllegalAccessException {
        return unserialize(ss, null, charset);
    }

    public static Object unserialize(byte[] ss, Class<?> cls)
            throws IllegalAccessException {
        return unserialize(ss, cls, UTF8);
    }

    public static Object unserialize(byte[] ss, Class<?> cls, String charset)
            throws IllegalAccessException {
        int hv = 1;
        ByteArrayInputStream stream = new ByteArrayInputStream(ss);
        Object result = unserialize(stream, new HashMap<Integer, Object>(), hv,
                new HashMap<Integer, Boolean>(), charset).value;
        try {
            stream.close();
        } catch (Exception e) {
            // LOG.error("Php unserialize error", e);
        }
        return cast(result, cls);
    }

    private static UnSerializeResult unserialize(ByteArrayInputStream stream,
                                                 Map<Integer, Object> ht, int hv, Map<Integer, Boolean> rt,
                                                 String charset) throws IllegalAccessException {
        Object obj;

        switch (stream.read()) {
            case __N:
                obj = readNull(stream);
                ht.put(Integer.valueOf(hv++), obj);
                return new UnSerializeResult(obj, hv);

            case __b:
                obj = readBoolean(stream);
                ht.put(Integer.valueOf(hv++), obj);
                return new UnSerializeResult(obj, hv);

            case __i:
                obj = readInteger(stream);
                ht.put(Integer.valueOf(hv++), obj);
                return new UnSerializeResult(obj, hv);

            case __d:
                obj = readDouble(stream);
                ht.put(Integer.valueOf(hv++), obj);
                return new UnSerializeResult(obj, hv);

            case __s:
                obj = readString(stream, charset);
                ht.put(Integer.valueOf(hv++), obj);
                return new UnSerializeResult(obj, hv);

            case __U:
                obj = readUnicodeString(stream);
                ht.put(Integer.valueOf(hv++), obj);
                return new UnSerializeResult(obj, hv);

            case __r:
                return readRef(stream, ht, hv, rt);

            case __a:
                return readArray(stream, ht, hv, rt, charset);

            case __O:
                return readObject(stream, ht, hv, rt, charset);

            case __C:
                return readCustomObject(stream, ht, hv, charset);

            case __R:
                return readPointRef(stream, ht, hv, rt);

            default:
                return null;
        }
    }

    private static String readNumber(ByteArrayInputStream stream) {
        StringBuilder sb = new StringBuilder();
        int i = stream.read();

        while ((i != __Semicolon) && (i != __Colon)) {
            sb.append((char) i);
            i = stream.read();
        }
        return sb.toString();
    }

    private static Object readNull(ByteArrayInputStream stream) {
        stream.skip(1);
        return null;
    }

    private static Boolean readBoolean(ByteArrayInputStream stream) {
        stream.skip(1);
        Boolean b = Boolean.valueOf(stream.read() == __1);
        stream.skip(1);
        return b;
    }

    private static Number readInteger(ByteArrayInputStream stream) {
        stream.skip(1);
        String i = readNumber(stream);

        try {
            return new Byte(i);
        } catch (Exception e1) {
            // LOG.error("", e1);
            try {
                return new Short(i);
            } catch (Exception e2) {
                // LOG.error("", e2);
                return Integer.valueOf(i);
            }
        }
    }

    private static Number readDouble(ByteArrayInputStream stream) {
        stream.skip(1);
        String d = readNumber(stream);

        if (d.equals(__NAN)) {
            return new Double(Double.NaN);
        }
        if (d.equals(__INF)) {
            return new Double(Double.POSITIVE_INFINITY);
        }
        if (d.equals(__NINF)) {
            return new Double(Double.NEGATIVE_INFINITY);
        }
        try {
            return new Long(d);
        } catch (Exception e1) {
            //  LOG.error("", e1);
            try {
                Float f = new Float(d);
                if (f.isInfinite()) {
                    return new Double(d);
                } else {
                    return f;
                }
            } catch (Exception e2) {
                // LOG.error("", e2);
                return new Float(0);
            }
        }
    }

    private static String readString(ByteArrayInputStream stream, String charset) {
        stream.skip(1);
        int len = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        byte[] buf = new byte[len];

        stream.read(buf, 0, len);
        String s = getString(buf, charset);

        stream.skip(2);
        return s;
    }

    private static String readUnicodeString(ByteArrayInputStream stream) {
        stream.skip(1);
        int l = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        StringBuilder sb = new StringBuilder(l);
        int c;

        for (int i = 0; i < l; i++) {
            c = stream.read();
            if (c == __Slash) {
                char c1 = (char) stream.read();
                char c2 = (char) stream.read();
                char c3 = (char) stream.read();
                char c4 = (char) stream.read();

                sb.append((char) (Integer.parseInt(new String(new char[]{c1,
                        c2, c3, c4}), 16)));
            } else {
                sb.append((char) c);
            }
        }
        stream.skip(2);
        return sb.toString();
    }

    private static UnSerializeResult readRef(ByteArrayInputStream stream,
                                             Map<Integer, Object> ht, int hv, Map<Integer, Boolean> rt) {
        stream.skip(1);
        Integer r = Integer.valueOf(readNumber(stream));

        if (rt.containsKey(r)) {
            rt.put(r, Boolean.TRUE);
        }
        Object obj = ht.get(r);

        ht.put(Integer.valueOf(hv++), obj);
        return new UnSerializeResult(obj, hv);
    }

    private static UnSerializeResult readPointRef(ByteArrayInputStream stream,
                                                  Map<Integer, Object> ht, int hv, Map<Integer, Boolean> rt) {
        stream.skip(1);
        Integer r = Integer.valueOf(readNumber(stream));

        if (rt.containsKey(r)) {
            rt.put(r, Boolean.TRUE);
        }
        Object obj = ht.get(r);

        return new UnSerializeResult(obj, hv);
    }

    private static UnSerializeResult readArray(ByteArrayInputStream stream,
                                               Map<Integer, Object> ht, int hv, Map<Integer, Boolean> rt,
                                               String charset) throws IllegalAccessException {
        stream.skip(1);
        int n = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        HashMap<Object, Object> h = new HashMap<Object, Object>(n);
        ArrayList<Object> al = new ArrayList<Object>(n);
        Integer r = Integer.valueOf(hv);

        rt.put(r, Boolean.FALSE);
        int p = getPos(stream);

        ht.put(Integer.valueOf(hv++), h);
        for (int i = 0; i < n; i++) {
            Object key;

            switch (stream.read()) {
                case __i:
                    key = cast(readInteger(stream), Integer.class);
                    break;

                case __s:
                    key = readString(stream, charset);
                    break;

                case __U:
                    key = readUnicodeString(stream);
                    break;

                default:
                    return null;
            }
            UnSerializeResult result = unserialize(stream, ht, hv, rt, charset);

            hv = result.hv;
            if (al != null) {
                if ((key instanceof Integer)
                        && (((Integer) key).intValue() == i)) {
                    al.add(result.value);
                } else {
                    al = null;
                }
            }
            h.put(key, result.value);
        }
        if (al != null) {
            ht.put(r, al);
            if ((rt.get(r)).booleanValue()) {
                hv = r.intValue() + 1;
                setPos(stream, p);
                for (int i = 0; i < n; i++) {
                    int key;

                    switch (stream.read()) {
                        case __i:
                            key = ((Integer) cast(readInteger(stream),
                                    Integer.class)).intValue();
                            break;

                        default:
                            return null;
                    }
                    UnSerializeResult result = unserialize(stream, ht, hv, rt,
                            charset);

                    hv = result.hv;
                    al.set(key, result.value);
                }
            }
        }
        rt.remove(r);
        stream.skip(1);
        return new UnSerializeResult(ht.get(r), hv);
    }

    @SuppressWarnings("unchecked")
    private static UnSerializeResult readObject(ByteArrayInputStream stream,
                                                Map<Integer, Object> ht, int hv, Map<Integer, Boolean> rt,
                                                String charset) throws IllegalAccessException {
        stream.skip(1);
        int len = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        byte[] buf = new byte[len];

        stream.read(buf, 0, len);
        String cn = getString(buf, charset);

        stream.skip(2);
        int n = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        Class<?> cls = getClass(cn);
        Object o;

        if (cls != null) {
            o = newInstance(cls);
            if (o == null) {
                o = new HashMap<Object, Object>(n);
            }
        } else {
            o = new HashMap<Object, Object>(n);
        }
        ht.put(Integer.valueOf(hv++), o);
        for (int i = 0; i < n; i++) {
            String key;

            switch (stream.read()) {
                case __s:
                    key = readString(stream, charset);
                    break;

                case __U:
                    key = readUnicodeString(stream);
                    break;

                default:
                    return null;
            }
            if (key.charAt(0) == (char) 0) {
                key = key.substring(key.indexOf('\0', 1) + 1);
            }
            UnSerializeResult result = unserialize(stream, ht, hv, rt, charset);

            hv = result.hv;
            if (o instanceof HashMap) {
                ((HashMap<String, Object>) o).put(key, result.value);
            } else {
                Field f = getField(o, key);

                f.setAccessible(true);
                f.set(o, result.value);
            }
        }
        stream.skip(1);
        Method __wakeup = null;

        try {
            __wakeup = o.getClass().getMethod("__wakeup", new Class[0]);
            __wakeup.invoke(o, new Object[0]);
        } catch (Exception e) {
            // LOG.error("", e);
        }
        return new UnSerializeResult(o, hv);
    }

    private static UnSerializeResult readCustomObject(
            ByteArrayInputStream stream, Map<Integer, Object> ht, int hv,
            String charset) {
        stream.skip(1);
        int len = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        byte[] buf = new byte[len];

        stream.read(buf, 0, len);
        String cn = getString(buf, charset);

        stream.skip(2);
        int n = Integer.parseInt(readNumber(stream));

        stream.skip(1);
        Class<?> cls = getClass(cn);
        Object o;

        if (cls != null) {
            o = newInstance(cls);
        } else {
            o = null;
        }
        ht.put(Integer.valueOf(hv++), o);
        if (o == null) {
            stream.skip(n);
        } else if (o instanceof Serializable) {
            byte[] b = new byte[n];

            stream.read(b, 0, n);
            ((Serializable) o).unserialize(b);
        } else {
            stream.skip(n);
        }
        stream.skip(1);
        return new UnSerializeResult(o, hv);
    }

    public static void main(String args[]) throws Exception{
        String json = "a:7:{s:16:\"payout_type_info\";a:1:{i:0;a:2:{s:12:\"payout_group\";s:15:\"程序开发组\";s:12:\"payout_price\";s:7:\"1231231\";}}s:12:\"payment_info\";a:11:{s:11:\"receive_man\";s:4:\"1111\";s:11:\"price_total\";s:7:\"1231231\";s:12:\"payment_type\";s:12:\"银行转账\";s:12:\"payment_card\";s:3:\"111\";s:12:\"payment_bank\";s:18:\"农业发展银行\";s:16:\"payment_province\";s:6:\"湖南\";s:12:\"payment_city\";s:6:\"娄底\";s:16:\"payment_discrict\";s:2:\"11\";s:15:\"payment_subbank\";s:2:\"11\";s:11:\"is_topublic\";s:2:\"no\";s:14:\"public_account\";s:0:\"\";}s:12:\"approve_info\";N;s:14:\"financial_info\";a:3:{s:11:\"receive_man\";s:4:\"1111\";s:12:\"payment_type\";s:12:\"银行转账\";s:12:\"payment_card\";s:3:\"111\";}s:12:\"act_log_info\";a:23:{s:16:\"director_auditer\";s:0:\"\";s:15:\"director_status\";s:0:\"\";s:13:\"director_time\";s:0:\"\";s:12:\"area_manager\";s:0:\"\";s:19:\"area_manager_status\";s:0:\"\";s:17:\"area_manager_time\";s:0:\"\";s:12:\"area_finance\";s:0:\"\";s:19:\"area_finance_status\";s:0:\"\";s:17:\"area_finance_time\";s:0:\"\";s:13:\"finance_total\";s:0:\"\";s:20:\"finance_total_status\";s:0:\"\";s:18:\"finance_total_time\";s:0:\"\";s:14:\"managerAuditer\";s:0:\"\";s:19:\"managerAudit_status\";s:0:\"\";s:17:\"managerAudit_time\";s:0:\"\";s:11:\"CFO_Auditer\";s:0:\"\";s:10:\"CFO_status\";s:0:\"\";s:8:\"CFO_time\";s:0:\"\";s:9:\"bossAudit\";s:0:\"\";s:16:\"bossAudit_status\";s:0:\"\";s:14:\"bossAudit_time\";s:0:\"\";s:11:\"approve_man\";s:0:\"\";s:12:\"approve_note\";s:0:\"\";}s:11:\"normal_info\";a:11:{s:9:\"payout_sn\";s:13:\"1304141727251\";s:11:\"payout_name\";s:21:\"11特疼撒旦法test\";s:11:\"payout_type\";s:21:\"部门备用金借款\";s:7:\"ask_man\";s:6:\"何仅\";s:8:\"add_time\";s:19:\"2013-04-14T17:27:25\";s:9:\"bill_type\";s:0:\"\";s:7:\"bill_sn\";s:0:\"\";s:11:\"payout_note\";s:110:\"乌鲁木齐体验馆新仓库租用时间2013年4月9日-2014年4月8日  房租56000元，面积：450平米\";s:7:\"app_url\";s:52:\"http://mlldoc.lejj.com/DocLib43/1304141727251.xml\";s:8:\"order_sn\";s:0:\"\";s:15:\"fast_requirepay\";s:0:\"\";}s:9:\"mail_info\";a:3:{s:9:\"mail_info\";s:17:\"hejin@lejj.com\";s:17:\"area_manager_mail\";s:0:\"\";s:13:\"director_mail\";s:0:\"\";}}\n";
        Object decode = PHPSerializer.unserialize(json.getBytes());
        System.out.println(JSON.toJSONString(decode));
    }
}
