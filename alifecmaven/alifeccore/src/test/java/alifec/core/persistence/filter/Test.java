package alifec.core.persistence.filter;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Test {

    @org.junit.Test
    public void test() throws IOException {
        String s1 = new File(System.getProperty("user.dir") + "../").getCanonicalPath();

        Path s2 = Paths.get(System.getProperty("user.dir"));

    }
}
