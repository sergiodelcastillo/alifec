package alifec.core.persistence;

import alifec.core.exception.ZipParsingException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.custom.FileNameFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Sergio Del Castillo on 19/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ZipFileManager {
    private final static Logger logger = LogManager.getLogger(ZipFileManager.class);
    private static int BUFFER = 2048;
    private final ContestConfig config;

    public ZipFileManager(ContestConfig config) {
        //todo: remove the config and use string
        //todo improve use of path
        this.config = config;
    }

    /**
     * This method creates the zip archive and then goes through
     * each file in the chosen directory, adding each one to the
     * archive. Note the use of the try with resource to avoid
     * any finally blocks.
     * <p>
     * The zip file will be generated in backup folder of the contest
     */
    public String zipContest() throws IOException {
        // the directory to be zipped
        String backupFile = config.getBackupFile();
        Path directory = Paths.get(config.getContestPath());
        String canonicalPathBackup = Paths.get(config.getBackupFolder()).toFile().getCanonicalPath();
        String canonicalPath = directory.getParent().toFile().getCanonicalPath() + File.separator;
        // the zip file name that we will create
        File zipFile = Paths.get(backupFile).toFile();

        // open the zip stream in a try resource block, no finally needed
        try (ZipOutputStream zipStream = new ZipOutputStream(
                new FileOutputStream(zipFile))) {

            // traverse every file in the selected directory and add them
            // to the zip file by calling addToZipFile(..)
            Files.walk(directory)
                    .sorted(Comparator.naturalOrder())
                    .filter(path -> {
                        try {
                            if (path.toFile().getCanonicalPath().contains(canonicalPathBackup)) {
                                logger.trace("Ignoring file: " + path.toFile().toString());
                                return false;
                            }
                            return true;
                        } catch (IOException e) {
                            logger.info(e.getMessage(), e);
                            return false;
                        }

                    })
                    .forEach(path -> addToZipFile(path, zipStream, canonicalPath));

            logger.info("Zip file created in " + directory.toFile().getPath());
        } catch (IOException | ZipParsingException e) {
            logger.error("Error while zipping.", e);
            throw e;
        }


        return getName(backupFile);
    }

    private String getName(String backupFile) {
        String[] tmp1 = backupFile.split(File.separator);

        return tmp1.length == 1 ? backupFile : tmp1[tmp1.length - 1];
    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment.
     *
     * @param file      file to be archived
     * @param zipStream archive to contain the file.
     */
    private void addToZipFile(Path file, ZipOutputStream zipStream, String root) {
        //Ignore folders
        if (file.toFile().isDirectory()) {
            logger.trace("Ignoring: " + file.toAbsolutePath());
            return;
        }
        //ContestConfig.getBackupPath(root, contestName);

        String inputFileName = file.toFile().getPath();
        try (FileInputStream inputStream = new FileInputStream(inputFileName)) {

            // create a new ZipEntry, which is basically another file
            // within the archive. We omit the path from the filename
            ZipEntry entry = new ZipEntry(file.toFile().getCanonicalPath()
                    .replace("\\", "/")
                    .replaceFirst(root, ""));
            entry.setCreationTime(FileTime.fromMillis(file.toFile().lastModified()));

            //entry.setComment("Created by Alifec");
            zipStream.putNextEntry(entry);

            logger.info("Generated new entry for: " + inputFileName);
            // Now we copy the existing file into the zip archive. To do
            // this we write into the zip stream, the call to putNextEntry
            // above prepared the stream, we now write the bytes for this
            // entry. For another source such as an in memory array, you'd
            // just change where you read the information from.

            byte[] readBuffer = new byte[BUFFER];
            int amountRead;
            int written = 0;

            while ((amountRead = inputStream.read(readBuffer)) > 0) {
                zipStream.write(readBuffer, 0, amountRead);
                written += amountRead;
            }

            logger.info("Stored " + written + " bytes to " + inputFileName);

        } catch (IOException e) {
            throw new ZipParsingException("Unable to process " + inputFileName, e);
        }
    }

    public void unzip(String source, String out) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {

            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {

                File file = new File(out, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();

                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                        byte[] buffer = new byte[BUFFER];

                        int location;

                        while ((location = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, location);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
        }
    }

    /**
     * Generate a list of files with the content of the zip file.
     *
     * @param zip the zip file
     * @return a list of string which represents the content of the zip
     * @throws IOException
     */
    public List<String> listEntries(String zip) throws IOException {
        try (ZipFile zipFile = new ZipFile(zip)) {
            return zipFile.stream()
                    .map(new FileNameFunction())
                    .collect(Collectors.toList());
        }
    }
}
