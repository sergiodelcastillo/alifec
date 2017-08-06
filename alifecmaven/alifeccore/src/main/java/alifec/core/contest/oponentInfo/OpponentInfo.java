/**
 * @author Sergio Del Castillo
 * @email sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.oponentInfo;

import java.io.*;
import java.util.Vector;

public class OpponentInfo {
    private String name = "";
    private String author = "";
    private String affiliation = "";

    public OpponentInfo(String n, String au, String af) {
        this.name = n;
        this.author = au;
        this.affiliation = af.toUpperCase();
    }

    public OpponentInfo(String line) {
        if (line == null || line.equalsIgnoreCase(""))
            throw new IllegalArgumentException("Illegal Argument");

        String[] info = line.trim().split(",");

        if (info.length != 3)
            throw new IllegalArgumentException("Illegal Argument");

        name = info[0];
        author = info[1];
        affiliation = info[2].toUpperCase();
    }

    public void write(String path) throws IOException {
        FileWriter f = new FileWriter(path, true);
        BufferedWriter bw = new BufferedWriter(f);
        bw.append(toString()).append("\n");
        bw.close();
        f.close();
    }

    public void del(String path) throws IOException {
        new File(path).renameTo(new File(path + "_backup"));

        BufferedReader in = new BufferedReader(new FileReader(path + "_backup"));
        BufferedWriter out = new BufferedWriter(new FileWriter(path));


        String line, miLine = toString();
        do {
            line = in.readLine();
            if (!miLine.equalsIgnoreCase(line))
                out.append(line).append("\n");
        } while (line != null);
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

    @Override
    public String toString() {
        return name + "," + author + "," + affiliation.toUpperCase() + ",";
    }

    public Vector<Object> toVector() {
        Vector<Object> v = new Vector<>();
        v.addElement(name);
        v.addElement(author);
        v.addElement(affiliation);
        return v;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof OpponentInfo) {
            OpponentInfo o = (OpponentInfo) obj;
            return name.equalsIgnoreCase(o.name) &&
                    author.equalsIgnoreCase(o.author) &&
                    affiliation.equalsIgnoreCase(o.affiliation);
        }
        return false;
    }

    public boolean contain(String name) {
        return this.name.equalsIgnoreCase(name);
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
