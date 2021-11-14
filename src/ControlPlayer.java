import ui.PlayerWindow;

public class ControlPlayer extends Thread{
    PlayerWindow window;
    boolean isActive;
    boolean isPlaying;
    boolean isRepeat;
    int currenttime;
    int totaltime;
    int songID;
    int queueSize;


    public ControlPlayer( PlayerWindow window,
            boolean isActive,
            boolean isPlaying,
            boolean isRepeat,
            int currenttime,
            int totaltime,
            int songID,
            int queueSize){

        this.window = window;
        this.isActive = isActive;
        this.isPlaying = isPlaying;
        this.isRepeat = isRepeat;
        this.currenttime = currenttime;
        this.totaltime = totaltime;
        this.songID = songID;
        this.queueSize = queueSize;


    }
    @Override
    public void run(){
        for(int i=1;i<=this.totaltime;i++) {
            try {
                Thread.sleep(1000);
                this.currenttime++;
                this.window.updateMiniplayer(this.isActive, this.isPlaying, this.isRepeat, i, this.totaltime, this.songID, this.queueSize);
            }
            catch (Exception error){
                this.window.updateMiniplayer(this.isActive, this.isPlaying, this.isRepeat, i, this.totaltime, this.songID, this.queueSize);
                Thread.interrupted();
            }


        }
    };
}
