package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Encryption.AES;
import com.FRCCompetitionMap.Gui.CustomComponents.RoundedPanel;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.DistrictData.District;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.DistrictData.SeasonDistricts;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData.DistrictEvents;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData.Event;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.ParsedTuple;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.SeasonSummary;
import com.FRCCompetitionMap.Requests.LoggedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainPage extends RoundedPanel implements SessionComponents {
    private static final Hashtable<String, Object> transferredData = new Hashtable<>();

    public static Object getTransferredData(String key) {
        return transferredData.get(key);
    }

    public static void setTransferredData(String key, Object value) {
        transferredData.put(key, value);
    }

    private volatile MainSubpage currentSubpage;

    private final JLabel header = new JLabel("HEADER");
    private final List<MainSubpage> subpages = List.of(
            new LoginSubpage(),
            new SeasonSelectionSubpage(),
            new EventFilterSubpage(),
            new EventSelectionSubpage()
    );
    private final Runnable onEnd;

    public MainPage(Runnable onEnd) {
        this(0, onEnd);
    }

    public MainPage(int startPage, Runnable onEnd) {
        super();
        this.onEnd = onEnd;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        setLayout(null);
        setBackground(UIManager.getColor("background.subpage"));

        header.setFont(header.getFont().deriveFont(Font.BOLD));

        add(header);
        subpages.forEach((page) -> {
            if (page.lastButton() != null) {
                page.lastButton().addActionListener((a) -> {
                    if (!page.isFocusedPage()) {
                        return;
                    }
                    if (isFirstPage()) {
                        return;
                    }
                    prevPage();
                });
            }
            if (page.nextButton() != null) {
                page.nextButton().addActionListener((a) -> page.canMoveOn(() -> {
                    if (!page.isFocusedPage()) {
                        return;
                    }
                    if (isLastPage()) {
                        onEnd.run();
                        return;
                    }
                    nextPage();
                }));
            }
            add((Component) page);
        });
        setPage(subpages.get(startPage));
        add(new JButton());
    }

    private boolean isFirstPage() {
        return currentSubpage.equals(subpages.getFirst());
    }

    private boolean isLastPage() {
        return currentSubpage.equals(subpages.getLast());
    }

    private void nextPage() {
        setPage(subpages.get(subpages.indexOf(currentSubpage) + 1));
    }

    private void prevPage() {
        setPage(subpages.get(subpages.indexOf(currentSubpage) - 1));
    }

    private void setPage(MainSubpage page) {
        if (currentSubpage != null) {
            currentSubpage.setFocusedPage(false);
        }
        page.setFocusedPage(true);
        currentSubpage = page;
        revalidate();
    }

    @Override
    public void update() {
        if (!isVisible()) {
            return;
        }

        if (currentSubpage != null) {
            header.setText(currentSubpage.getHeader());
            currentSubpage.update();
        }
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setLocation(0, 0);
        header.setSize(getWidth(), (int)(getHeight()*0.1f));
        header.setFont(header.getFont().deriveFont(header.getWidth() * 0.048f));
    }
}

interface MainSubpage {
    default String getHeader() {
        return "Unnamed";
    }

    void update();

    void setFocusedPage(boolean focused);

    boolean isFocusedPage();

    JPanel getDisplayPanel();

    void canMoveOn(Runnable onSuccess);

    JButton nextButton();

    default JButton lastButton() {
        return null;
    };
}

class SubpageTemplate extends JPanel {
    protected final JPanel displayPanel;
    protected final Insets defaultInsets = new Insets((int)(SessionUtils.SCREEN_SIZE.getHeight()*0.001f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f), (int)(SessionUtils.SCREEN_SIZE.getHeight()*0.001f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f));

    private final JPanel loadingPanel = new JPanel(new GridBagLayout());
    private final JLabel loadingJlabel = new JLabel("Loading...");
    private Point loadingLocation = null;
    private Dimension loadingSize = null;

    protected float fontSize = 0;

