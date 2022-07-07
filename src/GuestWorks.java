//Written by 小田竜太郎

import java.io.*;
import java.util.*;

public class GuestWorks extends Thread {

    private static final Client client = new Client();
    Player newPLayer = new Player();

    public void run() {
        try {
            client.connectServer();
            client.write(TagType.EnterRoom.getTag() + Main.myself.name);

            // 参加者の情報を受信したらテキストエリアを更新する
            // 待合室の終了通知を受信したら画面下部のラベルを更新して次に進む
            while (true) {
                while (client.isReadReady()) {
                    String input = client.read();
                    System.out.println(input);
                    if (input.startsWith(TagType.EnterRoom.getTag())) {
                        switch (input.substring(TagType.EnterRoom.getCharCount())) {
                            case "NAMEINUSE":
                                // JOptionPane.showMessageDialog(waitRoom, "既に使用されている名前です");
                                RoomView.showAlert("既に使用されている名前です。");
                                client.close();
                                System.exit(1);
                                return;
                            case "INVALIDNAME":
                                // JOptionPane.showMessageDialog(waitRoom, "無効な名前です");
                                RoomView.showAlert("無効な名前です無効な名前です。");
                                client.close();
                                System.exit(1);
                                return;

                            default:
                                RoomView.writeLog("入室に成功しました。");
                                RoomView.writeLog("---メンバーリスト---");
                                break;
                        }
                    }

                    else if (input.startsWith(TagType.MEMBER.getTag())) {
                        RoomView.showPlayer(input.substring(TagType.MEMBER.getCharCount()));

                    }

                    else if (input.startsWith(TagType.NewMember.getTag())) {
                        RoomView.addPlayer(input.substring(TagType.NewMember.getCharCount()));

                    }

                    else if (input.startsWith(TagType.ChatRoom.getTag())) {
                        String str = input.substring(TagType.ChatRoom.getCharCount());
                        if (!str.startsWith(Main.myself.name)) {
                            RoomView.writeLog(str);
                        }
                    }

                    else if (input.startsWith(TagType.ChatGame.getTag())) {
                        String str = input.substring(TagType.ChatGame.getCharCount());
                        if (!str.startsWith(Main.myself.name)) {
                            GameView.writeLog(str);
                        }
                    }

                    else if (input.startsWith(TagType.ShareStart.getTag())) {
                        roomEndLoop: while (true) {
                            if (client.isReadReady()) {
                                input = client.read();
                                System.out.println(input);
                            }
                            if (input.startsWith(TagType.READY.getTag())) {
                                for (int i = 0; i < Main.others.size(); i++) {
                                    System.out.println(Main.others.get(i).name + Main.others.get(i).role
                                            + Main.others.get(i).identifier);
                                    ;//ANCHOR
                                }
                                RoomView.endView();
                                break roomEndLoop;
                            }

                            if (input.startsWith(TagType.ShareNext.getTag())) {
                                if (newPLayer.name.equals(Main.myself.name)) {
                                    Main.myself = newPLayer;
                                } else {
                                    Main.others.add(newPLayer);
                                }
                                newPLayer = new Player();
                                continue;
                            }

                            else if (input.startsWith(TagType.ShareName.getTag())) {
                                newPLayer.name = input.substring(TagType.ShareName.getCharCount());
                            }

                            else if (input.startsWith(TagType.ShareRole.getTag())) {
                                int n = Integer.parseInt(input.substring(TagType.ShareRole.getCharCount()));
                                newPLayer.role = RoleType.values()[n];
                            }

                            else if (input.startsWith(TagType.ShareIdentifier.getTag())) {
                                newPLayer.identifier = Integer
                                        .parseInt(input.substring(TagType.ShareIdentifier.getCharCount()));

                            }
                        }
                    }

                    else if (input.startsWith(TagType.DayTime.getTag())) {
                        GameView.dayTimeAction();
                    }

                    else if (input.startsWith(TagType.PhantomThief.getTag())) {
                        int n = Integer.parseInt(input.substring(TagType.PhantomThief.getCharCount()));
                        int m = n % Main.playersCount;//元怪盗
                        if (n != Main.myself.identifier) {
                            if (m != Main.myself.identifier) {
                                n -= m;//対象
                                n /= Main.playersCount;
                                RoleType stolen = null;
                                for (int i = 0; i < Main.others.size(); i++) {
                                    if (Main.others.get(i).identifier == n) {
                                        stolen = Main.others.get(i).role;
                                        Main.others.get(i).role = RoleType.PhantomThief;
                                    }
                                }
                                for (int i = 0; i < Main.others.size(); i++) {
                                    if (Main.others.get(i).identifier == m) {
                                        Main.others.get(i).wasPhantomThief = true;
                                        Main.others.get(i).role = stolen;
                                    }
                                }
                            }
                        }
                    }

                    else if (input.startsWith(TagType.VOTE.getTag())) {
                        if (input.substring(TagType.VOTE.getCharCount()).startsWith("FIN")) {
                            GameView.showResult();
                        } else {
                            int n = Integer.parseInt(input.substring(TagType.VOTE.getCharCount()));
                            if (Main.myself.identifier == n) {
                                Main.myself.getVote++;
                            } else {
                                for (int i = 0; i < Main.others.size(); i++) {
                                    if (Main.others.get(i).identifier == n) {
                                        Main.others.get(i).getVote++;
                                    }
                                }
                            }
                        }
                    }

                    else if (input.startsWith(TagType.GameEnd.getTag())) {
                    }
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (ConnectionClosedException e) {
            e.printStackTrace();
            client.close();
            RoomView.showAlert("ホストとの接続が切断されました。");
            System.exit(1);
        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            client.close();
            RoomView.showAlert("ホストとの通信に失敗しました。");
            System.exit(1);
        }
    }

    public static void send(String data) {
        try {
            client.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
            RoomView.showAlert("ホストとの通信に失敗しました。");
            System.exit(1);
        }
    }
}