package alifec.core.contest;

import alifec.core.contest.Battle;
import alifec.core.event.EventBus;
import alifec.core.exception.BattleException;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Microorganism;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * Created by Sergio Del Castillo on 07/08/17.
 * Parent test was set to this package to avoid setting a new module-info.java file.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ParentTest {

    public static String ROOT_PATH = "./target";

    public static String TEST_ROOT_PATH = ROOT_PATH + File.separator + "alifectests";

    private ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

    @Before
    public void init() throws IOException {
        EventBus.setSingleThread();
        EventBus.reset();

        Path rootDir = Paths.get(TEST_ROOT_PATH);

        //ensure that the root dir exists
        if (Files.notExists(rootDir)) {
            Files.createDirectories(rootDir);
        }
    }

    @After
    public void cleanup() throws IOException {
        Path rootDir = Paths.get(TEST_ROOT_PATH);

        if (Files.exists(rootDir)) {
            Path dirPath = Paths.get(TEST_ROOT_PATH);

            Files.walk(dirPath, FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    /*.peek(System.out::println)*/
                    .forEach(File::delete);


            //Files.walk - return all files/directories below rootPath including
            //.sorted - sort the list in reverse order, so the directory itself comes
            //          after the including subdirectories and files
            //.map - map the Path to File
            //.peek - is there only to show which entry is processed
            //.forEach - calls the .deleteFromBattlesFile() method on every File object
        }
    }

    protected ContestConfig createContest(String name) throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        ContestConfig config = new ContestConfig(bundle, TEST_ROOT_PATH, name);

        createContest(config);

        Path basePath = Paths.get(config.getBaseAppFolder());

        if (Files.notExists(basePath))
            Files.createDirectory(basePath);

        config.save();
        return config;
    }

    protected void createContest(ContestConfig config) throws IOException, CreateContestFolderException, URISyntaxException {
        Path cppResources = Paths.get(getClass().getResource("/app/cpp/").toURI());
        Path examplesResources = Paths.get(getClass().getResource("/app/examples/").toURI());

        ContestFileManager.buildNewContestFolder(config, true, cppResources, examplesResources);

        //the app folder can exists if this method was already called.
        // The app folder is not contest folder dependent so it have to be created only the first time.
        //create the app folder
        Path app = Paths.get(config.getBaseAppFolder());
        if (Files.notExists(app))
            Files.createDirectory(app);

        //create the file compiler.properties
        Path compilerProperties = Paths.get(config.getCompilerConfigFile());
        if (Files.notExists(compilerProperties)) {
            Path compilerConfigFile = Paths.get(getClass().getResource("/app/compiler.properties").toURI());
            Files.copy(compilerConfigFile, compilerProperties);
        }
    }

    protected Battle createBattle(Environment env, int colony1, int colony2, int nutrientId, String nutrientName) throws BattleException {
        Colony c1 = env.getColonyById(colony1);
        Colony c2 = env.getColonyById(colony2);

        Battle battle = new Battle(c1.getId(), c2.getId(), nutrientId, c1.getName(), c2.getName(), nutrientName);
        env.createBattle(battle);

        return battle;
    }

    public List<Battle> battleDataSet() throws BattleException {
        List<Battle> list = new ArrayList<>();

        Battle battle1 = new Battle(1, 2, 1, "col1", "col2", "Famine");
        battle1.setWinner(1, 100f);
        Battle battle2 = new Battle(1, 3, 1, "col1", "col3", "Famine");
        battle2.setWinner(3, 140f);
        Battle battle3 = new Battle(1, 4, 1, "col1", "col4", "Famine");
        battle3.setWinner(1, 130f);
        Battle battle4 = new Battle(1, 2, 2, "col1", "col2", "Balls");
        battle4.setWinner(2, 200f);
        Battle battle5 = new Battle(1, 3, 2, "col1", "col3", "Balls");
        battle5.setWinner(1, 10f);
        Battle battle6 = new Battle(1, 4, 2, "col1", "col4", "Balls");
        battle6.setWinner(4, 1000f);
        Battle battle7 = new Battle(2, 3, 1, "col2", "col3", "Famine");
        battle7.setWinner(2, 2000f);
        Battle battle8 = new Battle(2, 4, 1, "col2", "col4", "Famine");
        battle8.setWinner(4, 500f);
        Battle battle9 = new Battle(3, 4, 1, "col3", "col4", "Famine");
        battle9.setWinner(3, 750f);
        Battle battle10 = new Battle(1, 4, 2, "col1", "col4", "Balls");
        battle10.setWinner(4, 1000f);

        list.add(battle1);
        list.add(battle2);
        list.add(battle3);
        list.add(battle4);
        list.add(battle5);
        list.add(battle6);
        list.add(battle7);
        list.add(battle8);
        list.add(battle9);
        list.add(battle10);

        return list;
    }

 //   @Test
    public void test() throws MalformedURLException, ClassNotFoundException {
      //  URL url= new URL("file://");
        //URLClassLoader cl = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
        //Constructor<Microorganism> constructor = (Constructor<Microorganism>) cl.loadClass("MOs.Tactica2").getConstructors()[0];

     /*   Class loadedClass = cl.loadClass("com.xyz.MyClass");
        MyClass obj = (MyClass) loadedClass.newInstance();
        obj.doSomething();*/


/*        try{

            File file = new File("/home/yeyo/work/alifec_new/alifec/alifecmaven/alifeccore/target/alifectests/data/Contest-01/compiled/");

            //load this folder into Class loader
            ClassLoader cl = new URLClassLoader(new URL[]{file.toURI().toURL()});

            //load the Address class in 'c:\\other_classes\\'
            Class  cls = cl.loadClass("MOs.Tactica2");

            //print the location from where this class was loaded
            ProtectionDomain pDomain = cls.getProtectionDomain();
            CodeSource cSource = pDomain.getCodeSource();
            URL urlfrom = cSource.getLocation();
            System.out.println(urlfrom.getFile());

        }catch(Exception ex){
            ex.printStackTrace();
        }*/
    }
}
