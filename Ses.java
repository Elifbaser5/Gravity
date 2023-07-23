import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Ses {
    private static Clip clip;

    public static void play(String filepath) {
        try {
            File musicFile = new File(filepath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    public static void stop(String filepath) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}