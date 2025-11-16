package edu.uni.registration.model;

public class Student extends Person {
    private String major;
    private int year;

    public Student(String id,String firstName,String lastName,String email,String major,int year) {
        super(id,firstName,lastName,email);
        this.major = major;
        this.year = year;
    }

    //Getter methods
    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    //Setter methods
    public void setMajor(String major) {
        this.major = major;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Major: " + major +
                ", Year: " + year;
    }
}
