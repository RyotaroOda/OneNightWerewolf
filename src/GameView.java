//Written by 小田竜太郎

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.Timer;
import java.util.*;
import java.util.List;

public class GameView extends JPanel implements ActionListener {

    private static JPanel commandPanel;
    private static CardLayout layoutCommand;

    private static JLabel phaseImage;
    private static JLabel timeLabel;
    private static JTextArea logText;
    private final JScrollPane logScrollPane;
    private static JScrollBar logScrollBar;

    // private final JButton settingButton;
    private static JLabel myNameLabel;
    private static JLabel myRoleImage;
    private static JLabel otherNameLabels[];
    private static JLabel otherRoleImages[];

    private final JPanel unusedRolesPanel;
    private final JLabel unusedLabel;
    private final JPanel unusedRoleImagesPanel;
    private static JLabel unusedRoleImage0;
    private static JLabel unusedRoleImage1;

    private static JPanel nightPanel;
    private static JLabel nightLabel;
    private static JPanel nightMainPanel;
    private static JLabel nightMainLabel;
    private static JComboBox<String> nightSelect;
    private static JButton nightButton;

    private static JPanel chatPanel;
    private final JPanel messagePanel;
    private final JLabel messageLabel;

    private final JTextField messageField;
    private final JPanel coPanel;
    private final JButton coButton;
    private final JComboBox<String> selectCO;

    private static JPanel votePanel;
    private static JLabel voteTimeLabel;
    private static JComboBox<String> selectPlayer;
    private static JButton voteButton;

    private static JPanel waitPanel;
    private static JLabel waitLabel;

    private static JPanel continuePanel;
    private final JLabel continueLabel;
    private final JPanel continueButtonPanel;
    private final JButton continueYesButton;
    private final JButton continueNoButton;

    private static RoleType[] unusedRoles;

    private static boolean voted = false;
    private static boolean chaos = false;

