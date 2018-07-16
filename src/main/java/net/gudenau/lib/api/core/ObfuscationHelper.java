package net.gudenau.lib.api.core;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gudenau on 1/11/2017.
 * <p>
 * GudLib
 */
public final class ObfuscationHelper {
    private static final Map<String, String> classNameCache = new HashMap<>();
    private static final Map<String, String> methodNameCache = new HashMap<>();
    private static final Map<String, String> methodDescriptionCache = new HashMap<>();

    @Nonnull
    public static String getObfuscatedClassName(@Nonnull String className) {
        if(classNameCache.containsKey(className)){
            return classNameCache.get(className);
        }

        String name = className.replaceAll("\\.", "/");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ObfuscationHelper.class.getResourceAsStream("/assets/gud_lib/notch-mcp.srg")))){
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] split = line.split(": ", 2);
                if(split[0].equals("CL")){
                    split = split[1].split(" ");
                    if(name.equals(split[1])){
                        name = split[0];
                        break;
                    }
                }
            }
        }catch (IOException e){
            throw new RuntimeException("Error reading mappings!", e);
        }

        name = name.replaceAll("/", ".");
        classNameCache.put(className, name);
        return name;
    }

    private static void cacheMethod(@Nonnull String methodName, @Nonnull String methodDescription){
        String name = methodName.replaceAll("\\.", "/");
        String description = methodDescription.replaceAll("\\.", "/");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ObfuscationHelper.class.getResourceAsStream("/assets/gud_lib/notch-mcp.srg")))){
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] split = line.split(": ", 2);
                if(split[0].equals("MD")){
                    split = split[1].split(" ");
                    if(name.equals(split[2]) && description.equals(split[3])){
                        name = split[0];
                        description = split[1];
                        break;
                    }
                }
            }
        }catch (IOException e){
            throw new RuntimeException("Error reading mappings!", e);
        }

        name = name.replaceAll("/", ".");
        name = name.substring(name.lastIndexOf('.') + 1);
        if(!methodNameCache.containsKey(methodName)) {
            methodNameCache.put(methodName, name);
        }
        if(!methodDescriptionCache.containsKey(methodDescription)) {
            methodDescriptionCache.put(methodDescription, description.replaceAll("/", "."));
        }
    }

    @Nonnull
    public static String getObfuscatedMethodName(@Nonnull String methodName, @Nonnull String methodDescription) {
        if(methodNameCache.containsKey(methodName)){
            return methodNameCache.get(methodName);
        }
        cacheMethod(methodName, methodDescription);
        if(methodNameCache.containsKey(methodName)) {
            return methodNameCache.get(methodName);
        }else{
            return methodName.substring(methodName.lastIndexOf('.') + 1);
        }
    }

    @Nonnull
    public static String getObfuscatedMethodDescription(@Nonnull String methodName, @Nonnull String methodDescription) {
        if(methodDescriptionCache.containsKey(methodDescription)){
            return methodDescriptionCache.get(methodDescription);
        }
        cacheMethod(methodName, methodDescription);
        return methodDescriptionCache.getOrDefault(methodDescription, methodDescription);
    }

    @Nonnull
    public static String getObfuscatedClassNameForASM(@Nonnull String className) {
        return getObfuscatedClassName(className).replaceAll("\\.", "/");
    }

    @Nonnull
    public static String getObfuscatedMethodNameForASM(@Nonnull String methodName, @Nonnull String methodDescription) {
        return getObfuscatedMethodName(methodName, methodDescription);
    }

    @Nonnull
    public static String getObfuscatedMethodDescriptionForASM(@Nonnull String methodName, @Nonnull String methodDescription) {
        return getObfuscatedMethodDescription(methodName, methodDescription).replaceAll("\\.", "/");
    }

}
