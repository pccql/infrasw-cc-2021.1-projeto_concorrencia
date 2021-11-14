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
        for(int i=0;i<=this.totaltime;i++) {
            try {
                this.window.updateMiniplayer(
                        this.isActive,
                        this.isPlaying,
                        this.isRepeat,
                        this.currenttime,
                        this.totaltime,
                        this.songID,
                        this.queueSize)
                ;
                Thread.sleep(1000);
                this.currenttime++;
            }
            catch (Exception error){
                this.window.updateMiniplayer(
                        true,
                        false,
                        false,
                        this.currenttime,
                        this.totaltime,
                        this.songID,
                        this.queueSize
                );
                break;

            }


        }}
        public int getCurrentTime(){
            return this.currenttime;
        }

        public int getTotalTime(){
            return this.totaltime;
        }

        public int getCurrentSongId(){
            return this.songID;
        }
    };

