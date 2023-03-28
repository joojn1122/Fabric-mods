package com.joojn.meteoraddon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class BytecodeUtils {

    public static byte[] getBytecode(String className) throws ClassNotFoundException, IOException {
        Class<?> clazz = Class.forName(className);

        String resourceName = clazz.getName().replace('.', '/') + ".class";
        URL resourceUrl = clazz.getClassLoader().getResource(resourceName);

        if (resourceUrl == null) {
            throw new ClassNotFoundException(className + " not found");
        }

        try(InputStream is = resourceUrl.openStream()){
            return is.readAllBytes();
        }
    }


    public static boolean classExists(String internalName) {
        try {
            Class.forName(internalName);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

