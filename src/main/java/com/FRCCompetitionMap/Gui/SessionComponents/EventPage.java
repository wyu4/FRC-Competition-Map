package com.FRCCompetitionMap.Gui.SessionComponents;

import com.FRCCompetitionMap.Gui.CustomComponents.RoundedPanel;
import com.FRCCompetitionMap.Gui.SessionUtils;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData.Event;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.MatchData.EventMatches;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.MatchData.Match;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.ParsedTuple;
import com.FRCCompetitionMap.Requests.LoggedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EventPage extends RoundedPanel implements SessionComponents {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventPage.class);

    private final JPanel infoPanel = new JPanel(null), headerPanel = new JPanel(new GridBagLayout());
    private final JButton backButton = new JButton("<");
    private final QualificationPanel qualificationPanel = new QualificationPanel();

    private final JLabel
            eventHeader = new JLabel("Event"),
            districtHeader = new JLabel("???"),
            codeHeader = new JLabel("Event Code:"),
            venueHeader = new JLabel("Venue:"),
            locationHeader = new JLabel("@"),
            websiteHeader = new JLabel("Website:"),
            codeLabel = new JLabel("???"),
            venueLabel = new JLabel("???"),
            locationLabel = new JLabel("???");
    private final JButton websiteLabel = new JButton("???");

    private Runnable onBack;
    private boolean focused = false;
    private LoggedThread loadTask;

    public EventPage() {
        super();

//        setLayout(new GridLayout(1, 2));
        setLayout(null);
        setBackground(UIManager.getColor("background.subpage"));

        infoPanel.setBackground(getBackground());
        headerPanel.setBackground(UIManager.getColor("invisible"));

        eventHeader.setFont(eventHeader.getFont().deriveFont(Font.BOLD));
        districtHeader.setFont(districtHeader.getFont().deriveFont(Font.ITALIC));
        backButton.setFont(backButton.getFont().deriveFont(Font.BOLD));
        backButton.addActionListener((e) -> {
            if (focused && onBack != null) {
                onBack.run();
            }
        });

        websiteLabel.setBackground(UIManager.getColor("background.subpage"));
        websiteLabel.setFocusPainted(false);
        websiteLabel.setFocusable(false);
        websiteLabel.addActionListener((e) -> {
            try {
                SessionUtils.openLink(websiteLabel.getText());
            } catch (Exception ignore) {}
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
        constraints.weightx = 2; constraints.weighty = 0.8;
        constraints.gridx = 1; constraints.gridy = 2;
        constraints.insets = new Insets(0, 0, 0, defaultInset.right/2);
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
        constraints.weightx = 0.1; constraints.weighty = 0.8f;
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
        infoPanel.add(qualificationPanel);

        add(infoPanel);
//        add(scrollPane);
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

            if (loadTask != null && !loadTask.isInterrupted()) {
                loadTask.interrupt();
            }

            loadTask = new LoggedThread(getClass(), () -> {
                qualificationPanel.setLoading(true);
                try {
                    ParsedTuple<List<Match>> matches = EventMatches.getMatches(event);

                    if (Thread.currentThread().isInterrupted()) {
                        qualificationPanel.setLoading(false);
                        Thread.currentThread().interrupt();
                        return;
                    }

                    qualificationPanel.clearDisplays();
                    for (Match match : matches.getParsed()) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        qualificationPanel.addDisplay(match);
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not complete loading job.", e);
                }
                qualificationPanel.setLoading(false);
                Thread.currentThread().interrupt();
            });
            loadTask.start();

        } else {
            LOGGER.error("Could not parse event ({}).", raw.getClass());
        }
    }

    @Override
    public void update() {
        if (!isVisible()) {
            return;
        }

        infoPanel.setSize(getSize());
        infoPanel.setLocation(0, 0);

        backButton.setSize((int)(infoPanel.getWidth()*0.05f), (int)(infoPanel.getWidth()*0.05f));
        backButton.setLocation((int)(infoPanel.getWidth()*0.025f), (int)(infoPanel.getWidth()*0.025f));
        headerPanel.setSize((int)(infoPanel.getWidth()*0.95f), infoPanel.getHeight()/2);
        headerPanel.setLocation(infoPanel.getWidth() - headerPanel.getWidth(), 0);
        qualificationPanel.setSize(infoPanel.getWidth(), infoPanel.getHeight() - (headerPanel.getHeight() + headerPanel.getY()));
        qualificationPanel.setLocation(0, headerPanel.getY() + headerPanel.getHeight());

        final float fontSize = headerPanel.getWidth()*0.04f;

        backButton.setFont(backButton.getFont().deriveFont(fontSize));
        districtHeader.setFont(districtHeader.getFont().deriveFont(fontSize*0.9f));
        eventHeader.setFont(eventHeader.getFont().deriveFont(fontSize*0.6f));
        codeHeader.setFont(codeHeader.getFont().deriveFont(fontSize*0.5f)); codeLabel.setFont(codeHeader.getFont());
        venueHeader.setFont(venueHeader.getFont().deriveFont(fontSize*0.5f)); venueLabel.setFont(venueHeader.getFont());
        locationHeader.setFont(venueHeader.getFont()); locationLabel.setFont(locationHeader.getFont());
        websiteHeader.setFont(venueHeader.getFont()); websiteLabel.setFont(websiteHeader.getFont());

        locationHeader.setFont(locationHeader.getFont().deriveFont(fontSize));

        qualificationPanel.update();
    }
}

