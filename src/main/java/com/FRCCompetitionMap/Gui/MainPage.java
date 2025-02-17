package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Encryption.AES;
import com.FRCCompetitionMap.Gui.CustomComponents.RoundedPanel;
import com.FRCCompetitionMap.Requests.FRC.FRC;
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

public class MainPage extends RoundedPanel implements SessionComponents {
    private volatile MainSubpage currentSubpage;

    private final JLabel header = new JLabel("HEADER");
    private final List<MainSubpage> subpages = List.of(
            new LoginSubpage(),
            new SeasonSelectionSubpage()
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

        setBackground(UIManager.getColor("invisible"));

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

        setBackground(UIManager.getColor("invisible"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

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
            for (int whitelistedSeason : whitelistedSeasons) {
                if (Thread.interrupted()) {
                    break;
                }
                String name = SeasonSummary.getSeasonName(whitelistedSeason);
                loadedData.put(whitelistedSeason, "[" + whitelistedSeason + "] " + name);
                listModel.addElement(loadedData.get(whitelistedSeason));
            }
            setLoading(false);
            Thread.currentThread().interrupt();
        });
        loadingJob.start();
    }

    @Override
    public String getHeader() {
        return "Season";
    }

    @Override
    public void update() {
        templateUpdate(() -> {
            setLoadingLocation(scrollPane.getLocation());
            setLoadingSize(scrollPane.getSize());

            if (listModel.isEmpty()) {
                loadData();
                return;
            }

            seasonsHeader.setFont(seasonsHeader.getFont().deriveFont(fontSize*0.75f));

            list.setFont(list.getFont().deriveFont(fontSize/2));
            list.setFixedCellWidth(scrollPane.getWidth());

            prevButton.setFont(prevButton.getFont().deriveFont(prevButton.getWidth()*0.1f));
            nextButton.setFont(prevButton.getFont());

        });
    }

    public Integer getSelectedSeason() {
        return selectedSeason;
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
        System.out.println("User chose " + selectedSeason);
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

class DistrictSelectionSubpage extends SubpageTemplate implements MainSubpage{
    public DistrictSelectionSubpage() {
        super(new GridBagLayout());
    }

    @Override
    public String getHeader() {
        return "District";
    }

    @Override
    public void update() {

    }

    @Override
    public void setFocusedPage(boolean focused) {

    }

    @Override
    public boolean isFocusedPage() {
        return false;
    }

    @Override
    public JPanel getDisplayPanel() {
        return null;
    }

    @Override
    public void canMoveOn(Runnable onSuccess) {

    }

    @Override
    public JButton nextButton() {
        return null;
    }
}