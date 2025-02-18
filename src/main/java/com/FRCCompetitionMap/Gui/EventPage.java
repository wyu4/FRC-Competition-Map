package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Gui.CustomComponents.RoundedPanel;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class EventPage extends RoundedPanel implements SessionComponents {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventPage.class);

    private final JPanel infoPanel = new JPanel(null), headerPanel = new JPanel(new GridBagLayout()), matchPanel = new JPanel();
    private final JButton backButton = new JButton("<");
    private final JScrollPane scrollPane = new JScrollPane();
    private final PlayoffPanel playoffPanel = new PlayoffPanel();

    private final JLabel
            eventHeader = new JLabel("Event"),
            districtHeader = new JLabel("???"),
            codeHeader = new JLabel("Event Code:"),
            venueHeader = new JLabel("Venue:"),
            locationHeader = new JLabel("@"),
            websiteHeader = new JLabel("Website:"),
            codeLabel = new JLabel("???"),
            venueLabel = new JLabel("???"),
            locationLabel = new JLabel("???"),
            websiteLabel = new JLabel("???");

    private Runnable onBack;
    private boolean focused = false;

    public EventPage() {
        super();

        setLayout(new GridLayout(1, 2));
        setBackground(UIManager.getColor("background.subpage"));

        infoPanel.setBackground(getBackground());
        headerPanel.setBackground(UIManager.getColor("invisible"));

        scrollPane.getViewport().setView(matchPanel);

        eventHeader.setFont(eventHeader.getFont().deriveFont(Font.BOLD));
        districtHeader.setFont(districtHeader.getFont().deriveFont(Font.BOLD));
        backButton.setFont(backButton.getFont().deriveFont(Font.BOLD));
        backButton.addActionListener((e) -> {
            if (focused && onBack != null) {
                onBack.run();
            }
        });

        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.setVerticalAlignment(SwingConstants.CENTER);
        districtHeader.setHorizontalAlignment(SwingConstants.CENTER);
        codeHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        venueHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        locationHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        websiteHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        codeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        venueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        locationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        websiteLabel.setHorizontalAlignment(SwingConstants.LEFT);

        Insets defaultInset = new Insets((int)(SessionUtils.SCREEN_SIZE.getHeight()*0.005f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f), (int)(SessionUtils.SCREEN_SIZE.getHeight()*0.005f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridwidth = 1; constraints.gridheight = 1;
        constraints.weightx = 0.1; constraints.weighty = 0.5;
        constraints.gridx = 1; constraints.gridy = 1;
        constraints.insets = new Insets(defaultInset.top, defaultInset.left, defaultInset.bottom*2, defaultInset.right/2);
        headerPanel.add(districtHeader, constraints);

        constraints.weightx = 1; constraints.weighty = 2;
        constraints.gridx = 2;
        constraints.insets = new Insets(defaultInset.top, defaultInset.left/2, defaultInset.bottom*2, defaultInset.right);
        headerPanel.add(eventHeader, constraints);

        //////////////////////////////////////////////////////////////////////////////
        // Subheaders
        constraints.weighty = 0.8;
        constraints.gridx = 1; constraints.gridy = 2;
        constraints.insets = new Insets(0, defaultInset.left, 0, defaultInset.right/2);
        headerPanel.add(codeHeader, constraints);

        constraints.weighty = 0.8;
        constraints.gridy = 3;
        headerPanel.add(venueHeader, constraints);

        constraints.weighty = 0.8;
        constraints.gridy = 4;
        headerPanel.add(locationHeader, constraints);

        constraints.weighty = 0.8;
        constraints.gridy = 5;
        constraints.insets = new Insets(0, defaultInset.left, defaultInset.bottom*2, defaultInset.right/2);
        headerPanel.add(websiteHeader, constraints);

        //////////////////////////////////////////////////////////////////////////////
        // Contents
        constraints.weighty = 0.8f;
        constraints.gridx = 2; constraints.gridy = 2;
        constraints.insets = new Insets(0, defaultInset.left/2, 0, defaultInset.right);
        headerPanel.add(codeLabel, constraints);

        constraints.weighty = 0.8;
        constraints.gridy = 3;
        headerPanel.add(venueLabel, constraints);

        constraints.weighty = 0.8;
        constraints.gridy = 4;
        headerPanel.add(locationLabel, constraints);

        constraints.weighty = 0.8;
        constraints.gridy = 5;
        constraints.insets = new Insets(0, defaultInset.left, defaultInset.bottom*2, defaultInset.right/2);
        headerPanel.add(websiteLabel, constraints);

        infoPanel.add(backButton);
        infoPanel.add(headerPanel);
        infoPanel.add(matchPanel);

        add(infoPanel);
        add(scrollPane);
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            reloadData();
        }
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void reloadData() {
        Object raw = MainPage.getTransferredData("event");
        if (raw instanceof Event event) {
            eventHeader.setText(event.getShortenedName());
            districtHeader.setText("[%s]".formatted(event.getDistrictCode()));
            codeLabel.setText(event.getCode());
            venueLabel.setText(event.getVenue());
            locationLabel.setText(event.getFullAddress());
            websiteLabel.setText(event.getWebsite());
        } else {
            LOGGER.error("Could not parse event ({}).", raw.getClass());
        }
    }

    @Override
    public void update() {
        if (!isVisible()) {
            return;
        }

        backButton.setSize((int)(infoPanel.getWidth()*0.05f), (int)(infoPanel.getWidth()*0.05f));
        backButton.setLocation((int)(infoPanel.getWidth()*0.025f), (int)(infoPanel.getWidth()*0.025f));
        headerPanel.setSize((int)(infoPanel.getWidth()*0.9f), infoPanel.getHeight()/2);
        headerPanel.setLocation(infoPanel.getWidth() - headerPanel.getWidth(), 0);
        matchPanel.setSize(infoPanel.getWidth(), infoPanel.getHeight() - (headerPanel.getHeight() + headerPanel.getY()));
        matchPanel.setLocation(0, headerPanel.getY() + headerPanel.getHeight());

        final float fontSize = headerPanel.getWidth()*0.05f;

        backButton.setFont(backButton.getFont().deriveFont(fontSize));
        districtHeader.setFont(districtHeader.getFont().deriveFont(fontSize));
        eventHeader.setFont(eventHeader.getFont().deriveFont(fontSize*0.6f));
        codeHeader.setFont(codeHeader.getFont().deriveFont(fontSize*0.5f)); codeLabel.setFont(codeHeader.getFont());
        venueHeader.setFont(venueHeader.getFont().deriveFont(fontSize*0.5f)); venueLabel.setFont(venueHeader.getFont());
        locationHeader.setFont(venueHeader.getFont()); locationLabel.setFont(locationHeader.getFont());
        websiteHeader.setFont(venueHeader.getFont()); websiteLabel.setFont(websiteHeader.getFont());

        locationHeader.setFont(locationHeader.getFont().deriveFont(fontSize));
    }
}

class PlayoffPanel extends JPanel {

}