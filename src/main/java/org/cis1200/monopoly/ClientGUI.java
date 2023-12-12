package org.cis1200.monopoly;

import org.cis1200.monopoly.game.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientGUI implements ActionListener {
    MonopolyClient client;
    CompletableFuture<ClientMessage> messageFuture = null;
    JFrame frame;
    JLabel myId;
    JLabel myName;
    JLabel myMoney;
    JLabel opponentMoneyTitle;
    JLabel opponentMoney;
    List<JButton> controls = new LinkedList<>();
    JComboBox<String> propertyChooser;

    public ClientGUI(MonopolyClient client, List<String> spaceNames, List<Color> spaceColors) {
        this.client = client;

        this.frame = new JFrame("Monopoly Client");
        frame.setLocation(300, 300);

        final JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 5));
        controlPanel.add(Box.createGlue());
        JButton rollDice = new JButton("Roll Dice");
        rollDice.addActionListener(this);
        controls.add(rollDice);
        controlPanel.add(rollDice);
        JButton buyProperty = new JButton("Buy Property");
        buyProperty.addActionListener(this);
        controls.add(buyProperty);
        controlPanel.add(buyProperty);
        JButton endTurn = new JButton("End Turn");
        endTurn.addActionListener(this);
        controls.add(endTurn);
        controlPanel.add(endTurn);
        controlPanel.add(Box.createGlue());
        JButton buyHouse = new JButton("Buy House");
        buyHouse.addActionListener(this);
        controls.add(buyHouse);
        controlPanel.add(buyHouse);
        JButton sellHouse = new JButton("Sell House");
        sellHouse.addActionListener(this);
        controls.add(sellHouse);
        controlPanel.add(sellHouse);
        JButton mortgageProperty = new JButton("Mortgage Property");
        mortgageProperty.addActionListener(this);
        controls.add(mortgageProperty);
        controlPanel.add(mortgageProperty);
        JButton unmortgageProperty = new JButton("Unmortgage Property");
        unmortgageProperty.addActionListener(this);
        controls.add(unmortgageProperty);
        controlPanel.add(unmortgageProperty);
        propertyChooser = new JComboBox<>();
        controlPanel.add(propertyChooser);
        frame.add(controlPanel, BorderLayout.SOUTH);

        final JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(2, 3));
        statusPanel.add(myId = new JLabel("Player N/A"));
        statusPanel.add(new JLabel("My Money"));
        statusPanel.add(opponentMoneyTitle = new JLabel("Opponent's Money"));
        statusPanel.add(myName = new JLabel("Playing as null"));
        statusPanel.add(myMoney = new JLabel("$0"));
        statusPanel.add(opponentMoney = new JLabel("$0"));
        frame.add(statusPanel, BorderLayout.NORTH);

        BoardGUI boardGUI = new BoardGUI(spaceNames, spaceColors, 9);
        JPanel boardWrapper = new JPanel();
        boardWrapper.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        boardWrapper.add(boardGUI, gbc);
        frame.add(boardWrapper, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public void update(CompletableFuture<ClientMessage> messageFuture) {
        this.messageFuture = messageFuture;
        myId.setText("Player " + client.getId());
        myName.setText("Playing as " + client.getMe().getName());
        myMoney.setText("$" + client.getMe().getMoney());
        opponentMoneyTitle.setText(client.getOpponent().getName() + "'s Money");
        opponentMoney.setText("$" + client.getOpponent().getMoney());
        propertyChooser.removeAllItems();
        client.getMe().getProperties().forEach(p -> propertyChooser.addItem(p));
        if (client.isMyTurn()) {
            controls.forEach(b -> b.setEnabled(true));
        } else {
            controls.forEach(b -> b.setEnabled(false));
        }
    }

    public void warnUser(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    public String provideName() {
        if (client.getId() == 1) {
            return "Cameron";
        } else {
            return "Duke";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClientMessage message;
        String selectedProperty = (String) propertyChooser.getSelectedItem();
        switch (e.getActionCommand()) {
            case "Roll Dice" -> {
                message = new ClientMessage("ROLL_DICE", "", client.getId());
            }
            case "Buy Property" -> {
                message = new ClientMessage("BUY_PROPERTY", "", client.getId());
            }
            case "End Turn" -> {
                message = new ClientMessage("END_TURN", "", client.getId());
            }
            case "Buy House" -> {
                message = new ClientMessage("BUY_HOUSE", selectedProperty, client.getId());
            }
            case "Sell House" -> {
                message = new ClientMessage("SELL_HOUSE", selectedProperty, client.getId());
            }
            case "Mortgage Property" -> {
                message = new ClientMessage("MORTGAGE", selectedProperty, client.getId());
            }
            case "Unmortgage Property" -> {
                message = new ClientMessage("UNMORTGAGE", selectedProperty, client.getId());
            }
            default -> {
                return;
            }
        }
        while (messageFuture == null || messageFuture.isDone()) {

        }
        messageFuture.complete(message);
    }
}
