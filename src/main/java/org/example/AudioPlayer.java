package org.example;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Audio resources both copyright free from https://pixabay.com/
 * Jazz : https://pixabay.com/music/traditional-jazz-the-best-jazz-club-in-new-orleans-164472/
 * bomb : https://pixabay.com/sound-effects/a-bomb-139689/
 */
public class AudioPlayer {
    private Clip music;
    private boolean playing;
    public AudioPlayer() {
        try {

            File audioFile = new File("src/main/resources/the-best-jazz-club-in-new-orleans.wav");
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
            this.music = AudioSystem.getClip();
            this.music.open(inputStream);
            this.music.loop(Clip.LOOP_CONTINUOUSLY);
            this.playing = true;
        } catch (Exception e) {
            System.out.println("Error with audio");
            e.printStackTrace();
        }
    }

    public void pause () {
        if (this.playing) {
            this.music.stop();
            this.playing = false;
        } else {
            this.music.loop(Clip.LOOP_CONTINUOUSLY);
            this.playing = true;
        }
    }



    public void playBombNoise() {
        try {
            File audioFile = new File("src/main/resources/bomb.wav");
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error with audio");
            e.printStackTrace();
        }
    }


}
