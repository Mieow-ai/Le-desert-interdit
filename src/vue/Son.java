package vue;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Son {

    /**
     * Joue un son court une seule fois
     */
    public static void jouer_Bruitage(String chemin) {
        try {
            File sound_file = new File(chemin);
            AudioInputStream audio_in = AudioSystem.getAudioInputStream(sound_file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio_in);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erreur lecture son : " + chemin);
        }
    }

    /**
     * Joue une musique en boucle
     * @return le Clip pour pouvoir l'arrêter plus tard
     */
    public static Clip jouer_musique_boucle(String chemin) {
        try {
            File sound_file = new File(chemin);
            AudioInputStream audio_in = AudioSystem.getAudioInputStream(sound_file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio_in);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            return clip;
        } catch (Exception e) {
            return null;
        }
    }
}
