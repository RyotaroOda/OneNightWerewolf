//Written by 小田竜太郎
import javax.swing.*;
import java.awt.*;

public class MainWindow {

    public static JFrame frame;
    public static JPanel cardPanel;
    public static CardLayout layout;

    public MainWindow() {
        frame = new JFrame();
        frame.setTitle("ワンナイト人狼");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//xでウィンドウが閉じた時の処理
        frame.setBounds(0, 0, 160 * Main.standardSize, 90 * Main.standardSize);//ウィンドウサイズ

        cardPanel = new JPanel();
        layout = new CardLayout();
        cardPanel.setLayout(layout);

        cardPanel.add(new HomeView(), ViewType.HomeView.name());
        cardPanel.add(new RoomView(), ViewType.RoomView.name());
        cardPanel.add(new GameView(), ViewType.GameView.name());

        var pane = frame.getContentPane();
        pane.add(cardPanel, BorderLayout.CENTER);
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void changeView(ViewType view) {
        if (view == ViewType.RoomView) {
            RoomView.loadView();
        } else if (view == ViewType.GameView) {
            GameView.loadView();
        } else if (view == ViewType.HomeView) {

        }
        layout.show(cardPanel, view.name());
    }
}