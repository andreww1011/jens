/*
 * jens - (J)ava (EN)umerable (S)ubsets
 * Copyright (C) 2022 andreww1011
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jamw.jens;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

/**
 *
 * 
 */
public abstract class Enumerables {
    
    private Enumerables() {}
    
    private static final Map<Class<? extends Enumerable<?>>,Object> ENUMERABLES;
    
    static {
        ENUMERABLES = Collections.synchronizedMap(new WeakHashMap<>());
    }
    
    @SuppressWarnings("unchecked")
    public static final
    <E extends Enumerable<E>>
    E getEnumerable(Class<E> enumerable) {
        E e = (E)ENUMERABLES.get(enumerable);
        if (e != null)    
            return e;
        ENUMERABLES.putIfAbsent(enumerable,createEnumerable(enumerable));
        return (E)ENUMERABLES.get(enumerable);
    }
    
    private static final String DYNAMIC_CLASS_NAME_SUFFIX = "_EnumerableDynImpl";
    private static final ClassLoader DEFAULT_CLASS_LOADER = Enumerables.class.getClassLoader();
    
    private static 
    <E extends Enumerable<E>>
    E createEnumerable(Class<E> enumerableClass) {
        checkIsInterface(enumerableClass);
        List<Class<?>> allInterfaces = getAllInterfaces(enumerableClass);
        List<Class<?>> itemInterfaces = getEnumerableItemInterfaces(allInterfaces);
        List<Class<?>> nonEnumerableInterfaces = getNonEnumerableInterfaces(allInterfaces,itemInterfaces);
        checkNoDeclaredAbstractMethods(nonEnumerableInterfaces);
        checkEnumerableItemInterfacesAllUniqueNames(itemInterfaces);
        return tryCreateDynamicInstance(enumerableClass,itemInterfaces);
    }

    private static void checkIsInterface(Class<?> c) {
        if (!c.isInterface())
            throw new IllegalArgumentException(
                    "Enumerable definition must be an interface.");
    }

    private static List<Class<?>> getAllInterfaces(Class<?> c) {
        List<Class<?>> allInterfaces = new ArrayList<>();
        getAllInterfaces(c, allInterfaces);
        return allInterfaces;
    }

    private static void getAllInterfaces(Class<?> c, List<Class<?>> list) {
        if (c == null || c.isAssignableFrom(Object.class))
            return;
        Class<?> superClass = c.getSuperclass();
        getAllInterfaces(superClass,list);
        Class<?>[] interfaces = c.getInterfaces();
        list.addAll(Arrays.asList(interfaces));
    }

    private static List<Class<?>> getEnumerableItemInterfaces(List<Class<?>> list) {
        return list
                .stream()
                .filter(cl -> isEnumerableItemInterfaceValid(cl))
                .collect(Collectors.toList());
    }

    private static boolean isEnumerableItemInterfaceValid(Class<?> c) {
        Annotation[] a = c.getAnnotations();
        if (a.length != 1) //magic number
            return false;
        if (!a[0].annotationType().isAssignableFrom(EnumeratedItem.class))
            return false;
        Class<?>[] ci = c.getInterfaces(); //@EnumeratedItem must not extends another interface
        if (ci.length != 0) //magic number
            return false;
        Method[] m = c.getDeclaredMethods();
        if (m.length != 1)
            return false;
        if (m[0].getParameterCount() != 0)
            return false;
        Class<?> r = m[0].getReturnType();
        return r.isAssignableFrom(Item.class);
    }

    private static List<Class<?>> getNonEnumerableInterfaces(List<Class<?>> allInterfaces, List<Class<?>> itemInterfaces) {
        List<Class<?>> nonItems = new ArrayList<>(allInterfaces);
        nonItems.removeAll(itemInterfaces);
        nonItems.remove(Enumerable.class);
        return nonItems;
    }

    private static void checkNoDeclaredAbstractMethods(List<Class<?>> list) {
        list.forEach( c -> {
            Method[] m = c.getDeclaredMethods();
            Arrays.asList(m).forEach( e -> {
                if (Modifier.isAbstract(e.getModifiers())) {
                    throw new IllegalArgumentException(
                        "Enumerable definition must not declare abstract methods: \n" +
                        "    " + c.getName() + "::" + e.toString());
                }
            });
        });
    }

