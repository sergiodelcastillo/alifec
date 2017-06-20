package view.controller;

import java.io.FileWriter;
import java.io.IOException;

public class ALifeCJFXController {

    /**
     * It loads the configuration from the current directory
     * @return if the configuration was loaded
     */
    public static boolean loadConfiguration(){
        try {
            System.out.println(System.getProperty("user.dir"));
            new FileWriter("test").write("aver");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
