package tn.isi.ussef.mytracker.Model;

/**
 * Created by Ussef on 3/29/2017.
 */

public class TrackerItem
{
    public  String PhoneNumber;
    public  String UserName;
    //for news details
    public TrackerItem(String UserName, String PhoneNumber)
    {
        this.UserName = UserName;
        this.PhoneNumber = PhoneNumber;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}