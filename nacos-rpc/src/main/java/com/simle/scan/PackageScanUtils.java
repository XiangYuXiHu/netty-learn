package com.simle.scan;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Description
 * @ClassName PackageScanUtils
 * @Author smile
 * @date 2022.04.01 17:57
 */
public class PackageScanUtils {

    /**
     * 获取main方法
     *
     * @return
     */
    public static String getStackTrace() {
        StackTraceElement[] trace = new Throwable().getStackTrace();
        return trace[trace.length - 1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                /**
                 * 协议名称
                 */
                String protocol = url.getProtocol();
                /**
                 * 以文件的形式保存到服务器
                 */
                if ("file".equalsIgnoreCase(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    getClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equalsIgnoreCase(protocol)) {
                    JarFile jar;
                    jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String name = jarEntry.getName();
                        if (name.charAt(0) == '/') {
                            name = name.substring(1);
                        }
                        if (name.startsWith(packageDirName)) {
                            int idx = name.lastIndexOf('/');
                            if (idx != -1) {
                                packageName = name.substring(0, idx).replace('/', '.');
                            }
                            if ((idx != -1) || recursive) {
                                if (name.endsWith(".class") && !jarEntry.isDirectory()) {
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    classes.add(Class.forName(packageName + '.' + className));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void getClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        /**
         * 获取包的目录建立一个file
         */
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        /**
         * 存在获取包下所有文件（含目录）
         */
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                getClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                /**
                 * java类文件，去掉后面的.class文件，只留类名
                 */
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
