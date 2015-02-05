
package com.mohaine.util;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;


public class JarDeploy {
    private static final int MAX_OLD_DIRS_TO_KEEP = 1;
    private static List loadedJars = null;

    public static List getBundleJars() {
        if (loadedJars != null) {
            return Collections.unmodifiableList(loadedJars);
        }
        return null;
    }

    public static void main(String[] args) {
        try {

            List jarsList = new ArrayList();

            URL url = JarDeploy.class.getResource("");
            JarFile bundleJar = ((JarURLConnection) url.openConnection()).getJarFile();

            Manifest manifest = bundleJar.getManifest();

            Attributes mainAttributes = manifest.getMainAttributes();
            String bundleClassPath = mainAttributes.getValue("Bundle-ClassPath");
            if (bundleClassPath == null) {
                bundleClassPath = "lib/";
            }

            String localClassPath = mainAttributes.getValue("Local-ClassPath");

            File bundleFile = new File(bundleJar.getName());

            List localJars = getLocalJars(localClassPath, bundleFile);

            jarsList.addAll(localJars);


            List bundleJars = extractJars(bundleJar, bundleClassPath);
            jarsList.addAll(bundleJars);

            String realMain = mainAttributes.getValue("Bundle-Main-Class");
            if (realMain == null && args.length > 0) {
                realMain = args[0];
                String[] remaining = new String[args.length - 1];
                System.arraycopy(args, 1, remaining, 0, args.length - 1);
                args = remaining;
            }

            if (realMain == null) {
                System.err.println("Failed to find Bundle-Main-Class.  Please provide as first argument or in the Bundle-Main-Class manifest entry");
                System.exit(-1);
            }

            for (Iterator iter = jarsList.iterator(); iter.hasNext(); ) {
                File file = (File) iter.next();
                addFileToClassPath(file);
            }
            jarsList.add(new File(bundleJar.getName()));
            loadedJars = jarsList;
            Class loadClass = ClassLoader.getSystemClassLoader().loadClass(realMain);

            Method main = loadClass.getMethod("main", new Class[]{args.getClass()});
            if (main == null) {
                throw new Exception("Could not find main in class: " + realMain);
            }
            if (!Modifier.isStatic(main.getModifiers())) {
                throw new Exception("Main is not static in class: " + realMain);
            }

            main.invoke(null, new Object[]{args});

        } catch (InvocationTargetException te) {
            Logger.getLogger("JarDeploy").log(Level.SEVERE, "Unexpected Exception has occurred", te.getTargetException());
        } catch (Exception e) {
            Logger.getLogger("JarDeploy").log(Level.SEVERE, "Unexpected Exception has occurred", e);
        }
    }

