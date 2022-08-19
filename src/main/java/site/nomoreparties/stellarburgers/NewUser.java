package site.nomoreparties.stellarburgers;

import org.apache.commons.lang3.RandomStringUtils;

public class NewUser {

    private String email;
    private String password;
    private String name;


    public String getEmail() {
        return email;
    }

    public String setEmail(String email) {
        return this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String setPassword(String password) {
        return this.password = password;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return this.name = name;
    }

    public NewUser(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static NewUser getRandomUser() {
        String email = RandomStringUtils.randomAlphabetic(6) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new NewUser(email, password, name);
    }
}
