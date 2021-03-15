package alifec.core.persistence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sergio Del Castillo on 06/07/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeCFileManager {
    /**
     * Build Artificial life contest folder structure if it does not exists.
     * Todo: it is a temporary solution until the distribution module is completed.
     *
     * @param path
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void build(Path path) throws IOException, URISyntaxException {
        Path app = ensureDirectory(path, "app");

        ensureFile(app, "compiler.properties", "compiler/compiler.properties");

        ensureFile(app, "log4j2.xml", "log/log4j2.xml");

        ensureDirectory(path, "data");
    }

    private static Path ensureFile(Path basePath, String fileName, String fileInternalLocation) throws IOException, URISyntaxException {
        Path file = basePath.resolve(fileName);

        if (JarFileManager.isLoadedFromJar()) {
            JarFileManager.createFileFromJar(fileInternalLocation, basePath.toString());
        } else {
            if (!Files.exists(file)) {
                Files.copy(Paths.get(ALifeCFileManager.class.getResource("/" + fileInternalLocation).toURI()), file);
            }
        }
        return file;
    }

    private static Path ensureDirectory(Path basePath, String folder) throws IOException {
        Path dir = basePath.resolve(folder);

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir;
    }
}
