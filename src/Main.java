//Written by 小田竜太郎

import java.util.*;

public class Main {
    public static int standardSize;
    public static List<Player> others;
    public static Player myself;
    public static int gameTime; //[M]
    public static int playersCount;
    public static int[] roleSlots;
    public static boolean isHost;

    public static void main(String[] args) {
        standardSize = 4;
        others = new ArrayList<Player>();
        myself = new Player();
        gameTime = 1;
        playersCount = 4;
        roleSlots = new int[RoleType.values().length];//役職ごとの人数
        roleSlots[RoleType.Villager.ordinal()] = 2;
        roleSlots[RoleType.Werewolf.ordinal()] = 2;
        roleSlots[RoleType.FortuneTeller.ordinal()] = 1;
        roleSlots[RoleType.PhantomThief.ordinal()] = 1;
  
        MainWindow window = new MainWindow();
        window.show();
    }
}