    public GameView() {
        Color noColor = new Color(1, 1, 1, 0);
        LineBorder debugBorder = new LineBorder(noColor, 2, true);//枠線
        // LineBorder buttonBorder = new LineBorder(Color.gray, 2, true);
        ImageIcon unknownIcon = getImageIcon("lib/width=1000/Unknown.png", 2);

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
                str += "<br>";
            }
            yAxis[i] = new JLabel(str);
            yAxis[i].setBorder(debugBorder);
            layout.setConstraints(yAxis[i], c);
            this.add(yAxis[i]);
        }

        layoutCommand = new CardLayout();
        commandPanel = new JPanel();
        commandPanel.setLayout(layoutCommand);
        commandPanel.setBorder(debugBorder);
        c.gridx = 3;
        c.gridy = 6;
        c.gridwidth = 10;
        c.gridheight = 3;
        layout.setConstraints(commandPanel, c);
        this.add(commandPanel);

        phaseImage = new JLabel(getImageIcon("lib/width=1000/Moon.png", 2));
        phaseImage.setBorder(debugBorder);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        layout.setConstraints(phaseImage, c);
        this.add(phaseImage);

        timeLabel = new JLabel("Time: STOP");
        timeLabel.setBorder(debugBorder);
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        layout.setConstraints(timeLabel, c);
        this.add(timeLabel);

        logText = new JTextArea("log:\nGameMaster: ゲームスタート！\n");
        logScrollPane = new JScrollPane();
        logScrollPane.setViewportView(logText);
        logScrollPane.setBorder(debugBorder);
        logScrollBar = logScrollPane.getVerticalScrollBar();

        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 10;
        c.gridheight = 6;
        layout.setConstraints(logScrollPane, c);
        logText.setLineWrap(true);
        logText.setWrapStyleWord(true);
        logText.setEditable(false);
        this.add(logScrollPane);

        // this.settingButton = new JButton("⚙");
        // // settingButton.setBorder(buttonBorder);
        // settingButton.setFont((new Font("ＭＳ ゴシック", Font.PLAIN, Main.standardSize * 5)));
        // c.gridx = 15;
        // c.gridy = 0;
        // c.gridwidth = 1;
        // c.gridheight = 1;
        // layout.setConstraints(settingButton, c);
        // this.add(settingButton);
        // settingButton.addActionListener(this);
        // settingButton.setActionCommand("setting");

        otherNameLabels = new JLabel[Main.playersCount - 1];
        otherRoleImages = new JLabel[Main.playersCount - 1];

        myNameLabel = new JLabel();//自分
        myNameLabel.setBorder(debugBorder);
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 2;
        c.gridheight = 1;
        layout.setConstraints(myNameLabel, c);
        this.add(myNameLabel);

        myRoleImage = new JLabel();
        myRoleImage.setBorder(debugBorder);
        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 2;
        c.gridheight = 2;
        layout.setConstraints(myRoleImage, c);
        this.add(myRoleImage);

        otherNameLabels[0] = new JLabel();
        otherNameLabels[0].setBorder(debugBorder);
        c.gridx = 13;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        layout.setConstraints(otherNameLabels[0], c);
        this.add(otherNameLabels[0]);

        otherRoleImages[0] = new JLabel(unknownIcon);
        otherRoleImages[0].setBorder(debugBorder);
        c.gridx = 14;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 2;
        layout.setConstraints(otherRoleImages[0], c);
        this.add(otherRoleImages[0]);

        otherNameLabels[1] = new JLabel();
        otherNameLabels[1].setBorder(debugBorder);
        c.gridx = 13;
        c.gridy = 4;
        c.gridwidth = 2;
        c.gridheight = 1;
        layout.setConstraints(otherNameLabels[1], c);
        this.add(otherNameLabels[1]);

        otherRoleImages[1] = new JLabel(unknownIcon);
        otherRoleImages[1].setBorder(debugBorder);
        c.gridx = 14;
        c.gridy = 4;
        c.gridwidth = 2;
        c.gridheight = 2;
        layout.setConstraints(otherRoleImages[1], c);
        this.add(otherRoleImages[1]);

        otherNameLabels[2] = new JLabel();
        otherNameLabels[2].setBorder(debugBorder);
        c.gridx = 13;
        c.gridy = 7;
        c.gridwidth = 2;
        c.gridheight = 1;
        layout.setConstraints(otherNameLabels[2], c);
        this.add(otherNameLabels[2]);

        otherRoleImages[2] = new JLabel(unknownIcon);
        otherRoleImages[2].setBorder(debugBorder);
        c.gridx = 14;
        c.gridy = 7;
        c.gridwidth = 2;
        c.gridheight = 2;
        layout.setConstraints(otherRoleImages[2], c);
        this.add(otherRoleImages[2]);

        this.unusedRolesPanel = new JPanel(new BorderLayout()); //caution: JPanelはデフォルトでBorderLayoutだが実際にBorderLayoutを使う場合は宣言しといた方がいい
        unusedRolesPanel.setBackground(Color.lightGray);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 2;
        layout.setConstraints(unusedRolesPanel, c);

        this.unusedLabel = new JLabel("未使用の役職");
        unusedLabel.setHorizontalAlignment(JLabel.CENTER);
        unusedLabel.setBorder(debugBorder);
        unusedRolesPanel.add(unusedLabel, BorderLayout.NORTH);

        this.unusedRoleImagesPanel = new JPanel(new BorderLayout());
        unusedRoleImagesPanel.setBackground(Color.lightGray);

        unusedRoleImage0 = new JLabel(unknownIcon);
        unusedRoleImage0.setBorder(debugBorder);
        unusedRoleImagesPanel.add(unusedRoleImage0, BorderLayout.WEST);

        unusedRoleImage1 = new JLabel(unknownIcon);
        unusedRoleImage1.setBorder(debugBorder);
        unusedRoleImagesPanel.add(unusedRoleImage1, BorderLayout.EAST);

        unusedRolesPanel.add(unusedRoleImagesPanel, BorderLayout.CENTER);
        this.add(unusedRolesPanel);

        nightPanel = new JPanel(new BorderLayout());
        nightLabel = new JLabel();
        nightLabel.setHorizontalAlignment(JLabel.CENTER);
        nightPanel.add(nightLabel, BorderLayout.NORTH);

        nightMainPanel = new JPanel(new BorderLayout());
        nightMainLabel = new JLabel();
        nightMainLabel.setHorizontalAlignment(JLabel.CENTER);
        nightMainPanel.add(nightMainLabel, BorderLayout.CENTER);
        nightSelect = new JComboBox<String>();//addは任意
        nightPanel.add(nightMainPanel, BorderLayout.CENTER);

        nightButton = new JButton("OK");
        nightPanel.add(nightButton, BorderLayout.SOUTH);
        nightButton.addActionListener(this);
        commandPanel.add(nightPanel, "night");

        chatPanel = new JPanel(new BorderLayout());
        this.messagePanel = new JPanel(new BorderLayout());

        this.messageLabel = new JLabel("メッセージ：");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messagePanel.add(messageLabel, BorderLayout.WEST);

        messageField = new JTextField(1);
        messageField.setBorder(debugBorder);
        messageField.addActionListener(this);
        messageField.setActionCommand("message");
        messagePanel.add(messageField, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.CENTER);

        this.coPanel = new JPanel(new BorderLayout());

        this.selectCO = new JComboBox<String>();
        for (int i = 0; i < RoleType.values().length; i++) {
            selectCO.addItem(RoleType.values()[i].getName());
        }
        coPanel.add(selectCO, BorderLayout.CENTER);

        this.coButton = new JButton("CO");
        coButton.addActionListener(this);
        coButton.setActionCommand("CO");
        coPanel.add(coButton, BorderLayout.EAST);
        chatPanel.add(coPanel, BorderLayout.NORTH);
        commandPanel.add(chatPanel, "chat");

        votePanel = new JPanel(new BorderLayout());

        voteTimeLabel = new JLabel("投票してください : 60");
        voteTimeLabel.setBorder(debugBorder);
        votePanel.add(voteTimeLabel, BorderLayout.NORTH);

        selectPlayer = new JComboBox<String>();
        selectPlayer.setBorder(debugBorder);
        votePanel.add(selectPlayer, BorderLayout.CENTER);

        voteButton = new JButton("投票");
        voteButton.addActionListener(this);
        voteButton.setActionCommand("vote");
        votePanel.add(voteButton, BorderLayout.EAST);
        commandPanel.add(votePanel, "vote");

        //commandPanelChildren
        waitPanel = new JPanel(new BorderLayout());
        waitLabel = new JLabel("他のプレイイヤーを待っています...");
        waitLabel.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 3;
        c.gridy = 7;
        c.gridwidth = 10;
        c.gridheight = 2;
        layout.setConstraints(waitPanel, c);
        waitPanel.add(waitLabel, BorderLayout.CENTER);
        commandPanel.add(waitPanel, "wait");

        continuePanel = new JPanel(new BorderLayout());
        this.continueLabel = new JLabel("この部屋でゲームを続けますか？");
        continueLabel.setHorizontalAlignment(JLabel.CENTER);
        continuePanel.add(continueLabel, BorderLayout.NORTH);

        this.continueButtonPanel = new JPanel(new FlowLayout());
        this.continueYesButton = new JButton("Yes");
        continueYesButton.addActionListener(this);
        continueYesButton.setActionCommand("continueYes");
        continueButtonPanel.add(continueYesButton);
        this.continueNoButton = new JButton("No");
        continueNoButton.addActionListener(this);
        continueNoButton.setActionCommand("continueNo");
        continueButtonPanel.add(continueNoButton);
        continuePanel.add(continueButtonPanel, BorderLayout.CENTER);
        commandPanel.add(continuePanel, "continue");

    }

    public static void loadView() {
        unusedRoles = new RoleType[2];
        int[] unusedSlots = Main.roleSlots;
        unusedSlots[Main.myself.role.ordinal()]--;
        for (int i = 0; i < Main.others.size(); i++) {
            unusedSlots[Main.others.get(i).role.ordinal()]--;
        }
        for (int i = 0; i < unusedRoles.length; i++) {
            for (int j = 0; j < unusedSlots.length; j++) {
                if (unusedSlots[j] != 0) {
                    unusedRoles[i] = RoleType.values()[j];
                    unusedSlots[j]--;
                    break;
                }
            }
        }

        Main.myself.nameLabel = myNameLabel;
        Main.myself.roleImage = myRoleImage;
        myNameLabel.setText(Main.myself.name);
        myNameLabel.setForeground(Main.myself.color);
        myRoleImage.setIcon(getImageIcon(Main.myself.role.getImageName(), 3));

        if (Main.playersCount != 4 || Main.others.size() + 1 != Main.playersCount || unusedRoles.length != 2) {//REVIEW ４人の場合のみ
            System.err.println("PlayerCountError!!");//ANCHOR error
        } else {
            for (int i = 0; i < otherNameLabels.length; i++) {
                otherNameLabels[i].setText(Main.others.get(i).name);
                otherNameLabels[i].setForeground(Main.others.get(i).color);
                Main.others.get(i).nameLabel = otherNameLabels[i];
                Main.others.get(i).nameLabel = otherNameLabels[i];
                Main.others.get(i).roleImage = otherRoleImages[i];
            }
        }

        nightAction();
    }

    private static void nightAction() {
        logText.append("-------------------------------------------------\n");
        logText.append("GameMaster: 夜になりました。\n");
        nightLabel.setText("あなたの役職は" + Main.myself.role.getName() + "です。");

        switch (Main.myself.role) {
            case Villager:
                nightMainLabel.setText("村人の夜の行動はありません。");
                nightButton.setActionCommand("Close");
                break;

            case Werewolf:
                boolean flag = false;//他に人狼がいるか
                for (int i = 0; i < Main.others.size(); i++) {
                    if (Main.others.get(i).role == RoleType.Werewolf) {
                        nightMainLabel.setText(Main.others.get(i).name + "さんも人狼です。");
                        otherRoleImages[i].setIcon(getImageIcon(RoleType.Werewolf.getImageName(), 2));
                        flag = true;
                    }
                }
                if (!flag) {
                    nightMainLabel.setText("他に人狼のプレイヤーはいません。");
                }
                nightButton.setActionCommand("Close");
                break;

            case FortuneTeller:
                nightMainLabel.setText("役職を占う対象を選択してください。");
                for (int i = 0; i < Main.others.size(); i++) {
                    nightSelect.addItem(Main.others.get(i).name);
                }
                nightSelect.addItem("未使用の役職");
                nightMainPanel.add(nightSelect, BorderLayout.SOUTH);
                nightButton.setActionCommand("FortuneTeller");
                break;

            case PhantomThief:
                nightMainLabel.setText("役職を盗みたい対象を選択してください。");
                for (int i = 0; i < Main.others.size(); i++) {
                    nightSelect.addItem(Main.others.get(i).name);
                }
                nightMainPanel.add(nightSelect, BorderLayout.SOUTH);
                nightButton.setActionCommand("PhantomThief");
                break;

            default:
                break;
        }
        changeCommandPanel("night");
    }

    public static void dayTimeAction() {
        logText.append("-------------------------------------------------\n");
        logText.append("GameMaster: 朝になりました。\n");
        changeCommandPanel("chat");
        phaseImage.setIcon(getImageIcon("lib/width=1000/Sun.png", 2));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int time = Main.gameTime * 10;//Max time

            public void run() {
                timeLabel.setText("Time: " + time);
                time--;
                if (time < 0) {
                    timer.cancel();
                    timeLabel.setText("Time Over");
                    voteStart();
                }
            }
        }, 0, 1000);
    }

    private static void voteStart() {
        changeCommandPanel("vote");
        phaseImage.setIcon(getImageIcon("lib/width=1000/Vote.png", 2));
        logText.append("-------------------------------------------\n");
        logText.append("GameMaster: 投票時間になりました。\n");
        logScrollBar.setValue(logScrollBar.getMaximum());

        for (int i = 0; i < Main.others.size(); i++) {
            selectPlayer.addItem(Main.others.get(i).name);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 30;//Max time

            public void run() {
                voteTimeLabel.setText("投票してください : " + i);
                i--;
                if (i < 0) {
                    timer.cancel();
                    voteTimeLabel.setText("Time Over");
                    if (!voted) {
                        voteButton.doClick();
                    }
                }
            }
        }, 0, 1000);
    }

    public static void showResult() {
        //REVIEW ４人の場合のみ
        List<Player> sacrifices = new ArrayList<Player>();
        if (Main.myself.getVote >= 2) {
            sacrifices.add(Main.myself);
            System.out.println("me"+ Main.myself.getVote);
        }
        for (int i = 0; i < Main.others.size(); i++) {
            if (Main.others.get(i).getVote >= 2) {
                sacrifices.add(Main.others.get(i));
            }
            System.out.println(i + "" + Main.others.get(i).getVote);
        }
        if (sacrifices.size() == 0) {
            logText.append("投票の結果 誰も処刑されませんでした。 \n");
            logScrollBar.setValue(logScrollBar.getMaximum());
            for (int i = 0; i < Main.others.size(); i++) {
                if (Main.others.get(i).role == RoleType.Werewolf) {
                    chaos = true;
                }
            }
        } else if (sacrifices.size() == 1) {
            logText.append("投票の結果 " + sacrifices.get(0).name + "が処刑されました。\n");
            logScrollBar.setValue(logScrollBar.getMaximum());
            if (sacrifices.get(0).role != RoleType.Werewolf) {
                chaos = true;
            }
        } else {
            logText.append("投票の結果 " + sacrifices.get(0).name + "と" + sacrifices.get(1).name + "が処刑されました。\n");
            logScrollBar.setValue(logScrollBar.getMaximum());
            if (sacrifices.get(0).role != RoleType.Werewolf && sacrifices.get(1).role != RoleType.Werewolf) {
                chaos = true;
            }
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int time = 5;//Max time

            public void run() {
                logText.append("・\n");
                logScrollBar.setValue(logScrollBar.getMaximum());
                time--;

                if (time < 0) {
                    timer.cancel();
                    if (!chaos) {
                        logText.append("最終結果：　村人チームの勝利！\n\n");
                    } else {
                        logText.append("最終結果：　人狼チームの勝利！\n\n");
                    }
                    logScrollBar.setValue(logScrollBar.getMaximum());
                    for (int i = 0; i < otherRoleImages.length; i++) {
                        otherRoleImages[i].setIcon(getImageIcon(Main.others.get(i).role.getImageName(), 2));
                        if (Main.others.get(i).wasPhantomThief) {
                            otherNameLabels[i].setText(otherNameLabels[i].getText() + "(元怪盗)");
                        }
                    }
                    myRoleImage.setIcon(getImageIcon(Main.myself.role.getImageName(), 3));
                    unusedRoleImage0.setIcon(getImageIcon(unusedRoles[0].getImageName(), 2));
                    unusedRoleImage1.setIcon(getImageIcon(unusedRoles[1].getImageName(), 2));
                    if (Main.myself.wasPhantomThief) {
                        myNameLabel.setText(myNameLabel.getText() + "(元怪盗)");
                    }
                    changeCommandPanel("continue");
                }
            }
        }, 0, 1000);
    }

    private static ImageIcon getImageIcon(String imageName, int size) {
        ImageIcon icon = new ImageIcon(imageName);
        Image img = icon.getImage().getScaledInstance((int) (icon.getIconWidth() * Main.standardSize * 0.005 * size),
                -1,
                Image.SCALE_SMOOTH);
        icon.setImage(img);
        return icon;
    }

    private static void getMessage() {
        //TODO: 発言者によってテキストの色を変える
        //JScrollPane(new BoxLayout(y)) <- add(new JLabel(str))でできそう
        //roomViewでも同様
    }

    private static void changeCommandPanel(String panel) {
        layoutCommand.show(commandPanel, panel);
    }

    public static void writeLog(String message) {
        logText.append(message + "\n");
        logScrollBar.setValue(logScrollBar.getMaximum());
    }

    public static void showAlert(String message) {
        JOptionPane.showMessageDialog(MainWindow.frame, message);
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "setting":
                JOptionPane.showOptionDialog(this, null, "coming soon...", JOptionPane.PLAIN_MESSAGE,
                        JOptionPane.DEFAULT_OPTION, null, null, null);//REVIEW: いつか必要になる
                break;

            case "Close":
                changeCommandPanel("wait");
                if (Main.isHost) {
                    HostWorks.wait(TagType.DayTime);
                } else {
                    CommunicationAPI.send(TagType.DayTime, null);
                }
                break;

            case "FortuneTeller":
                if (nightSelect.getSelectedIndex() <= Main.others.size() - 1) {
                    nightMainLabel
                            .setText(nightSelect.getSelectedItem() + "の役職は"
                                    + Main.others.get(nightSelect.getSelectedIndex()).role.getName() + "です。");
                    otherRoleImages[nightSelect.getSelectedIndex()].setIcon(
                            getImageIcon(Main.others.get(nightSelect.getSelectedIndex()).role.getImageName(), 2));
                } else {
                    String text = nightSelect.getSelectedItem() + "は ";
                    for (int i = 0; i < unusedRoles.length; i++) {
                        text += unusedRoles[i].getName();
                        if (i != unusedRoles.length - 1) {
                            text += " と ";
                        }
                    }
                    text += " です。";
                    nightMainLabel.setText(text);
                    unusedRoleImage0.setIcon(getImageIcon(unusedRoles[0].getImageName(), 2));
                    unusedRoleImage1.setIcon(getImageIcon(unusedRoles[1].getImageName(), 2));
                }

                nightMainPanel.remove(nightSelect);
                nightButton.setActionCommand("Close");
                break;

            case "PhantomThief":
                nightMainLabel.setText(nightSelect.getSelectedItem() + "の役職を盗みました。");
                for (int i = 0; i < Main.others.size(); i++) {
                    if (Main.others.get(i).name == nightSelect.getSelectedItem()) {
                        Main.myself.role = Main.others.get(i).role;
                        Main.others.get(i).role = RoleType.PhantomThief;
                        otherRoleImages[i].setIcon(getImageIcon(Main.others.get(i).role.getImageName(), 2));
                        CommunicationAPI.send(TagType.PhantomThief,
                                "" + (Main.myself.identifier + (Main.others.get(i).identifier * Main.playersCount)));
                    }
                } //nightActionに書く
                myRoleImage.setIcon(getImageIcon(Main.myself.role.getImageName(), 3));
                nightMainPanel.remove(nightSelect);
                nightButton.setActionCommand("Close");
                Main.myself.wasPhantomThief = true;
                break;

            case "message":
                if (!messageField.getText().equals("")) {
                    logText.append(Main.myself.name + ":" + messageField.getText() + "\n");
                    logScrollBar.setValue(logScrollBar.getMaximum());
                    CommunicationAPI.send(TagType.ChatGame, messageField.getText());
                    messageField.setText("");
                }
                break;

            case "CO":
                logText.append(Main.myself.name + ":[カミングアウト] 私の役職は" + selectCO.getSelectedItem() + "です。\n");
                logScrollBar.setValue(logScrollBar.getMaximum());
                CommunicationAPI.send(TagType.ChatGame, "[カミングアウト] 私の役職は" + selectCO.getSelectedItem() + "です。");
                //TODO: 受信した時の画像変更
                break;

            case "vote":
                voted = true;
                if (Main.isHost) {
                    Main.others.get(selectPlayer.getSelectedIndex()).getVote++;
                    CommunicationAPI.send(TagType.VOTE, Main.others.get(selectPlayer.getSelectedIndex()).identifier + "");
                    HostWorks.wait(TagType.VOTE);
                } else {
                    CommunicationAPI.send(TagType.VOTE, Main.others.get(selectPlayer.getSelectedIndex()).identifier + "");
                }
                changeCommandPanel("wait");
                break;

            case "continueYes":
                MainWindow.changeView(ViewType.RoomView);//TODO: init
                break;

            case "continueNo":
                MainWindow.changeView(ViewType.HomeView);//TODO init
                break;

            default:
                System.out.println("default: どこかで無効なボタンが押された");//ANCHOR: error
                break;
        }
    }
}