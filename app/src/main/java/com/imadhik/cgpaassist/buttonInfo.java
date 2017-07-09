package com.imadhik.cgpaassist;

import java.io.Serializable;

/**
 * Created by Adhik on 03-05-2017.
 */

public class buttonInfo implements Serializable{
    public String defaultGrade;
    public String currentGrade;
    public int credit;

    public buttonInfo(){
        defaultGrade=null;
        currentGrade=null;
        credit=0;
    }

    public buttonInfo(String DefaultGrade,String CurrentGrade,int Credit){
        defaultGrade=DefaultGrade;
        currentGrade=CurrentGrade;
        credit=Credit;
    }
}
