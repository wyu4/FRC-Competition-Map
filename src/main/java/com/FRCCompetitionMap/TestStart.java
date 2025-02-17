package com.FRCCompetitionMap;

import com.FRCCompetitionMap.Gui.Session;
import com.FRCCompetitionMap.Requests.RequestTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TestStart {
    private static final HashMap<String, Runnable> tasks = new HashMap<>();
    private static final List<Runnable> listedTasks = new ArrayList<>();

    public static void main(String[] args) {
        tasks.put("Request Test", () -> {
            try {
                RequestTest.main(args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        tasks.put("Start", () -> Start.main(args));

        tasks.put("Pre-Authenticated Session", () -> Session.startSession(1));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Input (0-" + (tasks.size()-1) + ")");
        int index = 0;
        for (String key : tasks.keySet()) {
            System.out.println(index + ": " + key);
            listedTasks.add(tasks.get(key));
            index++;
        }

        // Selection //

        Runnable chosenRunnable = null;
        while (chosenRunnable == null) {
            String input = scanner.nextLine();
            try {
                int parsed = Integer.parseInt(input);
                if (parsed > 0 && parsed < listedTasks.size()) {
                    chosenRunnable = listedTasks.get(parsed);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Incorrect input. Please try again.");
            }
        }
        scanner.close();
        System.out.println("--------------------------\n-------------------------\n");
        chosenRunnable.run();
    }
}