    protected final KeyListener textFieldUnfocusor = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                requestFocusInWindow();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {}
    };

    public SubpageTemplate(LayoutManager layout) {
        super(null);

        setBackground(UIManager.getColor("invisible"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        loadingPanel.setBackground(UIManager.getColor("background.loading"));
        loadingJlabel.setFont(loadingJlabel.getFont().deriveFont(Font.BOLD));

        displayPanel = new JPanel(layout);
        displayPanel.setBackground(UIManager.getColor("invisible"));

        GridBagConstraints loadingConstraints = new GridBagConstraints();
        loadingConstraints.insets = defaultInsets;
        loadingPanel.add(loadingJlabel, loadingConstraints);
        loadingPanel.setVisible(false);

        add(loadingPanel);
        add(displayPanel);
    }

    public void addToDisplay(Component comp, Object constraints) {
        displayPanel.add(comp, constraints);
    }

    public Component addToDisplay(Component comp) {
        return displayPanel.add(comp);
    }

    public void templateUpdate(Runnable customTask) {
        Container parent = getParent();
        if (parent == null) {
            return;
        }
        setSize(parent.getWidth(), (int)(parent.getHeight()*0.9f));
        setLocation(0, (int)(parent.getHeight()*0.1f));

        if (!isVisible()) {
            return;
        }

        fontSize = getWidth()*0.05f;

        displayPanel.setSize(getSize());
        displayPanel.setLocation(-getX(), 0);

        loadingPanel.setSize(loadingSize == null ? displayPanel.getSize() : loadingSize);
        loadingPanel.setLocation(loadingLocation == null ? displayPanel.getLocation() : loadingLocation);

        loadingJlabel.setFont(loadingJlabel.getFont().deriveFont(fontSize*2));

        customTask.run();
    }

    protected void setLoading(boolean loading) {
        loadingPanel.setVisible(loading);
    }

    protected void setLoadingLocation(Point p) {
        loadingLocation = p;
    }

    protected void setLoadingSize(Dimension size) {
        loadingSize = size;
    }
}

class LoginSubpage extends SubpageTemplate implements MainSubpage {
    private static final String SECRET_FILE = "secret";
    private static final String ERROR_OPEN_REGISTRATION = "An error occurred. Please try again.";

    private final Logger LOGGER = LoggerFactory.getLogger(LoginSubpage.class);

    private final JLabel usernameHeader = new JLabel("Username"), tokenHeader = new JLabel("API Token"), rememberLabel = new JLabel("Remember me"), credentialErrorLabel = new JLabel("Please check your credentials.");

    private final JTextField usernameField = new JTextField(100);
    private final JPasswordField tokenField = new JPasswordField(500);
    private final JCheckBox rememberBox = new JCheckBox();

    private final JButton nextButton = new JButton("Login"), registerButton = new JButton("Register");

    private boolean focusedPage = false;

    public LoginSubpage() {
        super(new GridBagLayout());

        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        tokenField.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField.addKeyListener(textFieldUnfocusor);
        tokenField.addKeyListener(textFieldUnfocusor);

        usernameHeader.setFont(usernameHeader.getFont().deriveFont(Font.BOLD));
        tokenHeader.setFont(tokenHeader.getFont().deriveFont(Font.BOLD));
        registerButton.setFont(registerButton.getFont().deriveFont(Font.BOLD));

        rememberBox.setFocusPainted(false);
        rememberBox.setFocusable(false);

        credentialErrorLabel.setForeground(UIManager.getColor("errorColor"));
        credentialErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        credentialErrorLabel.setVisible(false);

        registerButton.setFocusPainted(false);
        registerButton.setFocusable(false);

        nextButton.setFocusPainted(false);
        nextButton.setFocusable(false);


        registerButton.addActionListener((a) -> {
            registerButton.setEnabled(false);
            try {
                SessionUtils.openLink(FRC.API_REGISTRATION);
            } catch (IOException e) {
                credentialErrorLabel.setText(ERROR_OPEN_REGISTRATION);
                credentialErrorLabel.setVisible(true);
                LOGGER.error("Could not open token registration page.", e);
            }
            registerButton.setEnabled(true);
        });

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.weightx = 1;
        constraints.weighty = 1.2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = defaultInsets;
        constraints.gridx = 1; constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        addToDisplay(usernameHeader, constraints);

        constraints.weighty = 1;
        constraints.gridy = 2;
        constraints.insets.top = 0;
        addToDisplay(usernameField, constraints);

        constraints.weighty = 1.2;
        constraints.gridy = 3;
        constraints.insets = defaultInsets;
        addToDisplay(tokenHeader, constraints);

        constraints.weighty = 1;
        constraints.gridy = 4;
        constraints.insets.top = 0;
        addToDisplay(tokenField, constraints);

        constraints.weightx = 0.1; constraints.weighty = 0.5;
        constraints.gridx = 1; constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(defaultInsets.top, defaultInsets.left, defaultInsets.bottom, 0);
        addToDisplay(rememberBox, constraints);

        constraints.weightx = 2; constraints.weighty = 0.5;
        constraints.gridx = 2; constraints.gridy = 5;
        constraints.insets = new Insets(defaultInsets.top, 0, defaultInsets.bottom, defaultInsets.right);
        addToDisplay(rememberLabel, constraints);

        constraints.weightx = 1; constraints.weighty = 1;
        constraints.gridx = 1; constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.insets = defaultInsets;
        addToDisplay(credentialErrorLabel, constraints);

        constraints.weighty = 1;
        constraints.gridy = 7;
        constraints.insets = new Insets(defaultInsets.top*2, defaultInsets.left*5, defaultInsets.bottom*2, defaultInsets.right*5);
        addToDisplay(nextButton, constraints);

        constraints.weighty = 1;
        constraints.gridy = 8;
        constraints.insets.bottom = defaultInsets.bottom*50;
        addToDisplay(registerButton, constraints);

        rememberBox.setSelected(false);
        loadCredentials();

        revalidate();
        repaint();
    }

    @Override
    public String getHeader() {
        return "Authentication";
    }

    @Override
    public void update() {
        templateUpdate(() -> {
            usernameHeader.setFont(usernameHeader.getFont().deriveFont(fontSize));
            usernameField.setFont(usernameField.getFont().deriveFont(fontSize*0.9f));
            tokenHeader.setFont(tokenHeader.getFont().deriveFont(fontSize));
            tokenField.setFont(tokenField.getFont().deriveFont(fontSize*0.9f));
            rememberLabel.setFont(rememberLabel.getFont().deriveFont(fontSize*0.6f));
            credentialErrorLabel.setFont(credentialErrorLabel.getFont().deriveFont(fontSize*0.8f));
            registerButton.setFont(registerButton.getFont().deriveFont(registerButton.getWidth()*0.075f));
            nextButton.setFont(registerButton.getFont());
        });
    }

    @Override
    public void setFocusedPage(boolean focused) {
        setVisible(focused);
        focusedPage = focused;
    }

    @Override
    public boolean isFocusedPage() {
        return focusedPage;
    }

    @Override
    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    private void saveCredentials(String user, String token) {
        new LoggedThread(getClass(), () -> {
            String credentials = Base64.getEncoder().encodeToString((user + ":" + token).getBytes()).replace("=", "");
            byte[] encrypted;
            try {
                encrypted = AES.encrypt(credentials);
            } catch (Exception e) {
                System.err.println("Could not encrypt user secrets.\n" + e);
                return;
            }

            try {
                File output = new File(SECRET_FILE);
                output.createNewFile();
                Files.write(output.toPath(), encrypted);
            } catch (IOException e) {
                System.err.println("Could not store encrypted user secrets.\n" + e);
            }
            Thread.currentThread().interrupt();
        }).start();
    }

    private void loadCredentials() {
        File input = new File(SECRET_FILE);
        if (!input.exists()) {
            return;
        }

        String decrypted;
        try {
            decrypted = AES.decrypt(Files.readAllBytes(input.toPath()));
        } catch (IOException e) {
            LOGGER.error("Could not read encrypted user secrets from file.", e);
            return;
        } catch (Exception e) {
            LOGGER.error("Could not decrypt user secrets.", e);
            return;
        }

        String base64Decoded;
        try {
            base64Decoded = new String(Base64.getDecoder().decode(AES.addPadding(decrypted)));
        } catch (Exception e) {
            LOGGER.error("Could not base64-decode user secrets.", e);
            return;
        }

        String[] splitted = base64Decoded.split(":");
        if (splitted.length != 2) {
            LOGGER.error("User secret length is invalid (x{})", splitted.length);
            return;
        }

        usernameField.setText(splitted[0]);
        tokenField.setText(splitted[1]);
        rememberBox.setSelected(true);
    }

    @Override
    public void canMoveOn(Runnable onSuccess) {
        nextButton.setEnabled(false);
        nextButton.setText("Checking...");

        String storedUsername = usernameField.getText();
        String storedPassword = String.valueOf(tokenField.getPassword());

        new LoggedThread(getClass(), () -> {
            try {
                int code;
                if (FRC.compareAuth(storedUsername, storedPassword)) {
                    code = 200;
                } else {
                    code = FRC.checkCredentials(storedUsername, storedPassword);
                }
                credentialErrorLabel.setVisible(code!=200);

                switch (code) {
                    case 0 : credentialErrorLabel.setText("Please check your internet connection."); break;
                    case 200:
                        FRC.setAuth(storedUsername, storedPassword);
                        if (rememberBox.isSelected()) {
                            saveCredentials(storedUsername, storedPassword);
                        } else {
                            File output = new File(SECRET_FILE);
                            if (output.exists()) {
                                output.delete();
                            }
                        }
                        onSuccess.run();
                        break;
                    case 401: credentialErrorLabel.setText("Invalid credentials."); break;
                    case 408 | 500: credentialErrorLabel.setText("Please try again."); break;
                    default: credentialErrorLabel.setText("Unknown error (" + code + ")"); break;
                }
            } catch (Exception e) {
                System.err.println("Could not authenticate user secrets.\n" + e);
            }

            nextButton.setEnabled(true);
            nextButton.setText("Login");
            Thread.currentThread().interrupt();
        }).start();
    }

    @Override
    public JButton nextButton() {
        return nextButton;
    }
}

class SeasonSelectionSubpage extends SubpageTemplate implements MainSubpage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonSelectionSubpage.class);

    private static final int[] whitelistedSeasons = {2024, 2023, 2022, 2021, 2020};

    private final JButton nextButton = new JButton("Next"), prevButton = new JButton("Back");

    private final Hashtable<Integer, String> loadedData = new Hashtable<>();

    private final JLabel seasonsHeader = new JLabel("Choose a season to view");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);
    private final JScrollPane scrollPane = new JScrollPane(list);

    private Thread loadingJob = null;

    private Integer selectedSeason = null;

    private boolean focusedPage = false;

    public SeasonSelectionSubpage() {
        super(new GridBagLayout());

        seasonsHeader.setFont(seasonsHeader.getFont().deriveFont(Font.BOLD));
        seasonsHeader.setHorizontalAlignment(SwingConstants.CENTER);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        list.addListSelectionListener((e) -> {
            if (!e.getValueIsAdjusting()) {
                return;
            }
            for(Map.Entry<Integer, String> entry : loadedData.entrySet()){
                if(entry.getValue().equals(list.getSelectedValue())){
                    selectedSeason = entry.getKey();
                    break;
                }
            }
            nextButton.setEnabled(true);
        });
        list.setFocusable(false);

        nextButton.setEnabled(false);
        prevButton.setFont(prevButton.getFont().deriveFont(Font.BOLD));
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 2; constraints.gridheight = 1;

        constraints.gridx = 1; constraints.gridy = 1;
        constraints.weightx = 1; constraints.weighty=0.2;
        constraints.insets = new Insets(defaultInsets.top/2, defaultInsets.left, defaultInsets.bottom/2, defaultInsets.right);
        addToDisplay(seasonsHeader, constraints);

        constraints.gridy = 2;
        constraints.weighty=1;
        constraints.insets = new Insets(defaultInsets.top, defaultInsets.left*3, defaultInsets.bottom, defaultInsets.right*3);
        addToDisplay(scrollPane, constraints);

        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weighty=0.2;
        constraints.insets = new Insets(defaultInsets.top*10, defaultInsets.left*2, defaultInsets.bottom*50, defaultInsets.right/3);
        addToDisplay(prevButton, constraints);

        constraints.gridx = 2; constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weighty=0.2;
        constraints.insets = new Insets(defaultInsets.top*10, defaultInsets.left/3, defaultInsets.bottom*50, defaultInsets.right*2);
        addToDisplay(nextButton, constraints);
    }

    private void loadData() {
        if (loadingJob != null && !loadingJob.isInterrupted()) {
            return;
        }
        listModel.removeAllElements();
        loadingJob = new LoggedThread(getClass(), () -> {
            setLoading(true);
            try {
                for (int whitelistedSeason : whitelistedSeasons) {
                    if (Thread.interrupted()) {
                        break;
                    }
                    String name = SeasonSummary.getSeasonName(whitelistedSeason).getParsed();
                    loadedData.put(whitelistedSeason, "[" + whitelistedSeason + "] " + name);
                    listModel.addElement(loadedData.get(whitelistedSeason));
                    list.revalidate();
                    list.repaint();
                }
            } catch (Exception e) {
                LOGGER.error("Could not load data.", e);
            }
            setLoading(false);
            Thread.currentThread().interrupt();
        });
        loadingJob.start();
    }

    @Override
    public String getHeader() {
        return "Season Selection";
    }

    @Override
    public void update() {
        templateUpdate(() -> {
            setLoadingLocation(scrollPane.getLocation());
            setLoadingSize(scrollPane.getSize());

            if (listModel.isEmpty()) {
                loadData();
            }

            seasonsHeader.setFont(seasonsHeader.getFont().deriveFont(fontSize*0.75f));

            list.setFont(list.getFont().deriveFont(fontSize*0.5f));
            list.setFixedCellWidth(scrollPane.getWidth());

            prevButton.setFont(prevButton.getFont().deriveFont(prevButton.getWidth()*0.1f));
            nextButton.setFont(prevButton.getFont());

        });
    }

    @Override
    public void setFocusedPage(boolean focused) {
        setVisible(focused);
        focusedPage = focused;
    }

    @Override
    public boolean isFocusedPage() {
        return focusedPage;
    }

    @Override
    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    @Override
    public void canMoveOn(Runnable onSuccess) {
        if (selectedSeason != null) {
            MainPage.setTransferredData("season", selectedSeason);
            onSuccess.run();
        }
    }

    @Override
    public JButton nextButton() {
        return nextButton;
    }

    @Override
    public JButton lastButton() {
        return prevButton;
    }
}

