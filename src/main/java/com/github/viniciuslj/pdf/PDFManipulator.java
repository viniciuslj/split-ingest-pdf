package com.github.viniciuslj.pdf;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFManipulator {
    private PDFTextStripper stripper;
    private Splitter splitter;

    public PDFManipulator() throws IOException {
        stripper = new PDFTextStripper();
        splitter = new Splitter();
    }

    private PDDocument load(Path path) throws IOException {
        return PDDocument.load(new File(path.toString()));
    }

    private List<PDDocument> splitLoad(Path path) throws IOException {
        return splitter.split(load(path));
    }

    private Map<String, Object> setContent(Map<String, Object> map, PDDocument document) throws IOException {
        map.put("content", stripper.getText(document));
        return map;
    }

    private Map<String, Object> makeMap(Path path, PDDocument document) throws IOException {
        Map<String, Object> map = new HashMap<>();

        String absolutePath = path.toAbsolutePath().toString();
        map.put("fileName", path.getFileName());
        map.put("absolutePath", absolutePath);
        map.put("directory", path.toAbsolutePath().getParent().toString());

        String[] splitAbsolutePath = absolutePath.replace("\\", "/").split("/");
        map.put("parent", splitAbsolutePath[splitAbsolutePath.length - 2]);

        if(splitAbsolutePath.length >= 3) {
            map.put("parentParent", splitAbsolutePath[splitAbsolutePath.length - 3]);
        }

        setContent(map, document);
        return map;
    }

    public Map<String, Object> getDocumentMap(Path path) throws IOException {
        PDDocument document = load(path);
        Map<String, Object> map = makeMap(path, document);
        map.put("pages", document.getNumberOfPages());
        return map;
    }

    public List<Map<String, Object>> getPagesMap(Path path) throws IOException {
        List<PDDocument> pages = splitLoad(path);
        List<Map<String, Object>> listMapPages = new ArrayList<>(pages.size());

        for (PDDocument page: pages) {
            long pageNumber = listMapPages.size() + 1;
            Map<String, Object> mapPage = makeMap(path, page);
            mapPage.put("page", pageNumber);
            listMapPages.add(mapPage);
        }

        return listMapPages;
    }
}
