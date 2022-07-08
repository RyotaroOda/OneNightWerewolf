//Written by 小田竜太郎

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.Arrays;

public class RoomView extends JPanel implements ActionListener {

    private final JButton backButton;
    private final JLabel roomIDLabel;
    private static JTextArea playerNameList;
    private final JTextArea ruleText;
    private static JButton readyButton;
    private static JTextArea logText;
    private final JScrollPane logScrollPane;
    private static JScrollBar logScrollBar;
    private final JTextField messageField;

    public RoomView() {
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

        //x軸
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
        //y軸
        int[] ySize = new int[9];
        Arrays.fill(ySize, 0);
        JLabel[] yAxis = new JLabel[9];
        c.gridx = 0;
        for (int i = 0; i < yAxis.length; i++) {
            String str = "<html>";
            c.gridy = i;
            for (int j = 0; j < ySize[i]; j++) {
                str += "<br>";//改行
            }
            yAxis[i] = new JLabel(str);
            yAxis[i].setBorder(debugBorder);
            layout.setConstraints(yAxis[i], c);
            this.add(yAxis[i]);
        }

        this.backButton = new JButton("←");
        backButton.setBorder(buttonBorder);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        layout.setConstraints(backButton, c);
        this.add(backButton);
        backButton.addActionListener(this);
        backButton.setActionCommand("back");

        this.roomIDLabel = new JLabel("roomID:8080");
        roomIDLabel.setHorizontalAlignment(JLabel.CENTER);
        roomIDLabel.setVerticalAlignment(JLabel.CENTER);
        roomIDLabel.setBorder(debugBorder);
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 7;
        c.gridheight = 2;
        layout.setConstraints(roomIDLabel, c);
        this.add(roomIDLabel);

        playerNameList = new JTextArea("");
        playerNameList.setBorder(debugBorder);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 5;
        c.gridheight = 6;
        layout.setConstraints(playerNameList, c);
        playerNameList.setEditable(false);
        this.add(playerNameList);

        this.ruleText = new JTextArea("ゲームタイプ：ワンナイト\n");
        ruleText.append("人数：　" + Main.playersCount + "[人]\n");
        ruleText.append("時間：　" + Main.gameTime + "[分]\n");
        ruleText.append("役職：　");
        for (int i = 0; i < Main.roleSlots.length; i++) {
            ruleText.append(RoleType.values()[i].getName() + "（" + Main.roleSlots[i] + "）");
            if (i != Main.roleSlots.length - 1) {
                ruleText.append(", ");
            }
        }
        ruleText.append("\n");
        ruleText.setBorder(debugBorder);
        c.gridx = 5;
        c.gridy = 2;
        c.gridwidth = 5;
        c.gridheight = 6;
        layout.setConstraints(ruleText, c);
        ruleText.setLineWrap(true);
        ruleText.setWrapStyleWord(true);
        ruleText.setEditable(false);
        this.add(ruleText);

        readyButton = new JButton("　　　　　  準備OK?  　　　　　");
        c.gridx = 2;
        c.gridy = 8;
        c.gridwidth = 6;
        c.gridheight = 1;
        layout.setConstraints(readyButton, c);
        this.add(readyButton);
        readyButton.addActionListener(this);
        readyButton.setActionCommand("ready");
        readyButton.setEnabled(false);

        logText = new JTextArea("log:\n");
        this.logScrollPane = new JScrollPane();
        logScrollPane.setViewportView(logText);
        logScrollPane.setBorder(debugBorder);
        logScrollBar = logScrollPane.getVerticalScrollBar();
        c.gridx = 10;
        c.gridy = 0;
        c.gridwidth = 6;
        c.gridheight = 8;
        layout.setConstraints(logScrollPane, c);
        logText.setLineWrap(true);
        logText.setWrapStyleWord(true);//折り返し
        logText.setEditable(false);
        this.add(logScrollPane);

        this.messageField = new JTextField();
        messageField.setBorder(debugBorder);
        c.gridx = 10;
        c.gridy = 8;
        c.gridwidth = 6;
        c.gridheight = 1;
        layout.setConstraints(messageField, c);
        this.add(messageField);
        messageField.addActionListener(this);
        messageField.setActionCommand("message");

    }

    public static void loadView() {
        playerNameList.append("参加者リスト：\n" + Main.myself.name + "(あなた)\n");

        if (Main.isHost) {
            readyButton.setText("ゲームスタート！");
            readyButton.setActionCommand("start");
            Thread thread = new HostWorks();
            thread.start();
        } else {
            Thread thread = new GuestWorks();
            thread.start();
        }
    }

    public static void endView() {
        MainWindow.changeView(ViewType.GameView);
    }

    public static void addPlayer(String playerName) {
        playerNameList.append(playerName + "\n");
        logText.append(playerName + "が入室しました。\n");
    }

    public static void showPlayer(String name) {
        playerNameList.append(name + "\n");
        logText.append(name + "\n");
        logScrollBar.setValue(logScrollBar.getMaximum());
    }

    public static void writeLog(String message) {
        logText.append(message + "\n");
        logScrollBar.setValue(logScrollBar.getMaximum());
    }

    public static void allReady() {
        readyButton.setEnabled(true);
    }

    public static void showAlert(String message) {
        JOptionPane.showMessageDialog(MainWindow.frame, message);
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "back":
                int alert = JOptionPane.showConfirmDialog(this, "部屋から退出しますか？", "確認", JOptionPane.YES_NO_OPTION);
                switch (alert) {
                    case JOptionPane.OK_OPTION:
                        MainWindow.changeView(ViewType.HomeView);
                        break;

                    case JOptionPane.NO_OPTION:
                        break;

                    case JOptionPane.CLOSED_OPTION:
                        break;

                    default:
                        System.out.println(alert + " 未定義");
                        break;
                }
                break;

            case "ready":
                readyButton.setEnabled(false);
                readyButton.setText("他のプレイヤーを待っています...");
                CommunicationAPI.send(TagType.READY, null);
                break;

            case "start":
                HostWorks.assignmentRole();
                break;

            case "message":
                if (!messageField.getText().equals("")) {
                    logText.append(Main.myself.name + ":" + messageField.getText() + "\n");
                    logScrollBar.setValue(logScrollBar.getMaximum());
                    CommunicationAPI.send(TagType.ChatRoom, messageField.getText());
                    messageField.setText("");
                }
                break;

            default:
                System.out.println("default: どこかで無効なボタンが押された");
                break;
        }
    }
}
