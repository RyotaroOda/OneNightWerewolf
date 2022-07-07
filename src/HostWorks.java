//Written by 小田竜太郎


import java.io.*;
import java.util.*;

public class HostWorks extends Thread {

    private static ArrayList<String> guestNames = new ArrayList<String>();

    private static final Server server = new Server();
    private static final int minPlayers = 4;
    private static final int maxPlayers = 4;

    private static int waitPlayers = 0;

    public void run() {
        int lastNumOfClients = server.getTotalClients();
        try {
            while (true) {
                server.waitConnection();

                if (server.getTotalClients() > lastNumOfClients && server.getTotalClients() < maxPlayers) { // 接続が成功した場合
                    String input = server.blockingRead(lastNumOfClients, 200);
                    System.out.println(input);//ANCHOR input

                    if (input.startsWith(TagType.EnterRoom.getTag())) {
                        input = input.substring(10);

                        // 参加済みのプレイヤーと同じ名前や予約済みの名前だったら接続を閉じる
                        //投票とかの時に同じ名前があるとめんどい
                        if (input.equals(Main.myself.name) || guestNames.contains(input)) {
                            server.write(lastNumOfClients, TagType.EnterRoom.getTag() + "NAMEINUSE");
                            server.disconnectLatest();
                            continue;

                        } else {
                            server.write(lastNumOfClients, TagType.EnterRoom.getTag() + "OK");
                            RoomView.addPlayer(input);
                            server.write(lastNumOfClients, TagType.MEMBER.getTag() + Main.myself.name + "（ホスト）");
                            for (int i2 = 0; i2 < lastNumOfClients; i2++) {
                                server.write(lastNumOfClients, TagType.MEMBER.getTag() + guestNames.get(i2));
                            }
                            for (int i2 = 0; i2 < lastNumOfClients; i2++) {
                                server.write(i2, TagType.NewMember.getTag() + input);
                            }
                            lastNumOfClients = server.getTotalClients();
                            Player newPLayer = new Player();
                            newPLayer.name = input;
                            newPLayer.identifier = lastNumOfClients;
                            Main.others.add(newPLayer);
                            guestNames.add(input);
                        }
                    }
                }

                for (int i = 0; i < server.getTotalClients(); i++) {
                    while (server.isReadReady(i)) {

                        String input = server.read(i);
                        System.out.println(input);//ANCHOR input

                        if (input.startsWith(TagType.ChatRoom.getTag())) {
                            RoomView.writeLog(input.substring(TagType.ChatRoom.getCharCount()));
                            server.writeAll(input);
                        }

                        else if (input.startsWith(TagType.ChatGame.getTag())) {
                            GameView.writeLog(input.substring(TagType.ChatGame.getCharCount()));
                            server.writeAll(input);
                        }

                        else if (input.startsWith(TagType.READY.getTag())) {
                            waitPlayers++;
                            if (waitPlayers == lastNumOfClients && lastNumOfClients + 1 >= minPlayers) {
                                RoomView.allReady();
                                waitPlayers = 0;
                            }
                        }

                        else if (input.startsWith(TagType.PhantomThief.getTag())) {
                            int n = Integer.parseInt(input.substring(TagType.PhantomThief.getCharCount()));
                            int m = n % Main.playersCount;//元怪盗
                            if (Main.myself.identifier != m) {
                                n -= m;//対象
                                n /= Main.playersCount;
                                RoleType stolen = null;
                                if (Main.myself.identifier == n) {
                                    Main.myself.role = RoleType.PhantomThief;
                                } else {
                                    for (int i2 = 0; i2 < Main.others.size(); i2++) {
                                        if (Main.others.get(i2).identifier == n) {
                                            stolen = Main.others.get(i2).role;
                                            Main.others.get(i2).role = RoleType.PhantomThief;
                                        }
                                    }
                                }
                                for (int i2 = 0; i2 < Main.others.size(); i2++) {
                                    if (Main.others.get(i2).identifier == m) {
                                        Main.others.get(i2).wasPhantomThief = true;
                                        Main.others.get(i2).role = stolen;
                                    }
                                }
                            }
                            server.writeAll(input);
                        }

                        else if (input.startsWith(TagType.VOTE.getTag())) {
                            int n = Integer.parseInt(input.substring(TagType.VOTE.getCharCount()));
                            if (n == Main.myself.identifier) {
                                Main.myself.getVote++;
                            } else {
                                for (int j = 0; j < Main.others.size(); j++) {
                                    if (Main.others.get(j).identifier == n) {
                                        Main.others.get(j).getVote++;
                                    }
                                }
                            }
                            server.writeAll(input);
                            wait(TagType.VOTE);
                        }

                        else if (input.startsWith(TagType.DayTime.getTag())) {
                            wait(TagType.DayTime);
                        }
                    }
                }
            }

        } catch (ConnectionClosedException e) {
            e.printStackTrace();
            server.closeAll();
            RoomView.showAlert("参加者との接続が切断されました。");
            System.exit(1);
        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            server.closeAll();
            // JOptionPane.showMessageDialog(waitRoom, "参加者との通信に失敗しました");
            RoomView.showAlert("参加者との通信に失敗しました。");
            System.exit(1);
        }
    }

