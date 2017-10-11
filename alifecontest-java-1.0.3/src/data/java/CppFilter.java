package data.java;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 16, 2010
 * Time: 2:10:16 PM
 */
public class CppFilter extends AbstractFilter {
    /**
     * is a comment or a non-printable character.
     * i.e:
     * /*hello world*\/
     * // a comment \n
     * \t
     */
    private final static String empty = "[(/\\*[.]*\\*/) \n\t(//.*\n)]";


    /**
     * all characters before the colony name
     */
    private final static String clazz = "\\A(.*class" + empty + "+)";

    /**
     * The name of the colony
     */
    private final static String name = "([\\w_-]+)";
    /**
     * The character of the c/c++ code after the colony name
     */
    private final static String inherit = "(" + empty + "*:" + empty + "*public" + empty + "+Microorganism.+)\\z";

    /**
     * the complete pattern
     */
    public final static String pattern = clazz + name + inherit;


    private static Pattern validator = Pattern.compile(pattern, Pattern.DOTALL);


    public CppFilter() {
    }

    @Override
    public boolean valid(File dir, String name) {
        if (!dir.getName().equals(Config.MOS_FOLDER)) {
            //invalid dir
            return false;
        }

        if (!name.endsWith(".h")) {
            // the file must end with .h
            return false;
        }

        try {
            File file = new File(dir + File.separator + name);
            FileReader reader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = buffer.readLine()) != null) {
                builder.append(line);
            }
            boolean status = validator.matcher(builder.toString()).find();

            reader.close();
            buffer.close();

            return status;
        } catch (IOException e) {
            return false;
        }

    }

    public static String getColonyName( String name) {
        try {
            File file = new File(Config.getInstance().getAbsoluteMOsFolder() + File.separator + name);
            FileReader reader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = buffer.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            buffer.close();
            
            Matcher matcher = validator.matcher(builder.toString());

            boolean status = matcher.find();

            if (!status) {
                return null;
            }

            return matcher.group(2);
        } catch (IOException e) {
            return null;
        }

    }


}
