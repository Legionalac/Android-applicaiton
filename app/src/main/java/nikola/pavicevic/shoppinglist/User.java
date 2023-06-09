package nikola.pavicevic.shoppinglist;

public class User {
    private String mUsername;
    private String mEmail;
    private String mPassword;

    public User(String username, String email, String password) {
        this.mUsername = username;
        this.mEmail = email;
        this.mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }
}
