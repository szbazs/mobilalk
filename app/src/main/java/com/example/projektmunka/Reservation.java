package com.example.projektmunka;

import java.util.List;

public class Reservation {
    private String date;
    private String time;
    private String movieTitle;
    private List<String> seatNumbers;
    private String name;
    private String email;
    private String filmId;

    public Reservation() {
    }

    public Reservation(String date, String time, String movieTitle, List<String> seatNumbers, String name, String email) {
        this.date = date;
        this.time = time;
        this.movieTitle = movieTitle;
        this.seatNumbers = seatNumbers;
        this.name = name;
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", seatNumbers=" + seatNumbers +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", filmId='" + filmId + '\'' +
                '}';
    }
}
