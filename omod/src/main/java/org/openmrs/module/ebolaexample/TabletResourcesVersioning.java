package org.openmrs.module.ebolaexample;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

public class TabletResourcesVersioning {
    public static void main(String args[]) throws IOException {
        String htmlFile = args[0];
        String version = getTimestamp();

        System.out.println("=======================================================");
        System.out.println(String.format("Adding version %s to %s", version, htmlFile));

        String content = getFileContent(htmlFile);
        content = replaceContent(version, content);

        FileWriter fileWriter = new FileWriter(htmlFile);
        fileWriter.write(content);
        fileWriter.close();

        System.out.println("Added version successfully!");
        System.out.println("=============================================================");
    }

    private static String getTimestamp() {
        DateFormat dateFormat = new java.text.SimpleDateFormat("yyMMdd.hhmmssSSS");
        return dateFormat.format(new Date());
    }

    private static String getFileContent(String htmlFile) throws IOException {
        BufferedReader reader = new BufferedReader (new InputStreamReader(new FileInputStream(htmlFile))) ;
        String line = reader.readLine();
        StringBuffer stringBuffer = new StringBuffer("");
        String ls = System.getProperty("line.separator");

        while ( line != null ) {
            stringBuffer.append(line).append(ls);
            line = reader.readLine();
        }
        reader.close();
        return stringBuffer.toString();
    }

    protected static String replaceContent(String version, String content) {
        String jsReplacement = ".js?version="+ version;
        String cssReplacement = ".css?version="+ version;
        content = content.replaceAll("(?<!min)\\.js(?=\")(?!\\?version)", jsReplacement);
        content = content.replaceAll("(?<!min)\\.css(?=\")(?!\\?version)", cssReplacement);
        return content;
    }
}
