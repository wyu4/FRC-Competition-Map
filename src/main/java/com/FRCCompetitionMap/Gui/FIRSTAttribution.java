package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Gui.CustomComponents.SmartImageIcon;
import com.FRCCompetitionMap.IO.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FIRSTAttribution extends JPanel implements SessionPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(FIRSTAttribution.class);

    private final JLabel poweredLabel = new JLabel("Powered by");
    private final JLabel FIRSTLabel = new JLabel("[FIRST]"); // In case we can't load the image for some reason.
    private SmartImageIcon icon;

    public FIRSTAttribution() {
        super(null);
        setBackground(UIManager.getColor("invisible"));

        try {
            BufferedImage logo = ImageLoader.load(ImageLoader.FRC_LOGO);
            icon = new SmartImageIcon(logo);
        } catch (IOException ignore) {}

        if (icon != null) {
            icon.setMode(SmartImageIcon.PaintMode.RATIO);
            FIRSTLabel.setText("");
            FIRSTLabel.setIcon(icon);
        }

        poweredLabel.setHorizontalAlignment(SwingConstants.CENTER);
        poweredLabel.setFont(poweredLabel.getFont().deriveFont(Font.BOLD));

        add(poweredLabel);
        add(FIRSTLabel);

        setVisible(true);
        revalidate();
    }

    @Override
    public void update() {
        final float fontSize = getWidth() * 0.07f;

        poweredLabel.setSize(getWidth()/2, getHeight());
        poweredLabel.setLocation(0, 0);
        poweredLabel.setFont(poweredLabel.getFont().deriveFont(fontSize));

        FIRSTLabel.setSize(poweredLabel.getSize());
        FIRSTLabel.setLocation(poweredLabel.getWidth(), 0);
        if (icon == null) {
            FIRSTLabel.setFont(FIRSTLabel.getFont().deriveFont(fontSize));
        }
    }
}
