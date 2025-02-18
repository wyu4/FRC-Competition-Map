package com.FRCCompetitionMap.Gui.SessionComponents;

import com.FRCCompetitionMap.Requests.FRC.ParsedData.MatchData.Match;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchDisplay extends JPanel {
    private static class AlliancePanel extends JPanel {
        private final List<JLabel> labels = Collections.synchronizedList(new ArrayList<>());

        public AlliancePanel(Match.AllianceType allianceType, List<Integer> alliance) {
            super(new GridLayout(1, alliance.size()));

            switch (allianceType) {
                case BLUE -> setBackground(UIManager.getColor("alliance.blue"));
                case RED -> setBackground(UIManager.getColor("alliance.red"));
            }

            for (Integer team : alliance) {
                JLabel label = new JLabel(String.valueOf(team));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                labels.add(label);
                add(label);
            }
        }

        public void update() {
            labels.forEach((label) -> label.setFont(label.getFont().deriveFont(getWidth()*0.025f)));
        }

    }

    private final AlliancePanel blueAlliance, redAlliance;
    private final JPanel scorePanel = new JPanel(null);
    private final JLabel descriptionHeader = new JLabel("Description"), scoreLabelBlue = new JLabel("0"), scoreLabelRed = new JLabel("0");

    public MatchDisplay(Match match) {
        super(null);

        blueAlliance = new AlliancePanel(Match.AllianceType.BLUE, match.getAlliance(Match.AllianceType.BLUE));
        redAlliance = new AlliancePanel(Match.AllianceType.RED, match.getAlliance(Match.AllianceType.RED));

        scoreLabelBlue.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabelRed.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabelBlue.setText(String.valueOf(match.getScore(Match.AllianceType.BLUE)));
        scoreLabelRed.setText(String.valueOf(match.getScore(Match.AllianceType.RED)));

        if (match.getWinner() == Match.WinnerType.BLUE) {
            scoreLabelBlue.setForeground(UIManager.getColor("alliance.win"));
            scoreLabelBlue.setFont(scoreLabelBlue.getFont().deriveFont(Font.BOLD));
        } else if (match.getWinner() == Match.WinnerType.RED) {
            scoreLabelRed.setForeground(UIManager.getColor("alliance.win"));
            scoreLabelRed.setFont(scoreLabelRed.getFont().deriveFont(Font.BOLD));
        }

        descriptionHeader.setFont(descriptionHeader.getFont().deriveFont(Font.BOLD));
        descriptionHeader.setText(match.getDescription());
        descriptionHeader.setHorizontalAlignment(SwingConstants.LEFT);

        scorePanel.setBackground(UIManager.getColor("background.darkerSubpage"));

        scorePanel.add(scoreLabelBlue);
        scorePanel.add(scoreLabelRed);

        add(descriptionHeader);
        add(blueAlliance); add(scorePanel);
        add(redAlliance);
    }

    public void update() {
        if (!isVisible()) {
            return;
        }

        descriptionHeader.setSize(getWidth(), (int)(getHeight()*0.25f));
        descriptionHeader.setLocation(0, 0);
        blueAlliance.setSize((int)(getWidth()*0.75f), (getHeight()-descriptionHeader.getHeight())/2);
        blueAlliance.setLocation(0, descriptionHeader.getHeight() + descriptionHeader.getY());
        redAlliance.setSize(blueAlliance.getSize());
        redAlliance.setLocation(0, blueAlliance.getHeight() + blueAlliance.getY());
        scorePanel.setSize(getWidth() - blueAlliance.getWidth(), getHeight() - descriptionHeader.getHeight());
        scorePanel.setLocation(blueAlliance.getX() + blueAlliance.getWidth(), descriptionHeader.getY() + descriptionHeader.getHeight());

        scoreLabelBlue.setSize(scorePanel.getWidth(), scorePanel.getHeight()/2);
        scoreLabelBlue.setLocation(0, 0);
        scoreLabelRed.setSize(scoreLabelBlue.getSize());
        scoreLabelRed.setLocation(0, scoreLabelBlue.getHeight());

        final float fontSize = getWidth()*0.03f;
        descriptionHeader.setFont(descriptionHeader.getFont().deriveFont(fontSize));
        scoreLabelBlue.setFont(scoreLabelBlue.getFont().deriveFont(fontSize*0.75f));
        scoreLabelRed.setFont(scoreLabelRed.getFont().deriveFont(fontSize*0.75f));

        blueAlliance.update();
        redAlliance.update();
    }
}