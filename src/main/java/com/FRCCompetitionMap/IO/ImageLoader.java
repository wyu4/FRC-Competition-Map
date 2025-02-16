package com.FRCCompetitionMap.IO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);

    public static final String FRC_LOGO = "FRC.png";

    public static BufferedImage loadSafe(String path) {
        try {
            return load(path);
        } catch (IOException e) {
            LOGGER.error("Could not load image {}.", path, e);
            return new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        }
    }

    public static BufferedImage load(String path) throws IOException {
        InputStream stream = ImageLoader.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("Image not found: " + path);
        }
        return ImageIO.read(stream);
    }
}