class EventFilterSubpage extends SubpageTemplate implements MainSubpage{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventFilterSubpage.class);

    private static class DistrictPanel extends JPanel {
        private final District district;
        private final JLabel header;
        private final JLabel codeHeader;
        private final JButton selectButton = new JButton("View");

        public DistrictPanel(District district) {
            super(new GridBagLayout());

            this.district = district;
            header = new JLabel(district.getName());
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            header.setHorizontalAlignment(SwingConstants.CENTER);

            Insets defaultInsets = new Insets((int)(SessionUtils.SCREEN_SIZE.getHeight()*0.001f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f), (int)(SessionUtils.SCREEN_SIZE.getHeight()*0.001f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f));

            setBackground(UIManager.getColor("invisible"));
            setDoubleBuffered(true);

            codeHeader = new JLabel("[%s]".formatted(district.getCode()));
            codeHeader.setHorizontalAlignment(SwingConstants.CENTER);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 1; constraints.gridy = 1;
            constraints.weighty = 1;
            constraints.insets = defaultInsets;
            add(header, constraints);

            constraints.gridy = 2;
            constraints.weighty = 0.5f;
            add(codeHeader, constraints);

            constraints.gridy = 3;
            constraints.weighty = 0.25f;
            add(selectButton, constraints);

            setVisible(true);
        }

        public void update() {
            if (getParent() == null) {
                return;
            }
            final float fontSize = getWidth()*0.075f;
            header.setFont(header.getFont().deriveFont(fontSize));
            codeHeader.setFont(header.getFont().deriveFont(fontSize));
            selectButton.setFont(header.getFont().deriveFont(fontSize));
        }

        public void addSelectionListener(ActionListener l) {
            selectButton.addActionListener((e) -> {
                MainPage.setTransferredData("district", district.getCode());
                MainPage.setTransferredData("district_name", district.getName());
                l.actionPerformed(e);
            });
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DistrictPanel panel) {
                return panel.district.equals(this.district);
            }
            return false;
        }
    }

    private static class DistrictScrollPane extends JScrollPane {
        private final JPanel contentPane = new JPanel(null);
        private final List<DistrictPanel> districtPanels = Collections.synchronizedList(new ArrayList<>());
        private final List<ActionListener> selectionListeners = Collections.synchronizedList(new ArrayList<>());

        public DistrictScrollPane() {
            setDoubleBuffered(true);
            setBorder(null);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

//            contentPane.setBackground(UIManager.getColor("invisible"));
            contentPane.setDoubleBuffered(true);

            viewport.setLayout(null);
            viewport.setView(contentPane);
        }

        private int calculateDistrictPanelWidth() {
            return (int)(getWidth()*0.6f);
        }

        public void addDistrict(District district) {
            DistrictPanel panel = new DistrictPanel(district);
            districtPanels.add(panel);
            contentPane.add(panel);
            for (ActionListener l : selectionListeners) {
                panel.addSelectionListener(l);
            }
        }

        public void clear() {
            contentPane.removeAll();
            districtPanels.clear();
        }

        public void updateContentPane() {
            contentPane.setSize(districtPanels.size()*calculateDistrictPanelWidth(), getHorizontalScrollBar().isVisible() ? getHeight() - getHorizontalScrollBar().getHeight() : getHeight());
            contentPane.setPreferredSize(contentPane.getSize());

            AtomicInteger index = new AtomicInteger();

            districtPanels.forEach((panel) -> {
                panel.setSize(calculateDistrictPanelWidth(), contentPane.getHeight());
                panel.setLocation(index.get() * calculateDistrictPanelWidth(), 0);
                panel.update();
                index.addAndGet(1);
            });
        }

        public void addSelectionListener(ActionListener l) {
            selectionListeners.add(l);
            districtPanels.forEach((panel) -> panel.addSelectionListener(l));
        }
    }

    private final JLabel districtHeader = new JLabel("Available Districts");

    private final DistrictScrollPane scrollPane = new DistrictScrollPane();

    private final JButton prevButton = new JButton("Back"), dummyNextButton = new JButton("dummy") {
        @Override
        public void addActionListener(ActionListener l) {
            scrollPane.addSelectionListener(l);
        }
    };

    private boolean focusedPage = false;

    private LoggedThread loadingJob = null;

    public EventFilterSubpage() {
        super(new GridBagLayout());

        districtHeader.setFont(districtHeader.getFont().deriveFont(Font.BOLD));
        districtHeader.setHorizontalAlignment(SwingConstants.CENTER);

        prevButton.setFont(prevButton.getFont().deriveFont(Font.BOLD));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 2; constraints.gridheight = 1;

        constraints.gridx = 1; constraints.gridy = 1;
        constraints.weightx = 1; constraints.weighty = 0.2;
        constraints.insets = new Insets(defaultInsets.top, defaultInsets.left, defaultInsets.bottom/2, defaultInsets.right);
        addToDisplay(districtHeader, constraints);

        constraints.gridy = 2;
        constraints.weighty = 2;
        constraints.insets = defaultInsets;
        addToDisplay(scrollPane, constraints);

        constraints.gridy = 3;
        constraints.weighty = 0.1;
        constraints.insets = new Insets(defaultInsets.top, defaultInsets.left*10, defaultInsets.bottom*4, defaultInsets.right*10);
        addToDisplay(prevButton, constraints);

        revalidate();
    }

    public void load() {
        Object rawSeason = MainPage.getTransferredData("season");
        if (rawSeason instanceof Integer selectedSeason) {
            if (loadingJob != null && !loadingJob.isInterrupted()) {
                LOGGER.error("Cannot create new loading job. Currently handling another job.");
                return;
            }

            scrollPane.clear();

            loadingJob = new LoggedThread(getClass(), () -> {
                setLoading(true);

                try {
                    ParsedTuple<List<District>> districts = SeasonDistricts.getDistricts(selectedSeason);
                    districts.getParsed().forEach(scrollPane::addDistrict);
                } catch (Exception e) {
                    LOGGER.error("Could not load data.", e);
                }

                setLoading(false);
                loadingJob.interrupt();
            });
            loadingJob.start();

        } else {
            LOGGER.error("Could not get transferred season data ({} : {})", rawSeason, rawSeason.getClass());
        }
    }

    @Override
    protected void setLoading(boolean loading) {
        prevButton.setEnabled(!loading);
        super.setLoading(loading);
    }

    @Override
    public String getHeader() {
        return "District Selection";
    }

    @Override
    public void update() {
        templateUpdate(() -> {
            districtHeader.setFont(districtHeader.getFont().deriveFont(fontSize));
            scrollPane.updateContentPane();
            prevButton.setFont(prevButton.getFont().deriveFont(prevButton.getWidth()*0.1f));
            setLoadingLocation(scrollPane.getLocation());
            setLoadingSize(scrollPane.getSize());
        });
    }

    @Override
    public void setFocusedPage(boolean focused) {
        setVisible(focused);
        focusedPage = focused;
        if (focused) {
            load();
        }
    }

    @Override
    public boolean isFocusedPage() {
        return focusedPage;
    }

    @Override
    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    @Override
    public void canMoveOn(Runnable onSuccess) {
        onSuccess.run();
    }

    @Override
    public JButton lastButton() {
        return prevButton;
    }

    @Override
    public JButton nextButton() {
        return dummyNextButton;
    }
}

