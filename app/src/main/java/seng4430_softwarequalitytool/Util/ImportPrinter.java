package seng4430_softwarequalitytool.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class ImportPrinter {




    public static String importTreeToJson(Collection<String> imports) {
        ImportNode root = new ImportNode();

        // Construct the trie
        for (String importStr : imports) {
            String[] parts = importStr.split("\\.");
            ImportNode current = root;
            for (String part : parts) {
                current = current.children.computeIfAbsent(part, k -> new ImportNode(part));
            }
        }

        // Convert the trie to JSON
        Gson gson = new GsonBuilder().create();
        return gson.toJson(root);
    }

    static class ImportNode {
        String name;
        Map<String, ImportNode> children;

        public ImportNode() {
            this.name = null;
            this.children = new HashMap<>();
        }

        public ImportNode(String name) {
            this.name = name;
            this.children = new HashMap<>();
        }
    }
}
