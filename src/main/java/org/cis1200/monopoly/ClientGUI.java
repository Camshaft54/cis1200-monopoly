package org.cis1200.monopoly;

import javax.swing.*;
import java.awt.*;

public class ClientGUI {
    JLabel playerId;

    public ClientGUI() {
        JFrame frame = new JFrame("Monopoly Client");
        frame.setLocation(300, 300);

        final JPanel controlPanel = new JPanel();
        frame.add(controlPanel, BorderLayout.SOUTH);

        final JPanel statusPanel = new JPanel();
        playerId = new JLabel("N/A");
        statusPanel.add(playerId);
        frame.add(statusPanel, BorderLayout.NORTH);

        // TODO add board component

        frame.setSize(100, 100);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public void update(MonopolyClient client) {
        playerId.setText(Integer.toString(client.getMyPlayerId()));
    }
}
