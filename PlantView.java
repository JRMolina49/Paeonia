import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Phone-inspired Swing view for plant identification and the demo shopping flow.
 */
public class PlantView extends JPanel
{
    private static final Color BEIGE = new Color(0xF4F0E7);
    private static final Color CARD = Color.WHITE;
    private static final Color SOFT_CARD = new Color(0xF8F7F2);
    private static final Color MOSS = new Color(0x60734E);
    private static final Color MOSS_DARK = new Color(0x445638);
    private static final Color INK = new Color(0x23251F);
    private static final Color MUTED = new Color(0x74776E);
    private static final Color OUTLINE = new Color(0xE2DED3);
    private static final HttpClient IMAGE_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    private final JButton selectImageButton;
    private final JButton checkoutButton;
    private final JLabel statusLabel;
    private final JLabel uploadedPhotoLabel;
    private final JPanel uploadedPhotoPanel;
    private final JPanel plantDetailsPanel;
    private final JPanel shoppingPanel;
    private final JPanel cartPanel;
    private final JPanel contentPanel;
    private final Map<String, ImageIcon> referenceImageCache = new HashMap<>();
    private final Set<String> referenceImagesLoading = new HashSet<>();
    private final Map<String, List<JLabel>> referenceImageLabels = new HashMap<>();

    private PlantController controller;

