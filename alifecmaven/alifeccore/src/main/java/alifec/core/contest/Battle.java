package alifec.core.contest;

import java.io.*;


/**
 * @author Sergio Del Castillo
 * mail@: sergio.jose.delcastillo@gmail.com

 * Contains de history of battles.
 */
public class Battle {
    private float energy_1;
    private float energy_2;
    private String name_1;
    private String name_2;
    private String nutrient;

    public Battle(String n1, String n2, String nut, float ene1, float ene2) {
        name_1 = n1;
        name_2 = n2;
        nutrient = nut;
        energy_1 = ene1;
        energy_2 = ene2;
    }

    public Battle(String line) {
        if (line == null || line.isEmpty())
            throw new IllegalArgumentException("The line ("+ line+") is empty");

        String[] tmp = line.split(",");

        if (tmp.length != 5)
            throw new IllegalArgumentException("The line (" + line + ") should have 5 columns");

        name_1 = tmp[0];
        name_2 = tmp[1];
        nutrient = tmp[2];
        energy_1 = Float.parseFloat(tmp[3]);
        energy_2 = Float.parseFloat(tmp[4]);
    }


    /**
     * @return name of first colony
     */
    public String getFirstColony() {
        return name_1;
    }

    /**
     * @return name of second colony
     */
    public String getSecondColony() {
        return name_2;
    }

    /**
     * @return name of nutrient
     */
    public String getNutrient() {
        return nutrient;
    }

    public Float getWinnerEnergy() {
        return (energy_1 > 0) ? new Float(energy_1) : new Float(energy_2);
    }

    public String getWinnerName() {
        return (energy_1 > 0) ? name_1 : name_2;
    }

    public void save(String path) throws IOException {
        if (path == null) return;

        FileWriter f = new FileWriter(path, true);
        PrintWriter pw = new PrintWriter(f);
        pw.append(this.toString());
        pw.append("\n");
        pw.close();
        f.close();
    }

    /**
     * Delete this battle from battles.csv file
     * @param path of battle to delete
     * @return true if is successfully
     * @throws IOException
     */
    public boolean delete(String path) throws IOException {
        if (!new File(path).renameTo(new File(path + "_backup")))
            return false;

        String thisLine = this.toString(), line;

        BufferedReader br = new BufferedReader(new FileReader(path + "_backup"));
        PrintWriter pw = new PrintWriter(path);

        while ((line = br.readLine()) != null) {
            if (!line.equalsIgnoreCase("") && !line.equalsIgnoreCase(thisLine))
                pw.println(line);
        }

        pw.close();
        br.close();
        new File(path + "_backup").delete();
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Battle) {
            Battle b = (Battle) o;
            return name_1.equalsIgnoreCase(b.name_1) &&
                    name_2.equalsIgnoreCase(b.name_2) &&
                    nutrient.equalsIgnoreCase(b.nutrient) &&
                    energy_1 == b.energy_1 &&
                    energy_2 == b.energy_2;

        }
        return false;
    }

    public boolean contain(String name) {
        return name_1.equalsIgnoreCase(name) || name_2.equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return name_1 + "," + name_2 + "," + nutrient + "," +
                energy_1 + "," + energy_2 + ",";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Float.floatToIntBits(this.energy_1);
        hash = 23 * hash + Float.floatToIntBits(this.energy_2);
        hash = 23 * hash + (this.name_1 != null ? this.name_1.hashCode() : 0);
        hash = 23 * hash + (this.name_2 != null ? this.name_2.hashCode() : 0);
        hash = 23 * hash + (this.nutrient != null ? this.nutrient.hashCode() : 0);
        return hash;
    }

}
