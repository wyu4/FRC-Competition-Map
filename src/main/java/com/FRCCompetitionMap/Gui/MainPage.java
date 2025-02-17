package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Encryption.AES;
import com.FRCCompetitionMap.Gui.CustomComponents.RoundedPanel;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
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
        super();
        this.onEnd = onEnd;

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
        setPage(subpages.getFirst());
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

    public SubpageTemplate(LayoutManager layout) {
        super(null);
        displayPanel = new JPanel(layout);
        displayPanel.setBackground(UIManager.getColor("invisible"));
        super.add(displayPanel);
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

        displayPanel.setSize(getSize());
        displayPanel.setLocation(0, 0);

        customTask.run();
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

    private boolean focusedPage;

    private final KeyListener unfocuser = new KeyListener() {
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

    public LoginSubpage() {
        super(new GridBagLayout());
        focusedPage = false;

        setBackground(UIManager.getColor("invisible"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        tokenField.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField.addKeyListener(unfocuser);
        tokenField.addKeyListener(unfocuser);

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
        Insets defaultInsets = new Insets((int)(SessionUtils.SCREEN_SIZE.getHeight()*0.001f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f), (int)(SessionUtils.SCREEN_SIZE.getHeight()*0.001f), (int)(SessionUtils.SCREEN_SIZE.getWidth()*0.01f));

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
        addToDisplay(usernameField, constraints);

        constraints.weighty = 1.2;
        constraints.gridy = 3;
        addToDisplay(tokenHeader, constraints);

        constraints.weighty = 1;
        constraints.gridy = 4;
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
            final float fontSize = getWidth()*0.05f;

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
        new Thread(() -> {
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
                System.out.println("Encrypted user secrets.");
            } catch (IOException e) {
                System.err.println("Could not store encrypted user secrets.\n" + e);
            }
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

        FRC.setAuth(storedUsername, storedPassword);
        new Thread(() -> {
            try {
                Integer code = FRC.checkCredentials();
                credentialErrorLabel.setVisible(code!=200);

                switch (code) {
                    case 0 : credentialErrorLabel.setText("Please check your internet connection."); break;
                    case 200:
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

        }).start();
    }

    @Override
    public JButton nextButton() {
        return nextButton;
    }
}

class SeasonSelectionSubpage extends SubpageTemplate implements MainSubpage {
    private final JButton nextButton = new JButton("Next"), prevButton = new JButton("Back");


    private boolean focusedPage;

    public SeasonSelectionSubpage() {
        super(null);
        focusedPage = false;

        setBackground(UIManager.getColor("invisible"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    @Override
    public String getHeader() {
        return "Season";
    }

    @Override
    public void update() {
        templateUpdate(() -> {

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