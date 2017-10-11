package controller.java.oponentInfo;
/**
 * @author Sergio Del Castillo
 * @email sergio.jose.delcastillo@gmail.com
 */


import controller.java.Validator;

import java.io.*;

public class OpponentInfo {
  /**
     * The name of the colony
     */
    String name;
    /**
     * The name of the author
     */
    String author;

    /**
     * The affiliation of the author
     */
    String affiliation;

    public OpponentInfo(String n, String au, String af) {
        if (!Validator.validateColonyAffiliation(af) ||
            !Validator.validateColonyAuthor(au) ||
            !Validator.validateColonyName(n)) {
            throw new IllegalArgumentException("Illegal Opponent Info");
        }
        this.name = n;
        this.author = au;
        this.affiliation = af;
    }

    /**
     *
     * @param line a line of opponent file
     */
    public OpponentInfo(String line) {
        if (line == null || line.trim().equals(""))
            throw new IllegalArgumentException("Illegal argument");

        String[] info = line.trim().split(",");

        if (info.length != 3)
            throw new IllegalArgumentException("Illegal argument");

        info[0]  = info[0].trim();
        info[1]  = info[1].trim();
        info[2]  = info[2].trim().toUpperCase();

        if(!Validator.validateColonyName(info[0]))
            throw new IllegalArgumentException("Illegal argument");

        if(!Validator.validateColonyAuthor(info[1]))
            throw new IllegalArgumentException("Illegal argument");

        if(!Validator.validateColonyAffiliation(info[2]))
            throw new IllegalArgumentException("Illegal argument");

        name = info[0];
        author = info[1];
        affiliation = info[2];
    }

    /**
     * Write the opponent information to opponent file.
      * @param path path of a opponent file to append the current information of opponent.
     * @throws IOException
     */
    public void save(String path) throws IOException {
        FileWriter f = new FileWriter(path, true);
        f.append(toCSV()).append("\n");
        f.close();
    }

    /**
     * delete the current information of the opponent file
     * @param path path of the opponent file
     * @throws IOException
     */
    public void del(String path) throws IOException {
        new File(path).renameTo(new File(path + "_backup"));
        File backup = new File(path + "_backup");
        
        BufferedReader in = new BufferedReader(new FileReader(backup));
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        String line, miLine = toCSV();

        while((line = in.readLine()) != null){
            if (!miLine.equals(line)) {
                out.append(line).append("\n");
            }
        }
        
        in.close();
        out.close();
        backup.delete();
    }

    public String getAffiliation() {
        return affiliation.toUpperCase();
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public String toCSV(){
        return name + "," + author + "," + affiliation.toUpperCase() + ",";
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof OpponentInfo) {
            OpponentInfo o = (OpponentInfo) obj;
            return name.equalsIgnoreCase(o.name);/* &&
                    author.equalsIgnoreCase(o.author) &&
                    affiliation.equalsIgnoreCase(o.affiliation);*/
        }
        return false;
    }

    public boolean contain(String name) {
        return this.name.equals(name);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.author != null ? this.author.hashCode() : 0);
        hash = 67 * hash + (this.affiliation != null ? this.affiliation.hashCode() : 0);
        return hash;
    }
}
