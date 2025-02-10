package com.FRCCompetitionMap.Requests;

import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.TBA.TBA;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class RequestTest {
    public static void main(String[] args) throws IOException {
//        PrintStream defaultStream = System.out;
//
//        File file = new File("playoffTest.json");
//        file.createNewFile();
//        System.setOut(new PrintStream(file));
//
//        String playoffBracket = FRC.searchPlayoffBracket(2023, "arpky", "{}")[1];
//
//        System.out.println(playoffBracket);

        PrintStream defaultStream = System.out;

        File file = new File("allianceDataTest.json");
        file.createNewFile();
        System.setOut(new PrintStream(file));

        String playoffBracket = FRC.searchAllianceSelection(2023, "arpky", "{}")[1];

        System.out.println(playoffBracket);
    }
}
