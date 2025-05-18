package com.example.projektmunka;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Film implements Serializable {

    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String releaseDate;
    private String genre;
    private String director;
    private List<String> cast;
    private int durationMinutes;

    public Film() {
    }

    public Film(int id, String title, String description, String imageUrl,
                String releaseDate, String genre, String director, List<String> cast,
                int durationMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.director = director;
        this.cast = cast;
        this.durationMinutes = durationMinutes;
    }


    public Film(int id, String title, String description, String imageUrl) {
        this(id, title, description, imageUrl, "", "", "", new ArrayList<>(), 0);
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getCast() {
        return cast;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", genre='" + genre + '\'' +
                ", director='" + director + '\'' +
                ", cast=" + cast +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
