package com.cyberkit.cyberkit_server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String extractToolIdFromRequest(String requestUri) {
        Pattern pattern = Pattern.compile("([a-f0-9\\-]{36})");
        Matcher matcher = pattern.matcher(requestUri);
        return matcher.find() ? matcher.group(1) : null;
    }
    public static int minDistance(String word1, String word2) {
        int[][] diff= new int[word1.length()+1][word2.length()+1];
        for(int i =0 ; i<= word1.length();i++){
            diff[i][0]= i;
        }
        for(int i =0 ; i<= word2.length();i++){
            diff[0][i]= i;
        }
        for (int i =1;i<=word1.length(); i++){
            for(int j=1; j<=word2.length(); j++){
                if(word1.charAt(i-1)==word2.charAt(j-1)){
                    diff[i][j]= diff[i-1][j-1];
                }
                else{
                    diff[i][j] = Math.min(diff[i-1][j-1],Math.min(diff[i][j-1],diff[i-1][j]))+1;
                }
            }
        }
        return diff[word1.length()][word2.length()];
    }
}
