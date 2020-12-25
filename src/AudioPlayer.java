import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.sound.sampled.LineEvent.Type;

public class AudioPlayer {
    public static String folderLocation;

    public AudioPlayer(ArrayList<String> audioFiles) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Scanner terminalReader = new Scanner(System.in);
        AudioInputStream audioInputStream = null;
        String input = "";
        while (true) {
            while (true) {
                input = terminalReader.nextLine().trim();
                if (input != null && !input.equals("")) {
                    break;
                } else {
                    System.out.println("Make sure you have something other than spaces in your input.");
                }
            }
            if (input.equalsIgnoreCase("stop")) {
                break;
            }
            ArrayList<String> words = parseWords(input);
            ArrayList<Integer> soundsPlayed = createSoundsIndexArray(audioFiles, words);

            for (int i = 0; i < soundsPlayed.size(); i++) {
                audioInputStream = AudioSystem.getAudioInputStream(new File(folderLocation + "/" + audioFiles.get(soundsPlayed.get(i)) + ".wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                while (clip.getMicrosecondLength() > clip.getMicrosecondPosition()) {

                }
                clip.stop();
            }
        }
        terminalReader.close();
        if (audioInputStream != null) {
            audioInputStream.close();
        }
    }

    public ArrayList<String> parseWords(String input) {
        ArrayList<String> words = new ArrayList<>();
        String currentWord = "";
        char currentChar = 0;
        for (int i = 0; i < input.length(); i++) {
            currentChar = input.charAt(i);
            if (currentChar == ' ') {
                words.add(currentWord);
                currentWord = "";
            } else {
                currentWord = currentWord + currentChar;
            }
        }
        words.add(currentWord);
        return words;
    }

    public ArrayList<Integer> createSoundsIndexArray(ArrayList<String> audioText, ArrayList<String> words) {
        ArrayList<Integer> fileIndexArray = new ArrayList<Integer>();
        SortedMap fileIndexInWord;
        for (int i = 0; i < words.size(); i++) {
            String currWord = words.get(i);
            int currIndex = audioText.indexOf(currWord);
            if (currIndex != -1) {
                fileIndexArray.add(currIndex);
            } else {
                fileIndexInWord = new TreeMap();
                for (int j = 0; j < audioText.size(); j++) {
                    String currAudioText = audioText.get(j);
                    int indexOfAudioTextInWord = currWord.indexOf(currAudioText);
                    if (indexOfAudioTextInWord != -1) {
                        fileIndexInWord.put(indexOfAudioTextInWord, j);
                        currWord = currWord.replaceFirst(currAudioText, " ".repeat(currAudioText.length()));
                        j--;
                    }
                }
                for (int j = 0; j < currWord.length(); j++) {
                    if (fileIndexInWord.containsKey(j)) {
                        fileIndexArray.add(Integer.parseInt(fileIndexInWord.get(j).toString()));
                    }
                }
                fileIndexArray.add(0);
            }
        }

        return fileIndexArray;
    }

    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        ArrayList<String> audioFileNames = new ArrayList<>();
        File ClipFileNames = new File(AudioPlayer.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "ClipFileNames.txt");
        Scanner fileReader = new Scanner(ClipFileNames);
        if (fileReader.hasNextLine()) {
            folderLocation = fileReader.nextLine();
            if (folderLocation.equals("default")) {
                folderLocation = AudioPlayer.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/AudioFiles";
            }
        }
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            audioFileNames.add(line.trim());
        }
        fileReader.close();
        new AudioPlayer(audioFileNames);
    }
}