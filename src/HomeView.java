//Written by 小田竜太郎

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.Arrays;

public class HomeView extends JPanel implements ActionListener {

    private final JButton infoButton;
    private final JTextArea infoTextArea;
    private final JLabel gameTitleLabel;
    private final JLabel nameLabel;
    private final JTextField nameField;
    private final JButton createRoomButton;
    private final JButton enterRoomButton;
    private final JTextField roomIDField;

    public HomeView() {
        Color noColor = new Color(1, 1, 1, 0);
        LineBorder debugBorder = new LineBorder(noColor, 2, true);//枠線
        LineBorder buttonBorder = new LineBorder(Color.gray, 2, true);

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0d;
        c.weighty = 1.0d;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5); //隙間

        int[] xSize = new int[16];//x軸の１マスの長さ調整用
        Arrays.fill(xSize, 0);
        JLabel[] xAxis = new JLabel[16];
        c.gridy = 0;
        for (int i = 0; i < xAxis.length; i++) {
            String str = "  ";
            c.gridx = i;
            for (int j = 0; j < xSize[i]; j++) {
                str += " ";
            }
            xAxis[i] = new JLabel(str);
            xAxis[i].setBorder(debugBorder);
            layout.setConstraints(xAxis[i], c);
            this.add(xAxis[i]);
        }

        int[] ySize = new int[9];
        Arrays.fill(ySize, 0);
        JLabel[] yAxis = new JLabel[9];
        c.gridx = 0;
        for (int i = 0; i < yAxis.length; i++) {
            String str = "<html>";
            c.gridy = i;
            for (int j = 0; j < ySize[i]; j++) {
                str += "<br>";
            }
            yAxis[i] = new JLabel(str);
            yAxis[i].setBorder(debugBorder);
            layout.setConstraints(yAxis[i], c);
            this.add(yAxis[i]);
        }

        this.infoButton = new JButton("i");
        // infoButton.setBorder(buttonBorder);
        c.gridx = 15;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        layout.setConstraints(infoButton, c);
        this.add(infoButton);
        infoButton.addActionListener(this);
        infoButton.setActionCommand("info");

        this.infoTextArea = new JTextArea();
        infoTextArea
                .setText("Create by 　\n4班グループA　\n1W192077 小田竜太郎　　\n1W202335 水野重弦　　\n1W202345 村田一晃　　\n1W202347 村田侑輝");
        infoTextArea.setAlignmentY(CENTER_ALIGNMENT);

        this.gameTitleLabel = new JLabel("ワンナイト人狼");
        gameTitleLabel.setBorder(debugBorder);
        gameTitleLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, Main.standardSize * 10));
        gameTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        gameTitleLabel.setVerticalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        c.gridheight = 3;
        layout.setConstraints(gameTitleLabel, c);
        this.add(gameTitleLabel);

        this.nameLabel = new JLabel("名前:");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        nameLabel.setBorder(debugBorder);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 7;
        c.gridheight = 2;
        layout.setConstraints(nameLabel, c);
        this.add(nameLabel);

        this.nameField = new JTextField(1);
        nameField.setBorder(debugBorder);
        c.gridx = 7;
        c.gridy = 4;
        c.gridwidth = 9;
        c.gridheight = 2;
        layout.setConstraints(nameField, c);
        this.add(nameField);

        this.createRoomButton = new JButton("部屋を作成");
        createRoomButton.setBorder(buttonBorder);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 8;
        c.gridheight = 3;
        layout.setConstraints(createRoomButton, c);
        this.add(createRoomButton);
        createRoomButton.addActionListener(this);
        createRoomButton.setActionCommand("createRoom");

        JLabel roomIDLabel = new JLabel("ルームID:");
        roomIDLabel.setHorizontalAlignment(JLabel.RIGHT);
        roomIDLabel.setBorder(debugBorder);
        c.gridx = 8;
        c.gridy = 6;
        c.gridwidth = 3;
        c.gridheight = 1;
        layout.setConstraints(roomIDLabel, c);
        this.add(roomIDLabel);

        this.roomIDField = new JTextField(1);
        roomIDField.setBorder(debugBorder);
        c.gridx = 11;
        c.gridy = 6;
        c.gridwidth = 5;
        c.gridheight = 1;
        layout.setConstraints(roomIDField, c);
        this.add(roomIDField);

        this.enterRoomButton = new JButton("部屋に入室");
        enterRoomButton.setBorder(buttonBorder);
        c.gridx = 8;
        c.gridy = 7;
        c.gridwidth = 8;
        c.gridheight = 2;
        layout.setConstraints(enterRoomButton, c);
        this.add(enterRoomButton);
        enterRoomButton.addActionListener(this);
        enterRoomButton.setActionCommand("enterRoom");

    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "info":
                JOptionPane.showMessageDialog(this, infoTextArea, "Information", JOptionPane.PLAIN_MESSAGE);
                break;

            case "createRoom":
                if (nameField.getText().equals("")) {//?: == ""だと動かない
                    JOptionPane.showMessageDialog(this, "名前を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                } else {
                    Main.isHost = true;
                    Main.myself.name = nameField.getText();
                    Main.myself.identifier = 0;//(isHost) = :0)
                    System.out.println("I'm " + Main.myself.name);
                    MainWindow.changeView(ViewType.RoomView);
                }
                break;

            case "enterRoom":
                if (nameField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "名前を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                }
                // } else if (roomIDField.getText().equals("")) {
                //     JOptionPane.showMessageDialog(this, "RoomIDを入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                // }
                // else if (!roomIDField.getText().equals("8080")) {
                //     JOptionPane.showMessageDialog(this, "部屋が見つかりませんでした。", "エラー", JOptionPane.ERROR_MESSAGE);//TODO:
                // }
                else {
                    Main.isHost = false;
                    Main.myself.name = nameField.getText();
                    System.out.println("I'm " + Main.myself.name);
                    MainWindow.changeView(ViewType.RoomView);
                }
                break;

            default:
                System.out.println("どこかで無効なボタンが押された");
                break;
        }
    }
}