class EventSelectionSubpage extends SubpageTemplate implements MainSubpage {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSelectionSubpage.class);

    private final JLabel header = new JLabel("Competitions");
    private final JButton prevButton = new JButton("Back"), viewButton = new JButton("View");

    private final Hashtable<String, String> loadedData = new Hashtable<>();
    private final Hashtable<String, Event> loadedRawData = new Hashtable<>();

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);

    private final JScrollPane scrollPane = new JScrollPane(list);

    private String selectedCompetition = null;

    private LoggedThread loadingJob = null;

    private boolean focusedPage = false;

    public EventSelectionSubpage() {
        super(new GridBagLayout());

        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setHorizontalAlignment(SwingConstants.CENTER);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        list.setFocusable(false);
        list.addListSelectionListener((e) -> {
            if (!e.getValueIsAdjusting()) {
                return;
            }
            for(Map.Entry<String, String> entry : loadedData.entrySet()){
                if(entry.getValue().equals(list.getSelectedValue())){
                    selectedCompetition = entry.getKey();
                    break;
                }
            }
            viewButton.setEnabled(true);
        });

        viewButton.setEnabled(false);
        prevButton.setFont(prevButton.getFont().deriveFont(Font.BOLD));
        viewButton.setFont(viewButton.getFont().deriveFont(Font.BOLD));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 2; constraints.gridheight = 1;

        constraints.gridx = 1; constraints.gridy = 1;
        constraints.weightx = 1; constraints.weighty=0.2;
        constraints.insets = new Insets(defaultInsets.top/2, defaultInsets.left, defaultInsets.bottom/2, defaultInsets.right);
        addToDisplay(header, constraints);

        constraints.gridy = 2;
        constraints.weighty=1;
        constraints.insets = new Insets(defaultInsets.top, defaultInsets.left*3, defaultInsets.bottom, defaultInsets.right*3);
        addToDisplay(scrollPane, constraints);

        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weighty=0.2;
        constraints.insets = new Insets(defaultInsets.top*10, defaultInsets.left*2, defaultInsets.bottom*50, defaultInsets.right/3);
        addToDisplay(prevButton, constraints);

        constraints.gridx = 2; constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weighty=0.2;
        constraints.insets = new Insets(defaultInsets.top*10, defaultInsets.left/3, defaultInsets.bottom*50, defaultInsets.right*2);
        addToDisplay(viewButton, constraints);
    }

    private void load() {
        if (MainPage.getTransferredData("district") instanceof String districtCode && MainPage.getTransferredData("season") instanceof Integer season) {
            if (loadingJob != null && !loadingJob.isInterrupted()) {
                LOGGER.error("Cannot create new loading job. Currently handling another job.");
                return;
            }
            listModel.removeAllElements();
            loadedData.clear();
            loadingJob = new LoggedThread(getClass(), () -> {
                setLoading(true);

                try {
                    ParsedTuple<List<Event>> districtEvents = DistrictEvents.getEvents(season, districtCode);
                    for (Event event : districtEvents.getParsed()) {
                        loadedData.put(event.getCode(), event.getShortenedName());
                        loadedRawData.put(event.getCode(), event);
                    }
                    listModel.addAll(loadedData.values());
                } catch (Exception e) {
                    LOGGER.error("Could not load data.", e);
                }

                setLoading(false);
                Thread.currentThread().interrupt();
            });
            loadingJob.start();
        } else {
            LOGGER.error("Could not get transferred data.");
        }
    }

    @Override
    public void update() {
        templateUpdate(() -> {
            header.setText("Events @ %s (%s)".formatted(String.valueOf(MainPage.getTransferredData("district_name")), String.valueOf(MainPage.getTransferredData("season"))));
            header.setFont(header.getFont().deriveFont(fontSize*0.75f));

            list.setFont(list.getFont().deriveFont(fontSize*0.5f));
            list.setFixedCellWidth(scrollPane.getWidth());


            setLoadingSize(scrollPane.getSize());
            setLoadingLocation(scrollPane.getLocation());

            prevButton.setFont(prevButton.getFont().deriveFont(prevButton.getWidth()*0.1f));
            viewButton.setFont(prevButton.getFont());
        });
    }

    @Override
    protected void setLoading(boolean loading) {
        prevButton.setEnabled(!loading);
        super.setLoading(loading);
    }

    @Override
    public void setFocusedPage(boolean focused) {
        setVisible(focused);
        focusedPage = focused;
        if (focused) {
            load();
        }
    }

    @Override
    public boolean isFocusedPage() {
        return focusedPage;
    }

    @Override
    public JPanel getDisplayPanel() {
        return null;
    }

    @Override
    public void canMoveOn(Runnable onSuccess) {
        if (selectedCompetition != null) {
            MainPage.setTransferredData("event", loadedRawData.get(selectedCompetition));
            onSuccess.run();
        }
    }

    @Override
    public JButton nextButton() {
        return viewButton;
    }

    @Override
    public JButton lastButton() {
        return prevButton;
    }

    @Override
    public String getHeader() {
        return "Event Selection";
    }
}