    private static void checkEnumerableItemInterfacesAllUniqueNames(List<Class<?>> list) {
        List<String> names = list.stream()
                .map(c -> getItemName(c))
                .collect(Collectors.toList());
        Set<String> uniqueNames = new HashSet<>(names);
        if (uniqueNames.size() != names.size()) {
            List<String> duplicates = new ArrayList<>(names);
            duplicates.removeAll(uniqueNames);
            StringBuilder sb = new StringBuilder();
            sb.append("Enumerable definition contains duplicate item names: \n");
            duplicates.forEach(s -> sb.append(s).append("\n"));
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private static String getItemName(Class<?> c) {
        return c.getDeclaredMethods()[0].getName(); //magic number
    }
    
    private static String getItemDescription(Class<?> c) {
        return c.getAnnotation(EnumeratedItem.class).description();
    }

    private static 
    <E extends Enumerable<E>>
    E tryCreateDynamicInstance(Class<E> enumerableClass, 
                               List<Class<?>> itemInterfaces) {
        try {
            return createDynamicInstance(enumerableClass,itemInterfaces);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static
    <E extends Enumerable<E>>
    E createDynamicInstance(Class<E> enumerableClass,
                            List<Class<?>> itemInterfaces) 
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = DEFAULT_CLASS_LOADER;
        ClassLoadingStrategy<ClassLoader> classLoadingStrategy = ClassLoadingStrategy.Default.WRAPPER;
        String className = enumerableClass.getName() + DYNAMIC_CLASS_NAME_SUFFIX;
        int size = itemInterfaces.size();
        @SuppressWarnings("unchecked")
        Item<E>[] items = (Item<E>[])Array.newInstance(Item.class,size);
        DynamicType.Builder<E> builder = new ByteBuddy()
                .subclass(enumerableClass)
                .name(className);
        for (int i = 0; i < itemInterfaces.size(); i++) {
            Class<?> c = itemInterfaces.get(i);
            String itemName = getItemName(c);
            String itemDescription = getItemDescription(c);
            Item<E> item = new ItemImpl<>(enumerableClass,itemName,itemDescription,i);
            items[i] = item;
            builder = builder.method(ElementMatchers.named(itemName)).intercept(FixedValue.reference(item));
        }
        String toString = buildToString(enumerableClass,items);
        List<Item<E>> itemsList = Collections.unmodifiableList(Arrays.asList(items));
        builder = builder.method(ElementMatchers.named("toString")).intercept(FixedValue.reference(toString));
        builder = builder.method(ElementMatchers.named("size")).intercept(FixedValue.value(size));
        builder = builder.method(ElementMatchers.named("items")).intercept(FixedValue.reference(itemsList));
        return builder.make()
                .load(classLoader,classLoadingStrategy)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
    }
    
    private static 
    <E extends Enumerable<E>> 
    String buildToString(Class<E> enumerableClass, Item<E>[] items) {
        StringBuilder sb = new StringBuilder();
        sb.append(enumerableClass.getName()).append(":<");
        for (Item<E> i : items) {
            sb.append(i.name());
            if (i.ordinal() < items.length - 1) {
                sb.append(",");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    private static final class ItemImpl 
    <E extends Enumerable<E>> 
    extends Item<E>
    {
        private final Class<E> enumerableClass;
        private final String name,toString,description;
        private final int ordinal;

        private ItemImpl(final Class<E> enumerableClass, 
                         final String name, 
                         final String desciription,
                         final int ordinal) {
            this.enumerableClass = enumerableClass;
            this.name = name;
            this.toString = buildToString(enumerableClass,name);
            this.description = desciription;
            this.ordinal = ordinal;
        }

        private static 
        <E extends Enumerable<E>>
        String buildToString(final Class<?> enumerableClass, final String name) {
            return enumerableClass.getSimpleName() + ":" + name;
        }
        
        private E enumerable;
        
        @Override
        public final E enumerable() {
            if (enumerable == null)
                enumerable = getEnumerable(enumerableClass);
            return enumerable;
        }

        @Override
        public final int ordinal() {
            return ordinal;
        }

        @Override
        public final String name() {
            return name;
        }
        
        @Override
        public final String description() {
            return description;
        }

        @Override
        public final String toString() {
            return toString;
        }
    }
}
