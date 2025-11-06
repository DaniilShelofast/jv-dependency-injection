package mate.academy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import mate.academy.service.FileReaderService;
import mate.academy.service.ProductParser;
import mate.academy.service.ProductService;
import mate.academy.service.impl.FileReaderServiceImpl;
import mate.academy.service.impl.ProductParserImpl;
import mate.academy.service.impl.ProductServiceImpl;

public class Injector {
    private static final Injector injector = new Injector();

    private final Map<Class<?>, Object> instances = new HashMap<>();

    public static Injector getInjector() {
        return injector;
    }

    public Object getInstance(Class<?> interfaceClazz) {
        if (!findClassImplementation(interfaceClazz).isAnnotationPresent(Component.class)) {
            throw new RuntimeException("Error");
        }
        Object clazzImplementationInstance = null;
        Class<?> fieldClazz = findFieldImplementation(interfaceClazz);
        Field[] declaredFields = fieldClazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Inject.class)) {
                Object fieldInstance = getInstance(field.getType());
                clazzImplementationInstance = createNewInstance(fieldClazz);
                try {
                    field.setAccessible(true);
                    field.set(clazzImplementationInstance, fieldInstance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can`t initialize field value. "
                            +
                            "Class: " + fieldClazz.getName()
                            +
                            "Field: " + field.getName());
                }
            }
        }

        if (clazzImplementationInstance == null) {
            clazzImplementationInstance = createNewInstance(fieldClazz);
        }
        return clazzImplementationInstance;
    }

    private Class<?> findClassImplementation(Class<?> interfaceClazz) {
        Map<Class<?>, Class<?>> map = new LinkedHashMap<>();
        map.put(FileReaderService.class, FileReaderServiceImpl.class);
        map.put(ProductParser.class, ProductParserImpl.class);
        map.put(ProductService.class, ProductServiceImpl.class);
        if (interfaceClazz.isInterface()) {
            return map.get(interfaceClazz);
        }
        return interfaceClazz;
    }

    private Object createNewInstance(Class<?> clazz) {
        if (instances.containsKey(clazz)) {
            return instances.get(clazz);
        }
        try {
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();
            instances.put(clazz, instance);
            return instance;
        } catch (InstantiationException | InvocationTargetException
                 | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Can`t create a new instance of " + clazz.getName());
        }
    }

    private Class<?> findFieldImplementation(Class<?> interfaceClazz) {
        Map<Class<?>, Class<?>> map = new LinkedHashMap<>();
        map.put(FileReaderService.class, FileReaderServiceImpl.class);
        map.put(ProductParser.class, ProductParserImpl.class);
        map.put(ProductService.class, ProductServiceImpl.class);
        if (interfaceClazz.isInterface()) {
            return map.get(interfaceClazz);
        }
        return interfaceClazz;
    }
}
