package com.prayansh.upass.models;

/**
 * Created by Prayansh on 2017-10-21.
 */
public class RenewJobPayload {
    private String username;
    private String password;
    private String school;

    public RenewJobPayload(String username, String password, String school) {
        this.username = username;
        this.password = password;
        this.school = school;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenewJobPayload that = (RenewJobPayload) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return school != null ? school.equals(that.school) : that.school == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (school != null ? school.hashCode() : 0);
        return result;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchool() {
        return school;
    }
}
