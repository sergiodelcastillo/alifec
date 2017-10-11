package view;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 11, 2010
 * Time: 11:47:34 PM
 * Email: yeyo@druidalabs.com
 */
public class Properties {
    private static Properties instance;

    private java.util.Properties property = new java.util.Properties();

    public static Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
        }
        return instance;
    }

    private Properties() {
        try {
            property.load(new FileReader("src/view/Properties.properties"));
        } catch (IOException ignored) {
        }
    }

    public String getSaveIcon() {
        return property.getProperty("icons.save");
    }

    public String getDelAllBattleIcon() {
        return property.getProperty("icons.delAllBattles");
    }

    /*  public String getAddAllBattleIcon() {
 return property.getProperty("icons.addAllBattles");
}       */

    public String getRunSelectedBattleIcon() {
        return property.getProperty("icons.runSelectedBattle");
    }

    public String getRunAllBattleIcon() {
        return property.getProperty("icons.runAllBattle");
    }

    public String getAddSelectedBattleIcon() {
        return property.getProperty("icons.addBattles");
    }

    public String getDelSelectedBattleIcon() {
        return property.getProperty("icons.delSelectedBattle");
    }

    public String getVersionValue() {
        return property.getProperty("version.value");
    }

    public String getAboutIcon() {
        return property.getProperty("icons.about");
    }

    public String getPrevTournamentIcon() {
        return property.getProperty("icons.prev.tournament");
    }

    public String getNextTournamentIcon() {
        return property.getProperty("icons.next.tournament");
    }

    public String getRankingIcon() {
        return property.getProperty("icons.ranking");
    }

    public String getAddTournamentIcon() {
        return property.getProperty("icons.add.tournament");
    }

    public String getDelTournamentIcon() {
        return property.getProperty("icons.del.tournament");
    }
}