    private static List getLocalJars(String localClassPath, File bundleFile) throws IOException {
        List localJars = new ArrayList();
        if (localClassPath != null) {
            File localDir = getBaseDir().getParentFile();
            StringTokenizer st = new StringTokenizer(localClassPath, ",");
            while (st.hasMoreTokens()) {
                String jarName = st.nextToken().trim();
                File jarFile = new File(localDir, jarName);
                if (jarFile.exists()) {
                    localJars.add(jarFile);
                    if (jarFile.isDirectory()) {
                        File[] files = jarFile.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            File file = files[i];
                            localJars.add(file);
                        }
                    }
                }
            }
        }
        return localJars;
    }

    private static List extractJars(JarFile bundleJar, String bundleClassPath) throws IOException, FileNotFoundException, Exception {

        File bundleFile = new File(bundleJar.getName());
        File extractionDir = getExtractionDir(bundleFile);

        cleanupOld(bundleFile, extractionDir);

        List bundleJars = new ArrayList();
        StringTokenizer st = new StringTokenizer(bundleClassPath, ",");
        while (st.hasMoreTokens()) {
            String jarName = st.nextToken().trim();
            ZipEntry foundEntry = bundleJar.getEntry(jarName);
            if (foundEntry != null) {
                if (foundEntry.isDirectory()) {
                    // System.out.println("Found jar dir: " + foundEntry.getName());
                    File dirToExtract = getExtactedName(foundEntry, extractionDir);
                    bundleJars.add(dirToExtract);
                    String dirName = foundEntry.getName();
                    Enumeration entries = bundleJar.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) entries.nextElement();
                        if (!entry.isDirectory()) {
                            String name = entry.getName();
                            if (!name.equals(dirName) && name.startsWith(dirName)) {
                                File extractedJarFile = getExtactedName(entry, extractionDir);
                                if (extractedJarFile.getParentFile().equals(dirToExtract)) {
                                    addJar(bundleJar, extractedJarFile, bundleJars, entry);
                                }
                            }
                        }
                    }
                } else {
                    // System.out.println("Found jar file: " + foundEntry.getName());
                    File extractedJarFile = getExtactedName(foundEntry, extractionDir);
                    addJar(bundleJar, extractedJarFile, bundleJars, foundEntry);
                }
            } else {
                System.err.println("Could find jar entry " + jarName);
            }
        }
        return bundleJars;
    }

    private static void cleanupOld(File bundleFile, File extractionDir) {

        if (extractionDir.exists() && extractionDir.lastModified() < bundleFile.lastModified()) {
            deleteDir(extractionDir);
        }

        File baseDir = getBaseDir();
        File[] dirs = baseDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (dirs != null && dirs.length > MAX_OLD_DIRS_TO_KEEP) {
            Arrays.sort(dirs, new Comparator() {
                public int compare(Object o1, Object o2) {
                    File file1 = (File) o1;
                    File file2 = (File) o2;
                    return (int) (file2.lastModified() - file1.lastModified());
                }
            });

            for (int i = MAX_OLD_DIRS_TO_KEEP; i < dirs.length; i++) {
                File file = dirs[i];
                deleteDir(file);
            }
        }

    }

    private static void addJar(JarFile bundleJar, File extractedJarFile, List bundleJars, ZipEntry entry) throws IOException, NoSuchAlgorithmException {

        if (!extractedJarFile.exists() || entry.getSize() != extractedJarFile.length()) {
            writeJarEntry(bundleJar, entry, extractedJarFile);
        }

        String name = extractedJarFile.getName().toLowerCase();
        if (name.endsWith(".dll") || name.endsWith(".exe")) {
            File libName = new File(".", extractedJarFile.getName());

            boolean writeFile = !libName.exists() || entry.getSize() != libName.length();
            if (!writeFile) {
                // Check the file contents
                String existingFileSha1 = getSHA1(libName);
                InputStream inputStream = bundleJar.getInputStream(entry);
                try {
                    String jarFileSha1 = getSHA1(inputStream);
                    if (!jarFileSha1.equals(existingFileSha1)) {
                        writeFile = true;
                    }
                } finally {
                    inputStream.close();
                }
            }

            if (writeFile) {
                writeJarEntry(bundleJar, entry, libName);
            }
        }

        bundleJars.add(extractedJarFile);
    }

    private static void deleteDir(File extractionDir) {
        File[] listFiles = extractionDir.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            File file = listFiles[i];
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
        extractionDir.delete();
    }

    private static File getExtractionDir(File bundleFile) throws Exception {
        File file = new File(getBaseDir(), getSHA1(bundleFile));
        return file;
    }

    private static File getBaseDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "JarDeployCache-" + System.getProperty("user.name"));
        return baseDir;
    }

    private static void writeJarEntry(JarFile jar, ZipEntry foundEntry, OutputStream outputStream) throws IOException {
        InputStream inputStream = jar.getInputStream(foundEntry);
        byte[] buffer = new byte[5000];
        while (true) {
            int read = inputStream.read(buffer);
            if (read < 0) {
                break;
            } else if (read > 0) {
                outputStream.write(buffer, 0, read);
            }
        }
    }

    /**
     * @param name
     * @param jar
     * @param OutputStream
     * @throws java.io.IOException
     */
    private static File writeJarEntry(JarFile jar, ZipEntry foundEntry, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            writeJarEntry(jar, foundEntry, fos);
        } finally {
            fos.close();
        }
        return file;
    }

    private static File getExtactedName(ZipEntry foundEntry, File extractionDir) {
        File file = new File(extractionDir, foundEntry.getName());
        File parentDir = file.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        return file;
    }

    public static void addFileToClassPath(File file) throws IOException {
        addUrlToClassPath(file.toURI().toURL());
    }

    private static void addUrlToClassPath(URL toURL) throws IOException {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            method.invoke(systemClassLoader, new Object[]{toURL});
        } catch (Throwable t) {
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    private final static String getSHA1(File file) throws NoSuchAlgorithmException, IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return getSHA1(fis);
        } finally {
            fis.close();
        }
    }

    private static String getSHA1(InputStream is) throws NoSuchAlgorithmException, IOException {
        return getDigest(is, "SHA1");
    }


    private static String getDigest(InputStream fis, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[5000];
        while (true) {
            int read = fis.read(buffer);
            if (read > 0) {
                digest.update(buffer, 0, read);
            } else if (read < 0) {
                break;
            }
        }
        byte[] hash = digest.digest();
        StringBuffer hashStr = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            byte b = hash[i];
            hashStr.append(Integer.toHexString(0xFF & b));
        }
        return hashStr.toString();
    }
}
