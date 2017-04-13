package tn.isi.ussef.mytracker.Utils;

/**
 * Created by Ussef on 3/29/2017.
 */

public class Utils {
    public static String FormatPhoneNumber(String Oldnmber){
        try{
            String numberOnly= Oldnmber.replaceAll("[^0-9]", "");
            if(Oldnmber.charAt(0)=='+') numberOnly="+" +numberOnly ;
            if (numberOnly.length()>=10)
                numberOnly=numberOnly.substring(numberOnly.length()-10,numberOnly.length());
            return(numberOnly);
        }
        catch (Exception ex){
            return(" ");
        }
    }
}
