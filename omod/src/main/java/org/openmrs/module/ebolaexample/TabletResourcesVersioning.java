package org.openmrs.module.ebolaexample;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TabletResourcesVersioning {
    public static void main(String args[]) throws IOException {
        String htmlFile = args[0];
        String version = args[1];

        System.out.println("=======================================================");
        System.out.println(String.format("Adding version %s to %s", version, htmlFile));

        Path path = Paths.get(htmlFile);
        Charset charset = StandardCharsets.UTF_8;

        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, charset);
        String timestamp = DateTime.now().toString(".yyMMdd.hhmmssSSS");
        content = replaceContent(version + timestamp, content);
        Files.write(path, content.getBytes(charset));

        System.out.println("Added version successfully!");
        System.out.println("=============================================================");
    }

    protected static String replaceContent(String version, String content) {
        String jsReplacement = ".js?version="+ version;
        String cssReplacement = ".css?version="+ version;
        content = content.replaceAll("(?<!min)\\.js(?=\")(?!\\?version)", jsReplacement);
        content = content.replaceAll("(?<!min)\\.css(?=\")(?!\\?version)", cssReplacement);
        return content;
    }
}
