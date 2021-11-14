import java.awt.event.*;

import ui.PlayerWindow;
import ui.AddSongWindow;


public class Player {
    PlayerWindow window;
    String [][] listademusicas;
    int currentime;
    String songID = "0";
    AddSongWindow addSongWindow;
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

        String windowTitle = "Tocador de Música";


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
            windowTitle,this.listademusicas
            );





    }

    public String setSongID(String IDprevious ) {
        int IDpreviousint = Integer.parseInt(IDprevious);
        IDpreviousint = IDpreviousint + 1;
        String IDatual = String.valueOf(IDpreviousint);
        this.songID=IDatual;
        return this.songID;
    };

    public void start() {
        this.currentime=0;
        int musicaselecionada = this.window.getSelectedSongID();
        int tamanholistademusicas = this.listademusicas.length;
        int songTime = 0;

        // Checando a música escolhida na matriz para colher informações

        for (int i=0; i < tamanholistademusicas ; i++) {
            String songID = this.listademusicas[i][6];
            if (songID.equals(String.valueOf(musicaselecionada))) {
                this.window.updatePlayingSongInfo(
                        this.listademusicas[i][0], this.listademusicas[i][1], this.listademusicas[i][2]);
                        songTime = Integer.parseInt(this.listademusicas[i][5]);
                break;
            }
        }
        this.window.enableScrubberArea();
        this.isplaying=true;
        this.window.updatePlayPauseButton(true);

        //Uso de Thread para ser possível controlar a atualização do painel enquanto a música é executada
        //e deixar as outras funções também disponíveis para o usuário pode utilizar "simultaneamente"


        this.thread = new ControlPlayer(this.window,
                true,
                true,
                false,
                this.currentime,
                songTime, //Tempo Total
                musicaselecionada, //SongID
                tamanholistademusicas);
                
        this.thread.start();
        //Fazer a thread iniciar somente depois da  atualização das infomações:
        //-Habilitar a Área de MiniPlayer
        //-Botao de Play/Pause
    };


    public void playpause() {
        int tamanholistademusicas = this.listademusicas != null ? this.listademusicas.length : 0;
        int musicaselecionada = this.window.getSelectedSongID();
        if(this.isplaying){
            this.window.updatePlayPauseButton(false);
            this.thread.interrupt(); // Setado a flag de interrupção da thread quando o botão de pause é apertado
            this.isplaying=false;
        }else {
            this.window.updatePlayPauseButton(true);
            //Play apertado, seta-se nova thread com o CurrentTime que tinha parado anteriormente
            this.thread = new ControlPlayer(this.window,
                    true,
                    true,
                    false,
                    this.thread.getCurrentTime(),
                    this.thread.getTotalTime(),//Tempo Total da música em execução
                    Integer.parseInt(this.songID),
                    tamanholistademusicas);
            this.thread.start();
            this.isplaying=true;
        }
    };

    public void remove() {
        int IDmusicaremovida = this.window.getSelectedSongID();
        int tamanholistademusicas = this.listademusicas.length;
        String [][] novalistademusicas = new String[tamanholistademusicas-1][7];

        if (this.thread != null && IDmusicaremovida == this.thread.getCurrentSongId()){
            this.thread.interrupt();
            this.window.disableScrubberArea();
            this.isplaying=false;
            this.window.updateMiniplayer(
                    false,
                    false,
                    false,
                    0,
                    0,
                    0,
                    tamanholistademusicas - 1
            );
        }

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
                int tamanholistademusicas = this.listademusicas != null ? this.listademusicas.length : 0;
                ActionListener addSong = e -> {
                    //Guardando informações da música num array
                    String[] infosong = addSongWindow.getSong();

                    // Fazendo a cópia da lista de músicas antes de adicionar a nova música
                    String[][] novalistademusicas = new String[tamanholistademusicas+1][7];
                    for(int i=0;i<tamanholistademusicas;i++){
                        novalistademusicas[i]=listademusicas[i];
                    }

                    //Adicionando as informações da nova música na lista de músicas
                    novalistademusicas[tamanholistademusicas] = infosong;

                    //Atualizando a lista de músicas
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


}

