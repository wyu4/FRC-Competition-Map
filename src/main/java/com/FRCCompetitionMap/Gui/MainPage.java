package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPage extends JPanel implements SessionPage {
    private MainSubpage currentSubpage;

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
            page.nextButton().addActionListener((a) -> {
                if (isLastPage()) {
                    onEnd.run();
                    return;
                }
                nextPage();
            });
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

    boolean canMoveOn();

    JButton nextButton();

    default JButton lastButton() {
        return null;
    };
}

class LoginSubpage extends JPanel implements MainSubpage {
    private static final String ERROR_INVALID_CREDENTIALS = "Please check your credentials.";
    private static final String ERROR_OPEN_REGISTRATION = "An error occurred. Please try again.";

    private final Logger LOGGER = LoggerFactory.getLogger(LoginSubpage.class);

    private final JLabel usernameHeader = new JLabel("Username"), tokenHeader = new JLabel("API Token"), rememberLabel = new JLabel("Remember me"), credentialErrorLabel = new JLabel("Please check your credentials.");

    private final JTextField usernameField = new JTextField(100);
    private final JPasswordField tokenField = new JPasswordField(500);
    private final JCheckBox rememberBox = new JCheckBox();

    private final JButton nextButton = new JButton("Login"), registerButton = new JButton("Get credentials");

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

        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        tokenField.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField.addKeyListener(unfocuser);
        tokenField.addKeyListener(unfocuser);

        usernameHeader.setFont(usernameHeader.getFont().deriveFont(Font.BOLD));
        tokenHeader.setFont(tokenHeader.getFont().deriveFont(Font.BOLD));
        registerButton.setFont(registerButton.getFont().deriveFont(Font.BOLD));

        credentialErrorLabel.setForeground(UIManager.getColor("errorColor"));
        credentialErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        credentialErrorLabel.setVisible(false);

        registerButton.setFocusPainted(false);

        registerButton.addActionListener((a) -> {
            try {
                SessionUtils.openLink(FRC.API_REGISTRATION);
            } catch (IOException e) {
                credentialErrorLabel.setText(ERROR_OPEN_REGISTRATION);
                credentialErrorLabel.setVisible(true);
                LOGGER.error("Could not open token registration page.", e);
            }
        });

        usernameHeader.setDoubleBuffered(true);
        usernameField.setDoubleBuffered(true);
        tokenHeader.setDoubleBuffered(true);
        tokenField.setDoubleBuffered(true);
        rememberBox.setDoubleBuffered(true);
        rememberLabel.setDoubleBuffered(true);
        credentialErrorLabel.setDoubleBuffered(true);
        registerButton.setDoubleBuffered(true);

        add(usernameHeader);
        add(usernameField);
        add(tokenHeader);
        add(tokenField);
        add(rememberBox);
        add(rememberLabel);
        add(credentialErrorLabel);
        add(registerButton);

        revalidate();
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

        float fontSize = getWidth()*0.05f;

        usernameHeader.setSize((int)(getWidth() * 0.9f), (int)(getHeight() * 0.1f));
        usernameHeader.setLocation((getWidth()/2) - (usernameHeader.getWidth()/2), (int)(getHeight()*0.1f));
        usernameHeader.setFont(usernameHeader.getFont().deriveFont(fontSize));

        usernameField.setSize(usernameHeader.getSize());
        usernameField.setLocation(usernameHeader.getX(), usernameHeader.getY() + usernameHeader.getHeight());
        usernameField.setFont(usernameField.getFont().deriveFont(fontSize*0.9f));

        tokenHeader.setSize(usernameField.getSize());
        tokenHeader.setLocation(usernameHeader.getX(), (int)(usernameField.getY() + usernameField.getHeight()*1.5f));
        tokenHeader.setFont(tokenHeader.getFont().deriveFont(fontSize));

        tokenField.setSize(usernameField.getSize());
        tokenField.setLocation(tokenHeader.getX(), tokenHeader.getY() + tokenHeader.getHeight());
        tokenField.setFont(tokenField.getFont().deriveFont(fontSize*0.9f));

        rememberBox.setSize(usernameHeader.getHeight()/2, usernameHeader.getHeight()/2);
        rememberBox.setLocation(tokenField.getX(), tokenField.getY() + tokenField.getHeight());

        rememberLabel.setSize(usernameHeader.getWidth() - (rememberBox.getWidth()+rememberBox.getX()), rememberBox.getHeight());
        rememberLabel.setLocation((int)((rememberBox.getX()+rememberBox.getWidth())*1.25f), rememberBox.getY());
        rememberLabel.setFont(rememberLabel.getFont().deriveFont(fontSize*0.6f));

        credentialErrorLabel.setSize(usernameHeader.getSize());
        credentialErrorLabel.setLocation(rememberBox.getX(), (int)(rememberLabel.getY() + rememberLabel.getHeight()*1.5f));
        credentialErrorLabel.setFont(credentialErrorLabel.getFont().deriveFont(fontSize*0.8f));

        registerButton.setSize(usernameHeader.getWidth()/2, usernameHeader.getHeight());
        registerButton.setLocation(credentialErrorLabel.getX()+usernameHeader.getWidth()/4, (int)(credentialErrorLabel.getY() + credentialErrorLabel.getHeight()*1.2f));
        registerButton.setFont(registerButton.getFont().deriveFont(registerButton.getWidth()*0.1f));
    }

    @Override
    public boolean canMoveOn() {
        FRC.setAuth(usernameField.getText(), String.valueOf(tokenField.getPassword()));
        boolean correctCredentials = FRC.checkCredentials();

        if (correctCredentials) {
            return true;
        }

        credentialErrorLabel.setVisible(true);
        credentialErrorLabel.setText(ERROR_INVALID_CREDENTIALS);
        return false;
    }

    @Override
    public JButton nextButton() {
        return nextButton;
    }
}