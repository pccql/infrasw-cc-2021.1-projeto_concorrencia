import ui.PlayerWindow;

public class ControlPlayer extends Thread{
    PlayerWindow window;
    boolean isActive;
    boolean isPlaying;
    boolean isRepeat;
    int currentTime;
    int totalTime;
    int songId;
    int queueSize;


    public ControlPlayer( PlayerWindow window,
            boolean isActive,
            boolean isPlaying,
            boolean isRepeat,
            int currentTime,
            int totalTime,
            int songId,
            int queueSize){

        this.window = window;
        this.isActive = isActive;
        this.isPlaying = isPlaying;
        this.isRepeat = isRepeat;
        this.currentTime = currentTime;
        this.totalTime = totalTime;
        this.songId = songId;
        this.queueSize = queueSize;


    }
    @Override

    public void run(){
        for(int i=0;i<=this.totalTime;i++) {
            try {
                this.window.updateMiniplayer(
                        this.isActive,
                        this.isPlaying,
                        this.isRepeat,
                        this.currentTime,
                        this.totalTime,
                        this.songId,
                        this.queueSize)
                ;
                Thread.sleep(1000);
                this.currentTime++;
            }
            catch (Exception error){
                this.window.updateMiniplayer(
                        true,
                        false,
                        false,
                        this.currentTime,
                        this.totalTime,
                        this.songId,
                        this.queueSize
                );
                break;

            }


        }}
        public int getCurrentTime(){
            return this.currentTime;
        }

        public int getTotalTime(){
            return this.totalTime;
        }

        public int getCurrentSongId(){
            return this.songId;
        }
    };

