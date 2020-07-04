package phm.example.project_Diary;

public class DiaryRoom {
    private String diarysUserList;
    private String myName;
    private String friendName;


    public DiaryRoom(String diarysUserList, String myName, String friendName) {

        this.diarysUserList =diarysUserList;
        this.myName = myName;
        this.friendName = friendName;
    }

    public DiaryRoom(){

    }
    public String getDiarysUserList() {
        return diarysUserList;
    }

    public void setDiarysUserList(String diarysUserList) {
        this.diarysUserList = diarysUserList;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getFriendName () { return friendName ; }

    public void setFriendName (String friendName ) { this.friendName  = friendName ;  }
}
