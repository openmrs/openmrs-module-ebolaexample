package org.openmrs.module.ebolaexample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TabletResourcesVersioningTest {

    @Test
    public void shouldAddVersionToJsFileName(){
        String content = "<script src=\"test.js\"></script>";
        String result = TabletResourcesVersioning.replaceContent("1.0", content);
        assertEquals("<script src=\"test.js?version=1.0\"></script>", result);
    }

    @Test
    public void shouldNotAddVersionToMinJsFileName(){
        String content = "<script src=\"test.min.js\"></script>";
        String result = TabletResourcesVersioning.replaceContent("1.0", content);
        assertEquals("<script src=\"test.min.js\"></script>", result);
    }

    @Test
    public void shouldNotAddVersionToVersionedJsFileName(){
        String content = "<script src=\"test.js?version=1.1\"></script>";
        String result = TabletResourcesVersioning.replaceContent("1.0", content);
        assertEquals("<script src=\"test.js?version=1.1\"></script>", result);
    }

    @Test
    public void shouldAddVersionToCssFileName(){
        String content = "<script src=\"test.css\"></script>";
        String result = TabletResourcesVersioning.replaceContent("1.0", content);
        assertEquals("<script src=\"test.css?version=1.0\"></script>", result);
    }

    @Test
    public void shouldNotAddVersionToMinCssFileName(){
        String content = "<script src=\"test.min.css\"></script>";
        String result = TabletResourcesVersioning.replaceContent("1.0", content);
        assertEquals("<script src=\"test.min.css\"></script>", result);
    }

    @Test
    public void shouldNotAddVersionToVersionedCssFileName(){
        String content = "<script src=\"test.css?version=1.1\"></script>";
        String result = TabletResourcesVersioning.replaceContent("1.0", content);
        assertEquals("<script src=\"test.css?version=1.1\"></script>", result);
    }
}