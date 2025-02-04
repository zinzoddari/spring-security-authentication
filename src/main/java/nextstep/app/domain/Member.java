package nextstep.app.domain;

import nextstep.security.domain.UserDetails;

public class Member implements UserDetails {
    private final String email;
    private final String password;
    private final String name;
    private final String imageUrl;

    public Member(String email, String password, String name, String imageUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}