    public static void send(String data) {
        try {
            server.writeAll(data);
        } catch (Exception e) {
            e.printStackTrace();
            server.closeAll();
            RoomView.showAlert("ゲストとの通信に失敗しました。");
            System.exit(1);
        }
    }

    public static void wait(TagType tag) {
        try {
            waitPlayers++;
            if (waitPlayers >= Main.playersCount) {
                switch (tag) {
                    case DayTime:
                        server.writeAll(TagType.DayTime.getTag());
                        GameView.dayTimeAction();
                        break;

                    case VOTE:
                        server.writeAll(TagType.VOTE.getTag() + "FIN");
                        GameView.showResult();
                        break;

                    default:
                        System.err.println("tagError");//ANCHOR err
                        break;
                }
                waitPlayers = 0;
            }
        } catch (ConnectionClosedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void assignmentRole() {
        try {
            List<RoleType> allRoles = new ArrayList<RoleType>();

            for (int i = 0; i < Main.roleSlots.length; i++) {
                for (int j = 0; j < Main.roleSlots[i]; j++) {
                    allRoles.add(RoleType.values()[i]);
                }
            }
            Random rand = new Random();
            int n = rand.nextInt(allRoles.size());
            Main.myself.role = allRoles.get(n);
            allRoles.remove(n);

            for (int i = 0; i < Main.others.size(); i++) {
                n = rand.nextInt(allRoles.size());
                Main.others.get(i).role = allRoles.get(n);
                allRoles.remove(n);
            }

            sleep(1000);
            server.writeAll(TagType.ShareStart.getTag());//ANCHOR role
            System.out.println(TagType.ShareStart.getTag());
            server.writeAll(TagType.ShareName.getTag() + Main.myself.name);
            System.out.println(TagType.ShareName.getTag() + Main.myself.name);
            server.writeAll(TagType.ShareRole.getTag() + Main.myself.role.ordinal());
            System.out.println(TagType.ShareRole.getTag() + Main.myself.role.ordinal());
            server.writeAll(TagType.ShareIdentifier.getTag() + Main.myself.role.ordinal());
            System.out.println(TagType.ShareIdentifier.getTag() + Main.myself.identifier);
            server.writeAll(TagType.ShareNext.getTag());
            System.out.println(TagType.ShareNext.getTag());

            for (int i = 0; i < Main.others.size(); i++) {
                server.writeAll(TagType.ShareName.getTag() + Main.others.get(i).name);
                System.out.println(TagType.ShareName.getTag() + Main.others.get(i).name);
                server.writeAll(TagType.ShareRole.getTag() + Main.others.get(i).role.ordinal());
                System.out.println(TagType.ShareRole.getTag() + Main.others.get(i).role.ordinal());
                server.writeAll(TagType.ShareIdentifier.getTag()
                        + Main.others.get(i).role.ordinal());
                System.out.println(TagType.ShareIdentifier.getTag() + Main.others.get(i).identifier);
                server.writeAll(TagType.ShareNext.getTag());
                System.out.println(TagType.ShareNext.getTag());

            }
            server.writeAll(TagType.READY.getTag());
            System.out.println("READY:");//ANCHOR ready

            RoomView.endView();

        } catch (ConnectionClosedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}