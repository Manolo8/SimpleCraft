package com.github.manolo8.simplecraft.core.commands.line;

import com.github.manolo8.simplecraft.core.commands.line.annotation.CheckerOptions;
import com.github.manolo8.simplecraft.core.commands.line.inf.Checker;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.commands.line.inf.SupplierHelper;
import com.github.manolo8.simplecraft.utils.reflection.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OptionsRegistry {

    private final List<SupplierHelper> suppliers;
    private final List<Checker> checkers;

    public OptionsRegistry() {
        suppliers = new ArrayList<>();
        checkers = new ArrayList<>();
    }

    public void build(Object object) {
        Class objectClass = object.getClass();

        for (Class clazz : objectClass.getDeclaredClasses()) {
            if (Supplier.Convert.class.isAssignableFrom(clazz)) {

                Supplier.Convert convert = (Supplier.Convert) ReflectionUtils.createConstructor(clazz, object);

                suppliers.add(new SupplierHelper.Convert(convert));

            } else if (Supplier.Basic.class.isAssignableFrom(clazz)) {
                Supplier.Basic basic = (Supplier.Basic) ReflectionUtils.createConstructor(clazz, object);

                suppliers.add(new SupplierHelper.Basic(basic));
            }
        }

        for (Method method : objectClass.getDeclaredMethods()) {
            CheckerOptions options = method.getAnnotation(CheckerOptions.class);

            if (options == null) continue;

            checkers.add(new Checker(object, method, options));
        }
    }

    public Checker findChecker(String key) {
        for (Checker checker : checkers) {
            if (checker.getValue().equals(key)) {
                return checker;
            }
        }

        System.out.println("!!!ERROR!!!");
        System.out.println("Checker " + key + " not found!");
        System.out.println("!!!ERROR!!!");

        return null;
    }

    public SupplierHelper.Convert findConvert(String key, Class clazz) {

        for (SupplierHelper helper : suppliers) {
            if (helper instanceof SupplierHelper.Convert) {
                SupplierHelper.Convert convert = (SupplierHelper.Convert) helper;

                if (matchOrPrimitiveClass(convert.getSuppliedClass(), clazz)) {
                    for (String str : convert.getKeys()) {
                        if (str.equals(key)) {
                            return convert;
                        }
                    }
                }

            }
        }

        System.out.println("!!!ERROR!!!");
        System.out.println("convert for " + key + " " + clazz.getSimpleName() + " not found!");
        System.out.println("!!!ERROR!!!");

        return null;
    }

    public SupplierHelper.Basic findProvider(Class clazz, boolean global) {

        for (SupplierHelper helper : suppliers) {
            Class supplied = helper.getSuppliedClass();

            if (helper instanceof SupplierHelper.Basic
                    && (supplied.isAssignableFrom(clazz)
                    || clazz == supplied)) {
                return ((SupplierHelper.Basic) helper).copy(clazz, global);
            }
        }

        System.out.println("!!!ERROR!!!");
        System.out.println("Basic named " + clazz.getSimpleName() + " not found!");
        System.out.println("!!!ERROR!!!");

        return null;
    }

    private boolean matchOrPrimitiveClass(Class one, Class two) {

        if (one == two) return true;
        else if (two.isPrimitive()) {
            if (two == Double.TYPE) {
                return one == Double.class;
            } else if (two == Integer.TYPE) {
                return one == Integer.class;
            } else if (two == Long.TYPE) {
                return one == Long.class;
            } else if (two == Boolean.TYPE) {
                return one == Boolean.class;
            }
        }
        return false;
    }

}
