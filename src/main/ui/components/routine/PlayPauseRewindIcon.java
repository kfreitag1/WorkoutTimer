package ui.components.routine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Represents a high resolution play, pause, or rewind icon of a fixed height
public class PlayPauseRewindIcon implements Icon {
    // Represents the type of icon
    public enum Type {
        PLAY("./data/assets/play.png"),
        PAUSE("./data/assets/pause.png"),
        REWIND("./data/assets/rewind.png");

        private final String iconImageFilePath;

        private static final BufferedImage PLAY_IMAGE;
        private static final BufferedImage PAUSE_IMAGE;
        private static final BufferedImage REWIND_IMAGE;

        // EFFECTS: Private constructor to make an icon type form an image filepath
        Type(String iconImageFilePath) {
            this.iconImageFilePath = iconImageFilePath;
        }

        static {
            try {
                // Preload all icon files
                PLAY_IMAGE = ImageIO.read(new File(Type.PLAY.iconImageFilePath));
                PAUSE_IMAGE = ImageIO.read(new File(Type.PAUSE.iconImageFilePath));
                REWIND_IMAGE = ImageIO.read(new File(Type.REWIND.iconImageFilePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // EFFECTS: Returns the cached image for the current icon type
        public BufferedImage getBufferedImage() {
            switch (this) {
                case PLAY:
                    return PLAY_IMAGE;
                case PAUSE:
                    return PAUSE_IMAGE;
                case REWIND:
                    return REWIND_IMAGE;
            }
            throw new IllegalStateException("Invalid icon type.");
        }
    }

    final BufferedImage image;
    final int height;
    final int width;

    // REQUIRES: height > 0
    // EFFECTS: Constructs a new play, pause, or rewind icon
    public PlayPauseRewindIcon(Type type, int height) {
        image = type.getBufferedImage();
        this.height = height;
        this.width = scaledWidth(image, height);
    }

    // REQUIRES: height > 0
    // EFFECTS: Scales the provided image to the given height, maintaining the appropriate
    //          aspect ratio of the image.
    private BufferedImage resizedImage(BufferedImage image, int height) {
        int width = scaledWidth(image, height);

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return scaledImage;
    }

    // REQUIRES: height > 0
    // EFFECTS: Returns the width of the given image if scaled down to the given height
    private int scaledWidth(BufferedImage image, int height) {
        double aspectRatio = ((double) image.getHeight()) / image.getWidth();
        return (int) Math.round(height / aspectRatio);
    }

    // MODIFIES: g
    // EFFECTS: Just draws the icon, with a lot of extra work to make sure it looks good on
    //          displays with a high resolution...
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        // Keep track of previous graphics object to reset once finished
        // (since this is what people on the internet said you needed to do)
        Graphics previousGraphics = g.create();
        Graphics2D draw = (Graphics2D) g;

        // DPI scaling factor will be either the X or Y component of this transformation matrix
        double dpiScaling = draw.getTransform().getScaleX();

        // Set the transformation matrix to reverse the scaling so that we are drawing
        // exact pixels when we draw the icon image.
        AffineTransform scaledTransform = new AffineTransform();
        scaledTransform.concatenate(draw.getTransform());
        scaledTransform.scale(1 / dpiScaling, 1 / dpiScaling);
        draw.setTransform(scaledTransform);

        // Draw the image, scaled by the DPI scaling factor
        draw.drawImage(resizedImage(image, (int) Math.round(height * dpiScaling)), null,
                (int) Math.round(x * dpiScaling), (int) Math.round(y * dpiScaling));

        // Reset the graphics object
        draw.dispose();
        g = previousGraphics;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
