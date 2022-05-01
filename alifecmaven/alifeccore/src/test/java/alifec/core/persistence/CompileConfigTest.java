package alifec.core.persistence;

import alifec.core.contest.ParentTest;
import alifec.core.exception.CompileConfigException;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.config.CompileConfig;
import alifec.core.persistence.config.ContestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Sergio Del Castillo on 17/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompileConfigTest extends ParentTest {
    private String LINUX_ORACLE_COMPILATION_LINE = "g++ -o \"%s/libcppcolonies.so\" -fPIC -Wall -shared -lm -I\"%s\" -I\"%s\" -I\"%s\" -I\"%s\" \"%s/lib_CppColony.cpp\"";
    private String LINUX_OPENJDK_COMPILATION_LINE = "g++ -o \"%s/libcppcolonies.so\" -fPIC -Wall -shared -lm -I\"%s\" -I\"%s\" -I\"%s\" -I\"%s\" \"%s/lib_CppColony.cpp\"";
    private String WINDOWS_ORACLE_COMPILATION_LINE = "g++ -o \"%s\\libcppcolonies.dll\" -Wl,--add-stdcall-alias -Wall -shared -lm -I\"%s\" -I\"%s\" -I\"%s\" -I\"%s\" \"%s\\lib_CppColony.cpp\"";

    @Test
    public void testConfig() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException, CompileConfigException {
        ContestConfig config = createContest("Contest-01");

        //it will ensure that the config is valid.
        CompileConfig compileConfig = new CompileConfig(config);

        Assertions.assertEquals(LINUX_OPENJDK_COMPILATION_LINE, compileConfig.getLinuxOpenJdk());
        Assertions.assertEquals(LINUX_ORACLE_COMPILATION_LINE, compileConfig.getLinuxOracle());
        Assertions.assertEquals(WINDOWS_ORACLE_COMPILATION_LINE, compileConfig.getWindowsOracle());

    }
}
