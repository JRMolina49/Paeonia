import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

public class PlantView extends JPanel
{
    private JButton selectImageButton;
    private JButton checkoutButton;

    private JLabel statusLabel;

    private JPanel plantDetailsPanel;
    private JPanel shoppingPanel;
    private JPanel cartPanel;

    private PlantController controller;

    public PlantView()
    {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(3, 1));

        plantDetailsPanel = new JPanel();
        plantDetailsPanel.setLayout(new BoxLayout(plantDetailsPanel, BoxLayout.Y_AXIS));

        shoppingPanel = new JPanel();
        shoppingPanel.setLayout(new BoxLayout(shoppingPanel, BoxLayout.Y_AXIS));

        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

        selectImageButton = new JButton("Select Plant Image");
        selectImageButton.addActionListener(e -> chooseImage());

        checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> 
            {
            if (controller != null) 
                { 
                    controller.checkoutNext();
                }
            });
        
        statusLabel = new JLabel("Ready");

        plantDetailsPanel.setBorder(BorderFactory.createTitledBorder("Plant Information"));
        shoppingPanel.setBorder(BorderFactory.createTitledBorder("Available Plants"));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        mainPanel.add(plantDetailsPanel);
        mainPanel.add(shoppingPanel);
        mainPanel.add(cartPanel);

        add(selectImageButton, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttomPanel = new JPanel(new BorderLayout());
        buttomPanel.add(statusLabel, BorderLayout.CENTER);
        buttomPanel.add(checkoutButton, BorderLayout.EAST);

        add(buttomPanel, BorderLayout.SOUTH);
    }

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
            } catch (IOException e)
            {
                showMessage("Unable to read the selected image.");
            }
        }
    }

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

        for (PlantDetails plant : snapshot.getPlant())
        {
            plantDetailsPanel.add(new JLabel("Plant: " + plant.getCommonName()));
            plantDetailsPanel.add(new JLabel("Scienetific Name: " + plant.getScientificName()));
            plantDetailsPanel.add(new JLabel("Care Instructions: " + plant.getCareText()));
        }

        for (Listing listing : snapshot.getListings())
        {
            JPanel listingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            listingPanel.add(new JLabel(listing.getDisplayName()));
            listingPanel.add(new JLabel(String.format("$%.2f", listing.getPriceCents() / 100.0)));
            
            JButton addButton = new JButton("Add to Cart");
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

        for (CartItem item : snapshot.getCart())
        {
            Listing listing = item.getListing();

            cartPanel.add(new JLabel(listing.getDisplayName()));
        }

        plantDetailsPanel.revalidate();
        plantDetailsPanel.repaint();
        shoppingPanel.revalidate();
        shoppingPanel.repaint();
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    public void showMessage(String message)
    {
        if (message == null || message.isBlank())
        {
            return;
        }

        JOptionPane.showMessageDialog(this, message, "Plant Application", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void setBusy(boolean busy)
    {
        selectImageButton.setEnabled(!busy);
        checkoutButton.setEnabled(!busy);

        statusLabel.setText(busy ? "Processing...." : "Ready");
    }
}
