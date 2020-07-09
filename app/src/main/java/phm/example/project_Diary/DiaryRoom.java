package phm.example.project_Diary;

public class DiaryRoom {
    private String diarysUserList;
    private String mid;
    private String fid;


    public DiaryRoom(String diarysUserList, String mid, String fid) {

        this.diarysUserList =diarysUserList;
        this.mid = mid;
        this.fid = fid;
    }

    public DiaryRoom(){

    }
    public String getDiarysUserList() {
        return diarysUserList;
    }

    public void setDiarysUserList(String diarysUserList) {
        this.diarysUserList = diarysUserList;
    }

    public String getMid() { return mid; }

    public void setMyName(String mid) {
        this.mid = mid;
    }

    public String getFid() { return fid ; }

    public void setFriendID (String fid ) { this.fid  = fid ;  }
}