class QualificationPanel extends JPanel {
    private final List<MatchDisplay> displays = new ArrayList<>();

    private final JPanel scrollPaneView;
    private final JScrollPane scrollPane;
    private final JPanel loadingPanel = new JPanel(null);
    private final JLabel loadingLabel = new JLabel("Loading Matches...");

    public QualificationPanel() {
        super(null);

        scrollPaneView = new JPanel(null);

        loadingPanel.setBackground(UIManager.getColor("background.loading"));
        loadingLabel.setFont(loadingLabel.getFont().deriveFont(Font.BOLD));
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        scrollPane = new JScrollPane(scrollPaneView);
        scrollPane.setBorder(null);
        scrollPane.setDoubleBuffered(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPaneView.setBackground(Color.BLACK);

        loadingPanel.add(loadingLabel);

        add(loadingPanel);
        add(scrollPane);
    }

    public void clearDisplays() {
        displays.forEach(scrollPaneView::remove);
        displays.clear();
    }

    public void setLoading(boolean loading) {
        loadingPanel.setVisible(loading);
    }

    public void addDisplay(Match match) {
        MatchDisplay display = new MatchDisplay(match);
        displays.add(display);
        scrollPaneView.add(display);
    }

    public void update() {
        if (!isVisible()) {
            return;
        }

        loadingPanel.setSize(getSize());
        loadingPanel.setLocation(0, 0);
        loadingLabel.setSize(loadingPanel.getSize());
        loadingLabel.setLocation(0, 0);

        scrollPane.setSize(getWidth() - (scrollPane.getVerticalScrollBar().isVisible() ? scrollPane.getVerticalScrollBar().getWidth() : 0), getHeight());
        scrollPane.setLocation(0, 0);

        final float fontSize = getWidth()*0.04f;
        loadingLabel.setFont(loadingLabel.getFont().deriveFont(fontSize));

        try {
            int index = 0;
            for (MatchDisplay display : displays) {
                display.setSize(scrollPane.getWidth()/2, scrollPane.getHeight()/2);
                display.setLocation(scrollPane.getWidth()/4,(index * scrollPane.getHeight()/2) + (index * scrollPane.getHeight()/10));
                display.update();
                if (index == displays.size()-1) {
                    scrollPaneView.setSize(scrollPane.getWidth(), (int)(display.getY() + (display.getHeight() * 1.5f)));
                    scrollPaneView.setPreferredSize(scrollPaneView.getSize());
                }
                index ++;
            }
        } catch (Exception ignore) {}
    }
}


