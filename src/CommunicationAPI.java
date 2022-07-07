//Written by 小田竜太郎

public class CommunicationAPI {
    public static void send(TagType tag, String data) {
        if (tag == TagType.ChatGame || tag == TagType.ChatRoom) {
            data = tag.getTag() + Main.myself.name + ":" + data;
        } else {
            data = tag.getTag() + data;
        }
        System.out.println("API: " + data);
        if (Main.isHost) {
            HostWorks.send(data);
        } else {
            GuestWorks.send(data);
        }
    }
}
