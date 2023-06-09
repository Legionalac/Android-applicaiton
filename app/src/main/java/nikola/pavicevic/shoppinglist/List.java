package nikola.pavicevic.shoppinglist;

public class List {
    private String mName;
    private boolean mShared;
    private  String  mUsername;
    public List(String name, boolean shared) {
        this.mName = name;
        this.mShared = shared;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public boolean isShared() {
        return mShared;
    }
    public void setShared(boolean shared) {
        this.mShared = shared;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}
