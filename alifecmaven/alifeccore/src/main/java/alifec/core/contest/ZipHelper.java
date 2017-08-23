package alifec.core.contest;

import alifec.core.exception.ZipParsingException;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Sergio Del Castillo on 19/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ZipHelper {
    private final static Logger logger = Logger.getLogger(ZipHelper.class);

    /**
     * This method creates the zip archive and then goes through
     * each file in the chosen directory, adding each one to the
     * archive. Note the use of the try with resource to avoid
     * any finally blocks.
     */
    //public static void createZip(String zipFileName, String dirName) throws IOException {
    public static void createZip(ContestConfig config) throws IOException {
        // the directory to be zipped
        Path directory = Paths.get(config.getContestPath());
        String canonicalPathBackup = Paths.get(config.getBackupFolder()).toFile().getCanonicalPath();
        String canonicalPath = directory.getParent().toFile().getCanonicalPath() + File.separator;
        // the zip file name that we will create
        File zipFile = Paths.get(config.getBackupFile()).toFile();

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
        }
    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment.
     *
     * @param file      file to be archived
     * @param zipStream archive to contain the file.
     */
    private static void addToZipFile(Path file, ZipOutputStream zipStream, String root) {
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
            ZipEntry entry = new ZipEntry(file.toFile().getCanonicalPath().replaceFirst(root, ""));
            entry.setCreationTime(FileTime.fromMillis(file.toFile().lastModified()));

            //entry.setComment("Created by Alifec");
            zipStream.putNextEntry(entry);

            logger.info("Generated new entry for: " + inputFileName);

            // Now we copy the existing file into the zip archive. To do
            // this we write into the zip stream, the call to putNextEntry
            // above prepared the stream, we now write the bytes for this
            // entry. For another source such as an in memory array, you'd
            // just change where you read the information from.
            byte[] readBuffer = new byte[2048];
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


}
