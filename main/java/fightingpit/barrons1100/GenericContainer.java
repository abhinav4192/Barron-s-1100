package fightingpit.barrons1100;

/**
 * Created by AG on 07-Oct-15.
 */
public class GenericContainer {
    private String word;
    private String meaning;
    private boolean favourite;
    private Integer progress;

    public GenericContainer() {
    }

    public GenericContainer(String word, String meaning, boolean favourite, Integer progress) {
        this.word = word;
        this.meaning = meaning;
        this.favourite = favourite;
        this.progress = progress;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
