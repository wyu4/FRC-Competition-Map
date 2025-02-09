package com.FRCCompetitionMap.Requests;

import com.FRCCompetitionMap.Requests.FRC.ParsedData.PlayoffMatch;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FRCTest {
    public static void main(String[] args) throws IOException {
        File file = new File("playoffTest.json");
        StringBuilder playoffBracket = new StringBuilder();
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            playoffBracket.append(myReader.nextLine());
        }
        myReader.close();

        LinkedTreeMap<?, ?> parsed = DataParser.PARSER.fromJson(playoffBracket.toString(), LinkedTreeMap.class);
        List<?> matches = DataParser.objectToList(parsed.get("Matches"));
        Object firstMatch = matches.getFirst();
        LinkedTreeMap<?, ?> parsedFirstMatch;
        if (firstMatch instanceof LinkedTreeMap<?, ?>) {
            parsedFirstMatch = (LinkedTreeMap<?, ?>) firstMatch;
        } else {
            throw new ClassCastException("Could not cast " + firstMatch.getClass() + " to LinkedTreeMap.");
        }

//        System.out.println(parsedFirstMatch);

        PlayoffMatch playoffMatch = new PlayoffMatch(parsedFirstMatch);

//        System.out.println(playoffMatch.getNumber());

        System.out.println(playoffMatch.getAlliance(PlayoffMatch.AllianceType.RED));
    }
}
