package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.exception.CompileConfigException;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Sergio Del Castillo on 17/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompileConfigTest extends ParentTest{

    @Test
    public void testConfig() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException, CompileConfigException {
        ContestConfig config = createContest("Contest-01");

        CompileConfig compileConfig = new CompileConfig(config);

        //TOdo..

    }
}
