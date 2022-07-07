//Written by 小田竜太郎

import java.awt.*;

import javax.swing.JLabel;

public class Player {//構造体
    String name;
    RoleType role;
    int identifier;
    Color color = Color.black;
    boolean wasPhantomThief = false;
    int getVote = 0;
    JLabel nameLabel;
    JLabel roleImage;
}