    public PlantView()
    {
        setLayout(new BorderLayout(0, 14));
        setBackground(BEIGE);
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setPreferredSize(new Dimension(460, 780));

        JLabel title = new JLabel("Paeonia");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(INK);

        JLabel subtitle = new JLabel("A little more connected to nature");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(3));
        heading.add(subtitle);

        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(0xE5EBDd));
        statusLabel.setForeground(MOSS_DARK);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        statusLabel.setBorder(new EmptyBorder(7, 11, 7, 11));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.add(heading, BorderLayout.CENTER);
        header.add(statusLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        uploadedPhotoPanel = createCard("Your Photo");
        uploadedPhotoLabel = new JLabel("Your uploaded photo", SwingConstants.CENTER);
        uploadedPhotoLabel.setName("uploadedPhotoPreview");
        uploadedPhotoLabel.setForeground(MUTED);
        uploadedPhotoPanel.add(uploadedPhotoLabel);
        uploadedPhotoPanel.setVisible(false);

        plantDetailsPanel = createCard("Plant Information");
        shoppingPanel = createCard("Sample Listings (not for sale)");
        cartPanel = createCard("Shopping Cart");

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        addContentCard(uploadedPhotoPanel);
        addContentCard(plantDetailsPanel);
        addContentCard(shoppingPanel);
        addContentCard(cartPanel);

        JScrollPane contentScroll = new JScrollPane(contentPanel);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(contentScroll, BorderLayout.CENTER);

        selectImageButton = createButton("Select Plant Image", MOSS, Color.WHITE);
        checkoutButton = createButton("Demo Checkout", CARD, MOSS_DARK);
        checkoutButton.setBorder(new RoundedBorder(MOSS, 18));
        checkoutButton.addActionListener(e ->
        {
            if (controller != null)
            {
                controller.checkoutNext();
            }
        });
        selectImageButton.addActionListener(e -> chooseImage());

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.add(selectImageButton);
        actions.add(checkoutButton);
        add(actions, BorderLayout.SOUTH);
    }

    private static JPanel createCard(String title)
    {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        TitledBorder border = BorderFactory.createTitledBorder(
            new RoundedBorder(OUTLINE, 18),
            title,
            TitledBorder.LEADING,
            TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 14),
            MOSS_DARK
        );
        card.setBorder(border);
        return card;
    }

    private void addContentCard(JPanel card)
    {
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(card);
        contentPanel.add(Box.createVerticalStrut(12));
    }

    private static JButton createButton(String text, Color background, Color foreground)
    {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new RoundedBorder(
            background.equals(CARD) ? OUTLINE : background, 18));
        button.setMargin(new Insets(12, 12, 12, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Setter method for the controller variable. */
    public void setController(PlantController controller)
    {
        this.controller = controller;
    }

    private void chooseImage()
    {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                byte[] image = Files.readAllBytes(chooser.getSelectedFile().toPath());
                if (controller != null)
                {
                    controller.identifyPhoto(image);
                }
            }
            catch (IOException e)
            {
                showMessage("Unable to read the selected image.");
            }
        }
    }

    void showUploadedPhoto(byte[] image)
    {
        try
        {
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(image));
            if (source == null)
            {
                throw new IOException("The selected file is not a readable image.");
            }
            uploadedPhotoLabel.setIcon(new ImageIcon(scaleToFit(source, 360, 240)));
            uploadedPhotoLabel.setText("");
        }
        catch (IOException e)
        {
            uploadedPhotoLabel.setIcon(null);
            uploadedPhotoLabel.setText("Photo preview unavailable.");
            System.err.println("Unable to display uploaded plant photo: " + e.getMessage());
        }
        uploadedPhotoPanel.setVisible(true);
        uploadedPhotoPanel.revalidate();
        contentPanel.revalidate();
    }

    private static BufferedImage scaleToFit(BufferedImage source, int maxWidth, int maxHeight)
    {
        double scale = Math.min(1.0, Math.min(
            (double)maxWidth / source.getWidth(),
            (double)maxHeight / source.getHeight()));
        int width = Math.max(1, (int)Math.round(source.getWidth() * scale));
        int height = Math.max(1, (int)Math.round(source.getHeight() * scale));
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaled.createGraphics();
        try
        {
            graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(source, 0, 0, width, height, null);
        }
        finally
        {
            graphics.dispose();
        }
        return scaled;
    }

    /** Renders the current identification, sample listings, and cart. */
    public void render(PlantSnapshot snapshot)
    {
        if (snapshot == null)
        {
            showMessage("Unable to display plant information");
            return;
        }

        plantDetailsPanel.removeAll();
        shoppingPanel.removeAll();
        cartPanel.removeAll();
        referenceImageLabels.clear();

        for (PlantDetails plant : snapshot.getPlant())
        {
            JPanel result = new JPanel();
            result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
            result.setBackground(SOFT_CARD);
            result.setBorder(new EmptyBorder(11, 12, 11, 12));
            result.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel commonName = new JLabel("Plant: " + plant.getCommonName());
            commonName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            commonName.setForeground(INK);
            commonName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel scientificName = new JLabel(
                "Scientific Name: " + plant.getScientificName());
            scientificName.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            scientificName.setForeground(MUTED);
            scientificName.setAlignmentX(Component.LEFT_ALIGNMENT);

            String careText = plant.getCareText().isBlank()
                ? "Care details are not in the demo catalog."
                : plant.getCareText();
            JTextArea careInstructions = new JTextArea(
                "Care Instructions: " + careText);
            careInstructions.setEditable(false);
            careInstructions.setLineWrap(true);
            careInstructions.setWrapStyleWord(true);
            careInstructions.setOpaque(false);
            careInstructions.setFocusable(false);
            careInstructions.setColumns(36);
            careInstructions.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            careInstructions.setForeground(INK);
            careInstructions.setBorder(new EmptyBorder(7, 0, 0, 0));

            result.add(commonName);
            result.add(Box.createVerticalStrut(3));
            result.add(scientificName);
            if (!plant.getReferenceImages().isEmpty())
            {
                result.add(Box.createVerticalStrut(8));
                result.add(createReferenceImageTile(
                    plant.getReferenceImages().get(0)));
            }
            result.add(careInstructions);
            plantDetailsPanel.add(result);
            plantDetailsPanel.add(Box.createVerticalStrut(8));
        }

        if (!snapshot.getPlant().isEmpty() && snapshot.getListings().isEmpty())
        {
            shoppingPanel.add(createEmptyState("No sample listings for this species."));
        }

        for (Listing listing : snapshot.getListings())
        {
            JPanel listingCard = new JPanel(new BorderLayout(10, 0));
            listingCard.setBackground(SOFT_CARD);
            listingCard.setBorder(new EmptyBorder(10, 11, 18, 11));
            listingCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel listingInfo = new JPanel();
            listingInfo.setOpaque(false);
            listingInfo.setLayout(new BoxLayout(listingInfo, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(listing.getDisplayName());
            name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            name.setForeground(INK);
            JLabel price = new JLabel(String.format(
                "$%.2f", listing.getPriceCents() / 100.0));
            price.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            price.setForeground(MUTED);
            listingInfo.add(name);
            listingInfo.add(Box.createVerticalStrut(4));
            listingInfo.add(price);

            JButton addButton = createButton("Add Sample Item", MOSS, Color.WHITE);
            addButton.addActionListener(e ->
            {
                if (controller != null)
                {
                    controller.addPlant(listing.getId());
                }
            });

            listingCard.add(listingInfo, BorderLayout.CENTER);
            listingCard.add(addButton, BorderLayout.EAST);
            shoppingPanel.add(listingCard);
        }

        for (CartItem item : snapshot.getCart())
        {
            JLabel cartItem = new JLabel(item.getListing().getDisplayName());
            cartItem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            cartItem.setForeground(INK);
            cartItem.setBorder(new EmptyBorder(7, 11, 7, 11));
            cartItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            cartPanel.add(cartItem);
        }

        for (JPanel panel : new JPanel[] {
                plantDetailsPanel, shoppingPanel, cartPanel, contentPanel})
        {
            panel.revalidate();
            panel.repaint();
        }
    }

    private JPanel createReferenceImageTile(PlantDetails.ReferenceImage reference)
    {
        JPanel tile = new JPanel(new BorderLayout(10, 0));
        tile.setOpaque(false);
        tile.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imageLabel = new JLabel("Loading photo...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(136, 104));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(BEIGE);
        imageLabel.setForeground(MUTED);

        String organ = reference.getOrgan().isBlank()
            ? "" : "\nPlant part: " + reference.getOrgan();
        JTextArea attribution = new JTextArea(
            "Photo: " + reference.getAuthor()
                + "\nLicense: " + reference.getLicense() + organ);
        attribution.setEditable(false);
        attribution.setLineWrap(true);
        attribution.setWrapStyleWord(true);
        attribution.setOpaque(false);
        attribution.setFocusable(false);
        attribution.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        attribution.setForeground(MUTED);

        tile.add(imageLabel, BorderLayout.WEST);
        tile.add(attribution, BorderLayout.CENTER);

        String url = reference.getUrl();
        referenceImageLabels.computeIfAbsent(url, key -> new ArrayList<>())
            .add(imageLabel);
        ImageIcon cached = referenceImageCache.get(url);
        if (cached != null)
        {
            imageLabel.setText("");
            imageLabel.setIcon(cached);
        }
        else
        {
            loadReferenceImage(reference);
        }
        return tile;
    }

    private void loadReferenceImage(PlantDetails.ReferenceImage reference)
    {
        String url = reference.getUrl();
        if (!referenceImagesLoading.add(url))
        {
            return;
        }

        new javax.swing.SwingWorker<ImageIcon, Void>()
        {
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
                HttpResponse<byte[]> response = IMAGE_CLIENT.send(
                    request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200)
                {
                    throw new IOException(
                        "PlantNet image request returned status " + response.statusCode());
                }

                BufferedImage source = ImageIO.read(
                    new ByteArrayInputStream(response.body()));
                if (source == null)
                {
                    throw new IOException("PlantNet returned an unsupported image.");
                }
                return new ImageIcon(scaleToFit(source, 136, 104));
            }

            @Override
            protected void done()
            {
                referenceImagesLoading.remove(url);
                ImageIcon icon = null;
                try
                {
                    icon = get();
                    referenceImageCache.put(url, icon);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    System.err.println("PlantNet image loading was interrupted.");
                }
                catch (ExecutionException e)
                {
                    Throwable cause = e.getCause();
                    System.err.println("Unable to load PlantNet reference image: "
                        + (cause == null ? e.getMessage() : cause.getMessage()));
                }

                List<JLabel> labels = referenceImageLabels.get(url);
                if (labels == null)
                {
                    return;
                }
                for (JLabel label : labels)
                {
                    label.setIcon(icon);
                    label.setText(icon == null ? "Image unavailable" : "");
                }
            }
        }.execute();
    }

    private static JLabel createEmptyState(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        label.setForeground(MUTED);
        label.setBorder(new EmptyBorder(8, 10, 8, 10));
        return label;
    }

    /** Displays a message to the user. */
    public void showMessage(String message)
    {
        if (message == null || message.isBlank())
        {
            return;
        }

        JOptionPane.showMessageDialog(
            this, message, "Paeonia", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Disables actions while identification is running. */
    public void setBusy(boolean busy)
    {
        selectImageButton.setEnabled(!busy);
        checkoutButton.setEnabled(!busy);
        statusLabel.setText(busy ? "Identifying..." : "Ready");
        statusLabel.setBackground(busy ? new Color(0xE8E3D7) : new Color(0xE5EBDD));
    }

    private static final class RoundedBorder extends AbstractBorder
    {
        private final Color color;
        private final int radius;

        private RoundedBorder(Color color, int radius)
        {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics,
                int x, int y, int width, int height)
        {
            Graphics2D g2 = (Graphics2D)graphics.create();
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component)
        {
            return new Insets(18, 14, 14, 14);
        }
    }
}
