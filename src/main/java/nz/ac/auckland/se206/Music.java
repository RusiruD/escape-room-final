package nz.ac.auckland.se206;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/** Utility class for managing music playback in the game. */
public class Music {

  private static MediaPlayer mediaPlayer;
  private static Media background = generateMedia("music1");
  private static Media win = generateMedia("Win");
  private static Media loss = generateMedia("Loss");
  private static Media equip = generateMedia("item");
  private static Media unlock = generateMedia("unlock");
  private static Media swordDraw = generateMedia("swordDraw");
  private static Media swordHit = generateMedia("swordHit");
  private static Media potionMove = generateMedia("movePotion");
  private static Media potionDrop = generateMedia("potionDrop");
  private static Media pageTurn = generateMedia("pageTurn");

  public static void playBackgroundMusic() {

    playMusic(background);
  }

  public static void playWinMusic() {
    playMusic(win);
  }

  public static void playLossMusic() {
    playMusic(loss);
  }

  public static void playEquipSound() {
    playSimultaneousSound(equip, 1);
  }

  public static void playCorrectKey() {
    playSimultaneousSound(unlock, 1);
  }

  public static void playSwordDraw() {
    playSimultaneousSound(swordDraw, 1);
  }

  public static void playSwordHit() {
    playSimultaneousSound(swordHit, 1);
  }

  public static void playMovePotion() {
    playSimultaneousSound(potionMove, 2.0);
  }

  public static void playDropPotion() {
    playSimultaneousSound(potionDrop, 0.75);
  }

  public static void playPageTurn() {
    playSimultaneousSound(pageTurn, 1);
  }

  private static void playSimultaneousSound(Media music, double volume) {
    MediaPlayer newMedia = new MediaPlayer(music);
    newMedia.setVolume(volume);
    if (!GameState.isMuted) {
      newMedia.play();
    }
  }

  private static Media generateMedia(String name) {
    try {
      Media music = new Media(App.class.getResource("/sounds/" + name + ".mp3").toURI().toString());
      return music;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  /** Starts playing the music music. */
  private static void playMusic(Media music) {
    if (mediaPlayer != null) {
      stop();
    }
    mediaPlayer = new MediaPlayer(music);
    if (!GameState.isMuted) {
      mediaPlayer.play();
    }
  }

  public static void pause() {
    mediaPlayer.pause();
  }

  public static void unpause() {
    mediaPlayer.play();
  }

  public static void stop() {
    mediaPlayer.stop();
  }
}
