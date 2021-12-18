import java.awt.event.*;

import ui.PlayerWindow;
import ui.AddSongWindow;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Player {
    PlayerWindow window;
    String [][] songList;
    int currentTime;
    String songId = "0";
    AddSongWindow addSongWindow;
    boolean isPlaying = false;
    boolean isRepeat = false;
    boolean isRandom = false;
    Integer [] shuffleOrder;
    int shuffleId = 0;
    ControlPlayer thread;
    ReentrantLock lock = new ReentrantLock();


    public Player() {
        ActionListener buttonListenerPlayNow = e -> start();
        ActionListener buttonListenerRemove = e -> remove();
        ActionListener buttonListenerAddSong = e -> add();
        ActionListener buttonListenerPlayPause = e -> playPause();
        ActionListener buttonListenerStop = e -> stop();
        ActionListener buttonListenerNext = e -> next();
        ActionListener buttonListenerPrevious = e -> prev();
        ActionListener buttonListenerShuffle = e -> shuffle();
        ActionListener buttonListenerRepeat = e -> repeat();
        MouseListener scrubberListenerClick = new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e){
                released();
            }

            @Override
            public void mousePressed(MouseEvent e){
                dragged();

            }

            @Override
            public void mouseEntered(MouseEvent e){

            }

            @Override
            public void mouseExited(MouseEvent e){}
        };

        MouseMotionListener scrubberListenerMotion = new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e) {
                dragged();


            }

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
                windowTitle,
                this.songList
        );
    }

    public String setSongId(String previousId ) {
        this.songId = String.valueOf(Integer.parseInt(previousId) + 1);
        return this.songId;
    };

    public void start() {
        try {
            lock.lock();
            this.currentTime=0;
            int selectedSong = this.window.getSelectedSongID();
            int songListSize = this.songList.length;
            int songTime = 0;

            // Checando a música escolhida na matriz para colher informações

            for (int i=0; i < songListSize ; i++) {
                String songId = this.songList[i][6];
                if (songId.equals(String.valueOf(selectedSong))) {
                    this.window.updatePlayingSongInfo(
                            this.songList[i][0], this.songList[i][1], this.songList[i][2]);
                    songTime = Integer.parseInt(this.songList[i][5]);
                    break;
                }
            }
            this.window.enableScrubberArea();
            this.isPlaying=true;
            this.window.updatePlayPauseButton(true);

            //Uso de Thread para ser possível controlar a atualização do painel enquanto a música é executada
            //e deixar as outras funções também disponíveis para o usuário pode utilizar "simultaneamente"

            if (this.thread != null) {
                this.thread.interrupt();
            }
            this.thread = new ControlPlayer(this.window,
                    true,
                    true,
                    false,
                    this.currentTime,
                    songTime, //Tempo Total
                    selectedSong, //SongID
                    songListSize);

            this.thread.start();
            //Fazer a thread iniciar somente depois da  atualização das infomações:
            //-Habilitar a Área de MiniPlayer
            //-Botao de Play/Pause

        } finally {
            lock.unlock();
        }

    };


    public void playPause() {
        try {
            lock.lock();
            int songListSize = this.songList != null ? this.songList.length : 0;
            if(this.isPlaying){
                SwingUtilities.invokeLater(() -> this.window.updatePlayPauseButton(false));
                this.thread.interrupt(); // Setado a flag de interrupção da thread quando o botão de pause é apertado
                this.isPlaying=false;
            }else {
                SwingUtilities.invokeLater(() ->  this.window.updatePlayPauseButton(true));
                //Play apertado, seta-se nova thread com o CurrentTime que tinha parado anteriormente
                this.thread = new ControlPlayer(this.window,
                        true,
                        true,
                        false,
                        this.thread.getCurrentTime(),
                        this.thread.getTotalTime(),//Tempo Total da música em execução
                        Integer.parseInt(this.songId),
                        songListSize);
                this.thread.start();
                this.isPlaying=true;
            };
        } finally {
            lock.unlock();
        }

    }


    public void remove() {
        try{
            lock.lock();
            int songId = this.window.getSelectedSongID();
            int songListSize = this.songList.length;
            String [][] newSongList = new String[songListSize-1][7];

            if(this.thread!=null){
                this.thread.setQueueSize(songListSize-1);}

            if (this.thread != null && songId == this.thread.getCurrentSongId()){
                this.thread.interrupt();
                this.window.disableScrubberArea();
                this.isPlaying=false;
                this.window.updateMiniplayer(
                        false,
                        false,
                        false,
                        0,
                        0,
                        0,
                        songListSize - 1
                );
            }

            for(int j=0; j < songListSize-1; j++ ) {
                if (j < songId - 1) {
                    newSongList[j] = songList[j];
                    newSongList[j][6]=String.valueOf(j+1);

                }
                else{
                    newSongList[j] = songList[j+1];
                    newSongList[j][6]=String.valueOf(j+1);
                };
            }
            this.songList=newSongList;

            if(this.thread != null && this.thread.getCurrentSongId() > songId){
                this.thread.setSongId(this.thread.getCurrentSongId()-1);
            }

            this.songId = String.valueOf(Integer.parseInt(this.songId)-1);
            window.updateQueueList(newSongList);

        } finally {
            lock.unlock();
        }

    };



    public void add() {
        int songListSize = this.songList != null ? this.songList.length : 0;
        ActionListener addSong = e -> {
            try {
                lock.lock();
                //Guardando informações da música num array
                String[] songData = addSongWindow.getSong();

                // Fazendo a cópia da lista de músicas antes de adicionar a nova música
                String[][] newSongList = new String[songListSize+1][7];
                for(int i=0;i<songListSize;i++){
                    newSongList[i]=songList[i];
                }

                //Adicionando as informações da nova música na lista de músicas
                newSongList[songListSize] = songData;

                //Atualizando a lista de músicas
                this.songList = newSongList;
                window.updateQueueList(newSongList);

                if (this.thread != null) {
                    this.thread.setQueueSize(songListSize + 1);
                }
            } finally {
                lock.unlock();
            }
        };

        try {
            lock.lock();
            this.addSongWindow = new AddSongWindow(setSongId(songId), addSong, this.window.getAddSongWindowListener());
        } finally {
            lock.unlock();
        }



    };

    public void stop() {
        try{
            lock.lock();
            this.thread.interrupt();
            this.thread = null;
            this.window.disableScrubberArea();
            this.window.resetMiniPlayer();
            shuffleId = 0;
        } finally {
            lock.unlock();
        }
    };

    public void next() {
        try{
            lock.lock();
            this.thread.interrupt();
            int songListSize = this.songList.length;
            if(shuffleId == songListSize || shuffleId == songListSize-1 && isRepeat && isRandom){
                shuffleId = -1;
            }

            int nextSong = isRandom ? shuffleOrder[shuffleId + 1] : this.thread.getCurrentSongId()+1;
            int songTime = 0;
            int currentSong = this.thread.getCurrentSongId();

            if (isRandom) {
                shuffleId++;
            }

            if(!isRandom && isRepeat && currentSong == songListSize){
                nextSong = 1;
            }


            if(isRandom && isRepeat && shuffleId == songListSize-1){
                 shuffleId = -1;
            }


            for (int i=0; i < songListSize ; i++) {
                String songId = this.songList[i][6];
                if (songId.equals(String.valueOf(nextSong))) {
                    int finalI = i;
                    SwingUtilities.invokeLater(() -> {
                        this.window.updatePlayingSongInfo(this.songList[finalI][0], this.songList[finalI][1], this.songList[finalI][2]);
                    });

                    songTime = Integer.parseInt(this.songList[i][5]);
                    break;
                }
            }
            this.thread = new ControlPlayer(this.window,
                    true,
                    true,
                    isRepeat,
                    0,
                    songTime,//Tempo Total da música em execução
                    isRandom ? shuffleId + 1: nextSong,
                    songListSize);
            this.thread.start();
        }
        finally {
            lock.unlock();
        }


    };

    public void prev() {
        try{
            lock.lock();
            this.thread.interrupt();
            if( (shuffleId == -1 || shuffleId == 0) && isRandom && isRepeat){
                shuffleId = 1;
            }

            int songListSize = this.songList.length;

            int previousSong = isRandom ? shuffleOrder[shuffleId - 1] : this.thread.getCurrentSongId()-1;

            int songTime = 0;
            int currentSong = this.thread.getCurrentSongId();

            if (isRandom) {
                shuffleId--;
            }

            if(!isRandom && isRepeat && currentSong == 1){
                previousSong = this.songList.length;
            }

            if(isRandom && isRepeat && shuffleId == 0){
                shuffleId = songListSize;
            }


            for (int i=0; i < songListSize ; i++) {
                String songId = this.songList[i][6];
                if (songId.equals(String.valueOf(previousSong))) {
                    int finalI = i;
                    SwingUtilities.invokeLater(() ->
                            this.window.updatePlayingSongInfo(this.songList[finalI][0], this.songList[finalI][1], this.songList[finalI][2])
                    );

                    songTime = Integer.parseInt(this.songList[i][5]);
                    break;
                }
            }
            this.thread = new ControlPlayer(this.window,
                    true,
                    true,
                    isRepeat,
                    0,
                    songTime,//Tempo Total da música em execução
                    isRandom ? shuffleId + 1: previousSong,
                    songListSize);
            this.thread.start();
        } finally {
            lock.unlock();
        }
    };


    public void released(){
        try {
            lock.lock();
            this.thread.setCurrentTime(this.window.getScrubberValue());
            this.thread.setisPlaying(true);
        } finally {
            lock.unlock();
        }
    }

    public void dragged(){
        try {
            lock.lock();
            this.thread.setCurrentTime(this.window.getScrubberValue());
            this.thread.setisPlaying(false);
        } finally {
            lock.unlock();
        }
    }

    public void shuffle() {
        try{

            lock.lock();
            isRandom = !isRandom;
            if (isRandom && this.thread !=  null){
                createShuffleOrder();
                shuffleId = 0;

                this.thread.interrupt();
                this.thread = new ControlPlayer(this.window,
                        true,
                        true,
                        isRepeat,
                        this.thread.getCurrentTime(),
                        this.thread.getTotalTime(),
                        shuffleId,
                        shuffleOrder.length);
                this.thread.start();

            } else if (this.thread != null){
                this.thread.interrupt();

                this.thread = new ControlPlayer(this.window,
                        true,
                        true,
                        false,
                        this.thread.getCurrentTime(),
                        this.thread.getTotalTime(),
                        shuffleOrder[this.thread.getCurrentSongId() == 0 ? 0 : this.thread.getCurrentSongId() - 1 ],
                        shuffleOrder.length);
                this.thread.start();
            }
        } finally {
            lock.unlock();
        }

    };

    public void repeat() {
        try{
            lock.lock();
            isRepeat = !isRepeat;
            if (isRepeat){
                this.thread.setisRepeat(true);
            }
            else{
                this.thread.setisRepeat(false);
            }

        } finally {
            lock.unlock();
        }
    };


    private void createShuffleOrder( ) {
        int currentSong = this.thread.getCurrentSongId();
        int songListSize = this.songList.length;

        Integer[] newShuffleList = new Integer[songListSize - 1];

        int addedSongs = 0;
        for (int i = 1; i < songListSize + 1; i++) {
            if (i != currentSong) {
                newShuffleList[addedSongs] = i;
                addedSongs += 1;
            }
        }

        List<Integer> intList = Arrays.asList(newShuffleList);
        Collections.shuffle(intList);
        intList.toArray(newShuffleList);

        // adiciona musica atual no inicio na primeira posição e cria array final
        shuffleOrder = new Integer[songListSize];
        shuffleOrder[0] = currentSong;
        System.arraycopy(newShuffleList, 0, shuffleOrder, 1, songListSize - 1);

    }


}