import java.awt.event.ActionListener;
import ui.PlayerWindow;


public class Player {
    public Player() {

        ActionListener buttonListenerPlayNow = e -> start();
        ActionListener buttonListenerRemove = e -> remove();
        ActionListener buttonListenerAddSong = e -> {
            addSongWindow = new AddSongWindow(
                createID(), 
                buttonListenerAddSong, 
                window.getAddSongWindowListener()
                );
        };

        PlayerWindow window = new PlayerWindow(
            buttonListenerAddSong,
            buttonListenerPlayNow, 
            buttonListenerRemove
        );

        ActionListener actionListener = e -> {};

        AddSongWindow addSongWindow = new AddSongWindow(
            createID(), 
            buttonListenerAddSong, 
            window.getAddSongWindowListener()
            );

    }

    int songID = 1;

    public int createID() {
        songID += 1;
        return songID;
    }

    public void start() {};

    public void remove() {};

    public void add() {};
}

