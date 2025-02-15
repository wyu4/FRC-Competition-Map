package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Requests.Callbacks.BooleanCallback;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.formdev.flatlaf.FlatClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainPage extends JPanel implements SessionPage {
    private volatile MainSubpage currentSubpage;

    private final JLabel header = new JLabel("HEADER");
    private final List<MainSubpage> subpages = List.of(
            new LoginSubpage()
    );
    private final Runnable onEnd;

    public MainPage(Runnable onEnd) {
        super(null);
        this.onEnd = onEnd;

        putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $subpage.background");

        header.setFont(header.getFont().deriveFont(Font.BOLD));

        add(header);
        subpages.forEach((page) -> {
            if (page.lastButton() != null) {
                page.lastButton().addActionListener((a) -> {
                    if (isFirstPage()) {
                        return;
                    }
                    prevPage();
                });
            }
            page.nextButton().addActionListener((a) -> page.canMoveOn(() -> {
                if (isLastPage()) {
                    onEnd.run();
                    return;
                }
                nextPage();
            }));
            add((Component) page);
        });
        currentSubpage = subpages.getFirst();
        add(new JButton());

//        putClientProperty(FlatClientProperties.OUTLINE, "");
    }

    private boolean isFirstPage() {
        return currentSubpage.equals(subpages.getFirst());
    }

    private boolean isLastPage() {
        return currentSubpage.equals(subpages.getLast());
    }

    private void nextPage() {
        currentSubpage = subpages.get(subpages.indexOf(currentSubpage) + 1);
        revalidate();
    }

    private void prevPage() {
        currentSubpage = subpages.get(subpages.indexOf(currentSubpage) - 1);
        revalidate();
    }

    @Override
    public void update() {
        if (!isVisible()) {
            return;
        }

        Container parent = getParent();
        if (parent == null) {
            return;
        }
        setSize(parent.getWidth()/4, parent.getWidth()/4);
        setLocation(SessionUtils.calculateCenterLocation(parent, this));

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

    void canMoveOn(Runnable onSuccess);

    JButton nextButton();

    default JButton lastButton() {
        return null;
    };
}

class LoginSubpage extends JPanel implements MainSubpage {
    private static final String ERROR_INVALID_CREDENTIALS = "Please check your credentials.";
    private static final String ERROR_OPEN_REGISTRATION = "An error occurred. Please try again.";

    private final JPanel displayPanel = new JPanel(new GridBagLayout());

    private final Logger LOGGER = LoggerFactory.getLogger(LoginSubpage.class);

    private final JLabel usernameHeader = new JLabel("Username"), tokenHeader = new JLabel("API Token"), rememberLabel = new JLabel("Remember me"), credentialErrorLabel = new JLabel("Please check your credentials.");

    private final JTextField usernameField = new JTextField(100);
    private final JPasswordField tokenField = new JPasswordField(500);
    private final JCheckBox rememberBox = new JCheckBox();

    private final JButton nextButton = new JButton("Login"), registerButton = new JButton("Register");

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
        super(null);
        setBackground(new Color(0, 0, 0, 0));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        displayPanel.setBackground(UIManager.getColor("invisible"));

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
        displayPanel.add(usernameHeader, constraints);

        constraints.weighty = 1;
        constraints.gridy = 2;
        displayPanel.add(usernameField, constraints);

        constraints.weighty = 1.2;
        constraints.gridy = 3;
        displayPanel.add(tokenHeader, constraints);

        constraints.weighty = 1;
        constraints.gridy = 4;
        displayPanel.add(tokenField, constraints);

        constraints.weightx = 0.1; constraints.weighty = 0.5;
        constraints.gridx = 1; constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(defaultInsets.top, defaultInsets.left, defaultInsets.bottom, 0);
        displayPanel.add(rememberBox, constraints);

        constraints.weightx = 2; constraints.weighty = 0.5;
        constraints.gridx = 2; constraints.gridy = 5;
        constraints.insets = new Insets(defaultInsets.top, 0, defaultInsets.bottom, defaultInsets.right);
        displayPanel.add(rememberLabel, constraints);

        constraints.weightx = 1; constraints.weighty = 1;
        constraints.gridx = 1; constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.insets = defaultInsets;
        displayPanel.add(credentialErrorLabel, constraints);

        constraints.weighty = 1;
        constraints.gridy = 7;
        constraints.insets = new Insets(defaultInsets.top*2, defaultInsets.left*5, defaultInsets.bottom*2, defaultInsets.right*5);
        displayPanel.add(nextButton, constraints);

        constraints.weighty = 1;
        constraints.gridy = 8;
        constraints.insets.bottom = defaultInsets.bottom*50;
        displayPanel.add(registerButton, constraints);

        add(displayPanel);

        revalidate();
        repaint();
    }

    @Override
    public String getHeader() {
        return "Authentication";
    }

    @Override
    public void update() {
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

        final float fontSize = getWidth()*0.05f;

        usernameHeader.setFont(usernameHeader.getFont().deriveFont(fontSize));
        usernameField.setFont(usernameField.getFont().deriveFont(fontSize*0.9f));
        tokenHeader.setFont(tokenHeader.getFont().deriveFont(fontSize));
        tokenField.setFont(tokenField.getFont().deriveFont(fontSize*0.9f));
        rememberLabel.setFont(rememberLabel.getFont().deriveFont(fontSize*0.6f));
        credentialErrorLabel.setFont(credentialErrorLabel.getFont().deriveFont(fontSize*0.8f));
        registerButton.setFont(registerButton.getFont().deriveFont(registerButton.getWidth()*0.075f));
        nextButton.setFont(registerButton.getFont());
    }

    @Override
    public void canMoveOn(Runnable onSuccess) {
        nextButton.setEnabled(false);
        nextButton.setText("Checking...");

        FRC.setAuth(usernameField.getText(), String.valueOf(tokenField.getPassword()));
        new Thread(() -> {

            Integer code = FRC.checkCredentials();
            credentialErrorLabel.setVisible(code!=200);

            nextButton.setEnabled(true);
            nextButton.setText("Login");

            switch (code) {
                case 0 : credentialErrorLabel.setText("Please check your internet connection."); break;
                case 200: onSuccess.run(); break;
                case 401: credentialErrorLabel.setText("Invalid credentials."); break;
                case 408 | 500: credentialErrorLabel.setText("Please try again."); break;
                default: credentialErrorLabel.setText("Unknown error (" + code + ")"); break;
            }

//            if (!result) {
//                credentialErrorLabel.setText(ERROR_INVALID_CREDENTIALS);
//                credentialErrorLabel.setForeground(UIManager.getColor("errorColor"));
//            }


        }).start();
    }

    @Override
    public JButton nextButton() {
        return nextButton;
    }
}