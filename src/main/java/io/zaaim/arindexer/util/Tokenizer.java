package io.zaaim.arindexer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Tokenizer{
    public static String[] tokenize(String text) {
        String inputText = text.trim();
        String[] tokens = inputText.split("\\s+");
        return tokens;
    }

    public static String[] tokenize(String text, boolean withUniqueTokens) {
        if (!withUniqueTokens) {
            return tokenize(text);
        } else {
            String[] tokens = tokenize(text);
            List<String> list = new ArrayList();

            for(String token : tokens) {
                if (!list.contains(token)) {
                    list.add(token);
                }
            }

            String[] result = new String[list.size()];

            for(int i = 0; i < list.size(); ++i) {
                result[i] = (String)list.get(i);
            }

            return result;
        }
    }

    public static String[] tokenize(File inputFile) {
        return tokenizeFile(inputFile, "UTF-8");
    }

    public static String[] tokenize(File inputFile, String inputEncoding, boolean withUniqueTokens) {
        return tokenizeFile(inputFile, inputEncoding, withUniqueTokens);
    }


    private static String[] tokenizeFile(File inputFile, String inputEncoding) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = null;

        try {
            FileInputStream fis = new FileInputStream(inputFile);
            InputStreamReader ipsr = new InputStreamReader(fis, inputEncoding);
            BufferedReader brd = new BufferedReader(ipsr);

            String in;
            while((in = brd.readLine()) != null) {
                sb.append(in);
                sb.append("\n");
            }

            tokens = tokenize(sb.toString());
            brd.close();
            fis.close();
        } catch (Exception e) {

        }

        return tokens;
    }

    private static String[] tokenizeFile(File inputFile, String inputEncoding, boolean withUniqueTokens) {
        if (!withUniqueTokens) {
            return tokenizeFile(inputFile, inputEncoding);
        } else {
            String[] tokens = tokenizeFile(inputFile, inputEncoding);
            List<String> list = new ArrayList();

            for(String token : tokens) {
                if (!list.contains(token)) {
                    list.add(token);
                }
            }

            String[] result = new String[list.size()];

            for(int i = 0; i < list.size(); ++i) {
                result[i] = (String)list.get(i);
            }

            return result;
        }
    }


}
