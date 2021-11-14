import java.awt.event.*;

import ui.PlayerWindow;
import ui.AddSongWindow;


public class Player {
    String songID="0";
    String [][] listademusicas;
    AddSongWindow addSongWindow;
    PlayerWindow window;
    boolean isplaying = false;
    ControlPlayer thread;


    public Player() {
        ActionListener buttonListenerPlayNow = e -> start();
        ActionListener buttonListenerRemove = e -> remove();
        ActionListener buttonListenerAddSong = e -> add();
        ActionListener buttonListenerPlayPause = e -> playpause();
        ActionListener buttonListenerStop = e -> stop();
        ActionListener buttonListenerNext = e -> next();
        ActionListener buttonListenerPrevious = e -> prev();
        ActionListener buttonListenerShuffle = e -> shuffle();
        ActionListener buttonListenerRepeat = e -> repeat();
        MouseListener scrubberListenerClick = new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e){}

            @Override
            public void mousePressed(MouseEvent e){}

            @Override
            public void mouseEntered(MouseEvent e){}

            @Override
            public void mouseExited(MouseEvent e){}
        };

        MouseMotionListener scrubberListenerMotion = new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e){}

        };

        String windowTitle = "Player de Música";



        this.window = new PlayerWindow(
            buttonListenerPlayNow,
            buttonListenerRemove,
            buttonListenerAddSong,
            buttonListenerPlayPause,
            buttonListenerStop,
            buttonListenerNext,
            buttonListenerPrevious,
            buttonListenerShuffle,
            buttonListenerRepeat,
            scrubberListenerClick,
            scrubberListenerMotion,
            windowTitle,
            getQueueasArray()
            );





    }

    String[][] array = new String[100][100];


    public String setSongID(String IDprevious ) {
        int IDpreviousint = Integer.parseInt(IDprevious);
        IDpreviousint = IDpreviousint + 1;
        IDprevious = String.valueOf(IDpreviousint);
        this.songID=IDprevious;
        return IDprevious;
    };
    public void start() {
        int musicaselecionada = this.window.getSelectedSongID();
        int tamanholistademusicas = this.listademusicas != null ? this.listademusicas.length : 0; //Mudar essa parte
        for (int i=0; i < tamanholistademusicas ; i++) {
            if (this.listademusicas[i][6].equals(String.valueOf(musicaselecionada))) {
                this.window.updatePlayingSongInfo(
                        this.listademusicas[i][0], this.listademusicas[i][1], this.listademusicas[i][2]);
                break;
            }
        }

        this.window.enableScrubberArea();
        this.isplaying=true;
        this.window.updatePlayPauseButton(true);
        this.thread = new ControlPlayer(this.window,true,true,false,0,Integer.parseInt(listademusicas[musicaselecionada-1][5]),Integer.parseInt(listademusicas[musicaselecionada-1][6]), tamanholistademusicas);
        this.thread.start();


    };




    public void playpause() {
        if(this.isplaying){
            this.window.updatePlayPauseButton(false);
            this.isplaying=false;
        }else {
            this.window.updatePlayPauseButton(true);
            this.isplaying=true;
        }
    };

    public void remove() {
        int IDmusicaremovida = this.window.getSelectedSongID();

        int tamanholistademusicas = this.listademusicas != null ? this.listademusicas.length : 0; //Mudar essa parte
        String [][] novalistademusicas = new String[tamanholistademusicas-1][7];


        for(int j=0; j < tamanholistademusicas-1; j++ ) {
            if (j < IDmusicaremovida - 1) {
                novalistademusicas[j] = listademusicas[j];
            }
            else{
                novalistademusicas[j] = listademusicas[j+1];
            }

        }


        this.listademusicas=novalistademusicas;
        window.updateQueueList(novalistademusicas);

    };



    public void add() {
                ActionListener addSong = e -> {
                    String[] infosong = addSongWindow.getSong();
                    int tamlistademusicas = this.listademusicas != null ? this.listademusicas.length : 0; //Mudar essa parte

                    String[][] novalistademusicas = new String[tamlistademusicas+1][7];
                    for(int i=0;i<tamlistademusicas;i++){
                        novalistademusicas[i]=listademusicas[i];
                    }

                    novalistademusicas[tamlistademusicas] = infosong;
                    this.listademusicas = novalistademusicas;
                    window.updateQueueList(novalistademusicas);

                };
                this.addSongWindow = new AddSongWindow(setSongID(songID), addSong, this.window.getAddSongWindowListener());

    };



    public void stop() {};

    public void next() {};

    public void prev() {};

    public void shuffle() {};

    public void repeat() {};

    public String[][] getQueueasArray(){
        return array;
    };

}

