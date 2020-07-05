package phm.example.project_Diary;

public class FriendsList {
    private String fid;
    //private String displayname;
    //private String imageURL;

    public FriendsList(String fid) {
        this.fid = fid;
        //this.displayname = displayname;
        //this.imageURL = imageURL;
    }
    public FriendsList(){

    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }
/*
    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayame(String username) {
        this.displayname = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

 */

}