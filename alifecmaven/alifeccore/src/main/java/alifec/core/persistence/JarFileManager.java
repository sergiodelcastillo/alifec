package alifec.core.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergio Del Castillo on 07/07/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class JarFileManager {

    private static Logger logger = LogManager.getLogger(JarFileManager.class);

    static boolean createFolderFromJar(String source, String targetFolder) {

        try {

            Path targetPath = Path.of(targetFolder);
            makeDir(targetPath);

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = JarFileManager.class.getResource("/" + source).toURI();
            try (FileSystem filesystem = FileSystems.newFileSystem(uri, env)) {
                Path sourcePath = Paths.get(uri);
                if (Files.isRegularFile(sourcePath)) {
                    copyFile(targetPath, sourcePath);
                } else {
                    try (DirectoryStream<Path> paths = Files.newDirectoryStream(sourcePath)) {
                        logger.debug("Iterating over {}", uri);
                        for (final Path child : paths) {
                            copyFile(targetPath, child);
                        }
                    }
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("Error creating folder from jar", e);
        }

        return false;
    }

    static boolean createFileFromJar(String source, String targetFolder) {

        try {
            Path targetPath = Path.of(targetFolder);
            makeDir(targetPath);

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = JarFileManager.class.getResource("/" + source).toURI();
            try (FileSystem filesystem = FileSystems.newFileSystem(uri, env)) {
                Path sourcePath = Paths.get(uri);
                copyFile(targetPath, sourcePath);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error creating folder from jar", e);
        }

        return false;
    }

    private static void makeDir(Path targetPath) {
        File targetFile = targetPath.toFile();
        if (!targetFile.exists()) {
            logger.info("Creating folder: {}", targetFile.getAbsolutePath());
            targetFile.mkdirs();
        }
    }

    private static void copyFile(Path targetPath, Path sourcePath) throws IOException {
        Path targetFilePath = targetPath.resolve(sourcePath.getFileName().toString());
        logger.debug("Copying {}", targetFilePath);
        Files.copy(sourcePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    static boolean isLoadedFromJar() {
        return ContestFileManager.class.getResource("JarFileManager.class").toString().startsWith("jar");
    }

}
