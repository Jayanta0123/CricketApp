package com.sports.api.CricketApp.service.impl;

import com.sports.api.CricketApp.dto.AllMatchesDTO;
import com.sports.api.CricketApp.dto.MatchScoreSummaryDTO;
import com.sports.api.CricketApp.service.AllMatchesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

@Service
public class AllMatchesServiceImpl implements AllMatchesService {

    public static final String cricApiURL = "http://cricapi.com/api/";
    public static final String MATCHES = "matches";
    public static final String CRIC_SCORE = "cricketScore";
    public static final String UNQ_ID = "&unique_id=";
    public static final String apiKey = "16Dyx0esynMy9Jz4JocdLGC97Ek1";

    private static final Logger log = LoggerFactory.getLogger(AllMatchesServiceImpl.class);

    @Override
    public String getAllCurrentMatches() {
        String response="";
        String finalResponse="";

        try {
            String allMatchesURL = cricApiURL + MATCHES + "?apikey=" + apiKey;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<AllMatchesDTO> responseEntity = restTemplate.getForEntity(allMatchesURL, AllMatchesDTO.class);
            response = responseEntity.getBody().toString();

            String preLoad = " <!DOCTYPE html> " +
                             "  <html lang=\"en\"> <head> " +
                             "  <title>List of All Ongoing Matches</title></head> ";

            preLoad += " <style> table, th, td { border: 1px solid black; } </style> ";

            String headers = "<tr> <th>" + "uniqueId" + "</th>" +
                    "<th>" + "team2" + "</th>" + "<th>" + "team1" + "</th>" +
                    "<th>" + "matchType" + "</th>" + "<th>" + "matchDate" + "</th>" +
                    "<th>" + "matchDateGMT" + "</th>" + "<th>" + "squad" + "</th>" +
                    "<th>" + "tossWinner" + "</th>" + "<th>" + "matchStarted" + "</th> </tr>";
            response = processResponse (response, "MatchSummaryDTO");

            finalResponse = preLoad + " <table> " + headers + response + "</table>" + "</html>";
        }
        catch (Exception ex) {
            log.error("Error in fetching list of all the matches", ex);
        }
        return finalResponse;
    }

    @Override
    public String getSelectedMatchScore(String unqMatchId) {
        String matchNum = unqMatchId.substring(unqMatchId.indexOf('_')+1);
        String currentMatchUrl = cricApiURL + CRIC_SCORE + "?apikey=" + apiKey + UNQ_ID + matchNum;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MatchScoreSummaryDTO> responseEntity = restTemplate.getForEntity(currentMatchUrl, MatchScoreSummaryDTO.class);

        String response = responseEntity.getBody().toString();
        String preLoad = "<html lang=\"en\"> <head> " +
                "  <title>Selected match#" + matchNum.substring(0,matchNum.indexOf('.')) + " Score</title></head> ";

        preLoad += " <style> table, th, td { border: 1px solid black; } </style> ";

        String headers = "<tr> <th>" + "score" + "</th>" +
                "<th>" + "description" + "</th>" + "<th>" + "matchStarted" + "</th>" +
                "<th>" + "team1" + "</th>" + "<th>" + "team2" + "</th></tr>";

        response = processSingleMatchResponse(response, "MatchScoreSummaryDTO");

        String finalResponse = preLoad + " <table> " + headers + response + "</table>" + "</html>";

        return finalResponse;
    }

    private String processSingleMatchResponse(String response, String key) {
        String matchResp = response.substring(key.length()+1);

        matchResp = matchResp.replace("}", "");
        matchResp = "<tr><td>" + matchResp;
        matchResp = matchResp.replaceAll(", ", "</td><td>");
        matchResp = matchResp.replaceAll("[']", "");

        matchResp = matchResp.replaceAll("score=", "");
        matchResp = matchResp.replaceAll("description=", "");
        matchResp = matchResp.replaceAll("matchStarted=", "");
        matchResp = matchResp.replaceAll("team1=", "");
        matchResp = matchResp.replaceAll("team2=", "");

        matchResp = matchResp + "</td></tr>";

//        System.out.println(matchResp);
        return  matchResp;
    }

    private String processResponse (String response,  String key) {
        String[] tokens = response.split(key);
        String newResp = "";
        for(int i=0; i<tokens.length; i++)
            newResp += tokens[i] + "";

        int beginBracketIdx = newResp.indexOf('[');
        int closeBracketIdx = newResp.indexOf(']');

        newResp = newResp.substring(beginBracketIdx+1, closeBracketIdx);
        newResp = newResp.replaceAll("[{]", "<tr><td align=\"center\"> <a href=match_123.html>");
        newResp = newResp.replaceAll(", ", "</td><td>");
        newResp = newResp.replaceAll("},", "</tr><br><tr>");
        newResp = newResp.replaceAll("[}]", "");
        newResp = newResp.replaceAll("[']", "");

        newResp = newResp.replaceAll("uniqueId=", "");
        newResp = newResp.replaceAll("team2=", "");
        newResp = newResp.replaceAll("team1=", "");
        newResp = newResp.replaceAll("matchType=", "");
        newResp = newResp.replaceAll("matchDate=", "");
        newResp = newResp.replaceAll("matchDateGMT=", "");
        newResp = newResp.replaceAll("squad=", "");
        newResp = newResp.replaceAll("tossWinner=", "");
        newResp = newResp.replaceAll("matchStarted=", "");

        int length = newResp.split(Pattern.quote("match_123.html"), -1).length - 1;

        int lastIndex = 0;
        String findString = "match_123.html>";
        int totalOcc = 0;
        String[] allMatches = new String[500];

        while (lastIndex != length) {
            //System.out.println(lastIndex);
            lastIndex = newResp.indexOf(findString, lastIndex);
            String matchNum = newResp.substring(lastIndex+15,lastIndex+22);
            allMatches[totalOcc] = matchNum;
            //System.out.println("matchNum = " + matchNum);
            if (lastIndex != -1) {
                lastIndex += findString.length();
            }
            //System.out.println("Occ = " + totalOcc + ", matchNum = " + matchNum);
            if (totalOcc++ == length) break;
        }

        String resultResponse = "";

        final String separator = "#######";
        String oneMoreResp = newResp.replaceAll(findString, separator + findString);

        String[] tokens2 = oneMoreResp.split(separator);
        for (int i=0; i<tokens2.length-1; i++) {
            //System.out.println(tokens2[i] + " - token[" + i + "], match" + allMatches[i]);
            tokens2[i+1] = tokens2[i+1].replace(findString, "matchScore/" + allMatches[i] + ".html>");
            resultResponse += tokens2[i];
        }

        newResp = resultResponse;

        newResp = newResp + " " + length;

        return newResp;
    }
}
