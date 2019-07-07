package alifec.core.persistence;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergio Del Castillo on 07/07/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class JarFileManager {

    static boolean createFolderFromJar(String source, String targetFolder) {
        //todo: imporove the error handling and logger!!
        FileSystem filesystem = null;
        try {
            File target = new File(targetFolder);
            System.out.println("target folder: " + target.getAbsolutePath());

            URI uri = JarFileManager.class.getResource("/" + source).toURI();
            System.out.println("uri: " + uri.toString());
            //if (uri.toString().startsWith("jar:")) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            filesystem = FileSystems.newFileSystem(uri, env);
            //}

            Path imagesOrg = Paths.get(uri);
            System.out.println("path to iterate: " + imagesOrg);


            if (!target.exists()) {
                target.mkdirs();
            }

            System.out.println("before iterate");
            if (Files.isRegularFile(imagesOrg)) {
                String targetPath = target.getAbsolutePath() + File.separator + imagesOrg.getFileName().toString();
                Files.copy(imagesOrg, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            } else {
                try (DirectoryStream<Path> paths = Files.newDirectoryStream(imagesOrg)) {
                    System.out.println("iterating");
                    for (final Path child : paths) {
                        System.out.println(child);
                        try {
                            String targetPath = target.getAbsolutePath() + File.separator + child.getFileName().toString();
                            System.out.println(targetPath);
                            Files.copy(child, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            System.out.println("faileing");
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;

        } catch (URISyntaxException e) {
            System.out.println("exception 1");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("exception 2");
            e.printStackTrace();
        } finally {
            try {
                if (filesystem != null) {
                    filesystem.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    static boolean createFileFromJar(String source, String targetFolder) {
        //todo: improve the error handler and logger!!
        FileSystem filesystem = null;
        try {
            File target = new File(targetFolder);
            System.out.println("target folder: " + target.getAbsolutePath());
            System.out.println("source: " + source);

            URI uri = JarFileManager.class.getResource("/" + source).toURI();
            System.out.println("uri: " + uri.toString());
            //if (uri.toString().startsWith("jar:")) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            filesystem = FileSystems.newFileSystem(uri, env);
            //}

            Path imagesOrg = Paths.get(uri);
            System.out.println("path to iterate: " + imagesOrg);


            if (!target.exists()) {
                target.mkdirs();
            }

            System.out.println("before iterate");

            String targetPath = target.getAbsolutePath() + File.separator + imagesOrg.getFileName().toString();
            Files.copy(imagesOrg, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);

            return true;

        } catch (URISyntaxException e) {
            System.out.println("exception 1");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("exception 2");
            e.printStackTrace();
        } finally {
            try {
                if (filesystem != null) {
                    filesystem.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return false;
    }



    static boolean isLoadedFromJar() {
        return ContestFileManager.class.getResource("JarFileManager.class").toString().startsWith("jar");
    }

}
