/**
 * Swing class for the View features of the Paeonia application.
 * Acts as the GUI interface.
 * @author Joshua Molina (joshm05)
 * @author Conrad
 * @version November 11, 2026
 */

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

public class PlantView extends JPanel
{
    private static final long serialVersionUID = 1L;
    private boolean busy;
    /**
     * Data field declarations.
     * Creates the panel and button objects.
     */
    private JButton selectImageButton;
    private JButton checkoutButton;

    private JLabel statusLabel;

    private JPanel plantDetailsPanel;
    private JPanel shoppingPanel;
    private JPanel cartPanel;

    private PlantController controller;

    /**
     * Constructor method for the PlantView class.
     */
    public PlantView()
    {
        setLayout(new BorderLayout());
        /**
         * Sets up the main image components and layout.
         */
        JPanel mainPanel = new JPanel(new GridLayout(3, 1));

        plantDetailsPanel = new JPanel();
        plantDetailsPanel.setLayout(new BoxLayout(plantDetailsPanel, BoxLayout.Y_AXIS));

        shoppingPanel = new JPanel();
        shoppingPanel.setLayout(new BoxLayout(shoppingPanel, BoxLayout.Y_AXIS));

        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

        selectImageButton = new JButton("Select Plant Image");
        selectImageButton.addActionListener(e -> chooseImage());

        /**
         * Checkout button triggers the controller's checkoutNext method.
         */
        checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e ->
            {
            if (controller != null)
                {
                    controller.checkoutNext();
                }
            });

        statusLabel = new JLabel("Ready");

        /**
         * Creates borders with labels for each main panel.
         */
        plantDetailsPanel.setBorder(BorderFactory.createTitledBorder("Plant Information"));
        shoppingPanel.setBorder(BorderFactory.createTitledBorder("Available Plants"));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        /**
         * Adds the plant detail, shopping, and cart layouts to the main panel.
         */
        mainPanel.add(plantDetailsPanel);
        mainPanel.add(shoppingPanel);
        mainPanel.add(cartPanel);

        add(selectImageButton, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        JPanel buttomPanel = new JPanel(new BorderLayout());
        buttomPanel.add(statusLabel, BorderLayout.CENTER);
        buttomPanel.add(checkoutButton, BorderLayout.EAST);

        add(buttomPanel, BorderLayout.SOUTH);
    }

    /**
     * Setter method for the controller variable.
     * @param controller : The controller feature that allows user interactivity.
     */
    public void setController(PlantController controller)
    {
        this.controller = controller;
    }

    /**
     * Allows for the user to insert a saved image for identification.
     */
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
            } catch (IOException e)
            {
                showMessage("Unable to read the selected image.");
            }
        }
    }

    /**
     * Renders plant details, listings, and cart contents.
     * @param snapshot the current plant, listing, and cart state
     */
    public void render(PlantSnapshot snapshot)
    {
        if (snapshot == null)
        {
            showMessage("Unable to display plant information");
            return;
        }

        /**
         * Resets information contained within the panels.
         */
        plantDetailsPanel.removeAll();
        shoppingPanel.removeAll();
        cartPanel.removeAll();

        /**
         * Checks if snapshot contains items, then logs them to the panel.
         */
        for (PlantDetails plant : snapshot.getPlant())
        {
            plantDetailsPanel.add(new JLabel("Plant: " + plant.getCommonName()));
            plantDetailsPanel.add(new JLabel("Scientific Name: " + plant.getScientificName()));
            plantDetailsPanel.add(new JLabel("Care Instructions: " + plant.getCareText()));
        }

        /**
         * Logs informations within the listings to the listing panel.
         */
        for (Listing listing : snapshot.getListings())
        {
            JPanel listingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            listingPanel.add(new JLabel(listing.getDisplayName()));
            listingPanel.add(new JLabel(String.format("$%.2f", listing.getPriceCents() / 100.0)));

            JButton addButton = new JButton("Add to Cart");
            addButton.setEnabled(!busy);
            addButton.addActionListener(e ->
                {
                    if (controller != null)
                    {
                        controller.addPlant(listing.getId());
                    }
                });
            listingPanel.add(addButton);
            shoppingPanel.add(listingPanel);
        }

        /**
         * Logs information within the cart to the cart panel.
         */
        for (CartItem item : snapshot.getCart())
        {
            Listing listing = item.getListing();

            cartPanel.add(new JLabel(listing.getDisplayName()));
        }

        /**
         * Revalidates and repaints the panels with the new information.
         */
        plantDetailsPanel.revalidate();
        plantDetailsPanel.repaint();
        shoppingPanel.revalidate();
        shoppingPanel.repaint();
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    /**
     * Displays given dialog and error messages.
     * @param message : Contained details and correspondents on processes and errors.
     */
    public void showMessage(String message)
    {
        if (message == null || message.isBlank())
        {
            return;
        }

        JOptionPane.showMessageDialog(this, message, "Plant Application", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Sets the application as busy during a process.
     * @param busy : boolean expression to determine if a process is running.
     */
    public void setBusy(boolean busy)
    {
        this.busy = busy;
        selectImageButton.setEnabled(!busy);
        checkoutButton.setEnabled(!busy);
        for (Component component : shoppingPanel.getComponents())
        {
            if (component instanceof JPanel)
            {
                for (Component child : ((JPanel)component).getComponents())
                {
                    if (child instanceof JButton)
                    {
                        child.setEnabled(!busy);
                    }
                }
            }
        }

        statusLabel.setText(busy ? "Processing...." : "Ready");
    }
}
