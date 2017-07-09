package com.imadhik.cgpaassist;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    SharedPreferences sp;
    int totalCredits;
    int earnedCreditsPoints;
    View linearLayout;
    View view;
    Map buttonMap;
    float finalCGPA;
    Map countMap;



    public HomeFragment() {
        earnedCreditsPoints=0;
        totalCredits=0;
        buttonMap = new HashMap();
        countMap=new HashMap<String,Integer>();
        finalCGPA=(float)0.0;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        // Inflate the layout for this fragment
        sp=getActivity().getSharedPreferences("CGPAActivity", Context.MODE_PRIVATE);
        view=inflater.inflate(R.layout.fragment_home, container, false);
        linearLayout=view.findViewById(R.id.homeLinearLayout);
        layoutFiller(getActivity(),view);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.resetButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
               resetEverything();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        actionBar.setTitle("Home");
    }

    private void resetEverything() {
        buttonMap.clear();
        earnedCreditsPoints=0;
        totalCredits=0;

        if(((LinearLayout) linearLayout).getChildCount() > 0)
            ((LinearLayout) linearLayout).removeAllViews();
        layoutFiller(getActivity(),view);
    }

    private void layoutFiller(Activity activityObject,View view) {
        String ObjectString=sp.getString("object",null);


        try {
            countNumberOfTimes(ObjectString);
            JSONObject object = new JSONObject(ObjectString);
            JSONArray objectArray=object.getJSONArray("grades");
            int length=objectArray.length();
            //Toast.makeText(getActivity(),""+length,Toast.LENGTH_SHORT).show();;

            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsCourseCode=new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight=1;
            layoutParamsCourseCode.weight=0;
            int buttonWidth= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutButton=new LinearLayout.LayoutParams(buttonWidth,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutButton.weight=0;
            layoutButton.setMargins(0,0,20,5);
            layoutParams.setMargins(0,0,0,0);
            layoutParamsCourseCode.setMargins(20,0,0,0);




            for(int i=0;i<length;i++) {
                JSONObject ob = objectArray.getJSONObject(i);
                String credit = ob.getString("Credit").trim();

                if (credit.length() != 0) {



                    String courseText=ob.getString("Course Title");
                    String courseCode=ob.getString("Course Code");
                    String grade=ob.getString("Grade");

                    boolean finishedCourse=false;
                    if("N".equalsIgnoreCase(grade) || "F".equalsIgnoreCase(grade))
                    {
                        finishedCourse=checkCompleted(objectArray,courseCode);
                    }

                    if(!finishedCourse) {
                        String slno = ob.getString("Sl.No.").trim();
                        int slnoInt = Integer.parseInt(slno);

                        buttonInfo buttonObject = new buttonInfo(grade, grade, Integer.parseInt(credit));
                        buttonMap.put(slno, buttonObject);


                        int cr = Integer.parseInt(credit.trim());
                        totalCredits = totalCredits + cr;
                        earnedCreditsPoints = earnedCreditsPoints + getValue(grade) * cr;


                        TextView courseTextTexView = new TextView(activityObject);
                        courseTextTexView.setText(courseText);
                        courseTextTexView.setTextSize((float) 20);
                        courseTextTexView.setGravity(Gravity.LEFT);
                        courseTextTexView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        layoutParams.setMargins(5, 0, 0, 0);

                        courseTextTexView.setLayoutParams(layoutParams);

                        ((LinearLayout) linearLayout).addView(courseTextTexView);

                        LinearLayout childLinearLayout = new LinearLayout(activityObject);
                        childLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);


                        TextView courseCodeTextView = new TextView(activityObject);
                        courseCodeTextView.setText(courseCode);
                        //courseCodeTextView.setWeight
                        courseCodeTextView.setLayoutParams(layoutParamsCourseCode);
                        courseCodeTextView.setGravity(Gravity.CENTER);

                        childLinearLayout.addView(courseCodeTextView);

                        courseCodeTextView = new TextView(activityObject);
                        courseCodeTextView.setText("Cr : " + credit);
                        courseCodeTextView.setLayoutParams(layoutParams);
                        courseCodeTextView.setGravity(Gravity.CENTER);

                        childLinearLayout.addView(courseCodeTextView);

                        Button button = new Button(activityObject);
                        button.setLayoutParams(layoutButton);
                        button.setText(grade);
                        button.setTextColor(Color.parseColor("#ffffff"));
                        button.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                        button.setId(slnoInt);
                        button.setOnClickListener(this);

                        childLinearLayout.addView(button);


                        ((LinearLayout) linearLayout).addView(childLinearLayout);


                    }
                }
            }

            TextView cgpaTextView=(TextView)view.findViewById(R.id.cgpaTextView);
            float cgpa=(float)earnedCreditsPoints/totalCredits;
            cgpa=Math.round(cgpa*100.0)/(float)100.0;
            finalCGPA=cgpa;

            cgpaTextView.setText(""+cgpa);
            cgpaTextView.setTextColor(Color.parseColor("#4c5154"));

        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    public int getValue(String grade){

        grade=grade.trim();

        if(grade.equalsIgnoreCase("S"))
            return 10;
        if (grade.equalsIgnoreCase("A"))
            return 9;
        if (grade.equalsIgnoreCase("B"))
            return 8;
        if(grade.equalsIgnoreCase("C"))
            return 7;
        if (grade.equalsIgnoreCase("D"))
            return 6;
        if (grade.equalsIgnoreCase("E"))
            return 5;
        if (grade.equalsIgnoreCase("N"))
            return 0;
        if (grade.equalsIgnoreCase("F"))
            return 0;

        return 0;


    }

    @Override
    public void onClick(View v) {

        Button button=(Button)view.findViewById(v.getId());
        button.setBackground( getResources().getDrawable(R.drawable.rounded_button_selected));
        //button.setBackground(getResources().getDrawable(R.drawable.loginbutton));


        buttonInfo object=(buttonInfo)buttonMap.get(""+v.getId());
        String nextG=nextGrade(button.getText().toString());
        updateCgpa(object.currentGrade,nextG,object.credit);
        object.currentGrade=nextG;
        if(object.defaultGrade.equalsIgnoreCase(nextG)){
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        }
        else
            button.setTextColor(Color.parseColor("#000000"));
        button.setText(nextG);


        //Toast.makeText(getActivity(),""+object.credit,Toast.LENGTH_SHORT).show();

    }

    public String nextGrade(String grade){
        grade=grade.trim();

        if(grade.equalsIgnoreCase("S"))
            return "F";
        if (grade.equalsIgnoreCase("A"))
            return "S";
        if (grade.equalsIgnoreCase("B"))
            return "A";
        if(grade.equalsIgnoreCase("C"))
            return "B";
        if (grade.equalsIgnoreCase("D"))
            return "C";
        if (grade.equalsIgnoreCase("E"))
            return "D";
        if (grade.equalsIgnoreCase("N"))
            return "E";
        if (grade.equalsIgnoreCase("F"))
            return "N";

        return "S";

    }

    public void updateCgpa(String currentGrade,String nextGrade,int credit){
        earnedCreditsPoints=earnedCreditsPoints+(getValue(nextGrade)-getValue(currentGrade))*credit;
        TextView cgpaTextView=(TextView)view.findViewById(R.id.cgpaTextView);
        float cgpa=(float)earnedCreditsPoints/totalCredits;
        cgpa=Math.round(cgpa*100.0)/(float)100.0;

        float dif;
        if(cgpa>finalCGPA){
            dif=cgpa-finalCGPA;
            dif=Math.round(dif*100.0)/(float)100.0;
            cgpaTextView.setText(""+cgpa+" (+"+dif+")");
            cgpaTextView.setTextColor(Color.parseColor("#32b541"));
        }

        if(cgpa<finalCGPA){
            dif=cgpa-finalCGPA;
            dif=Math.round(dif*100.0)/(float)100.0;
            cgpaTextView.setText(""+cgpa+" ("+dif+")");
            cgpaTextView.setTextColor(Color.parseColor("#b53131"));
        }
        if(cgpa==finalCGPA){
            cgpaTextView.setText(""+cgpa+" (0.0)");
            cgpaTextView.setTextColor(Color.parseColor("#706a6a"));
        }


    }

    public boolean checkCompleted(JSONArray gradesArray,String courseID) throws JSONException {
        int length=gradesArray.length();

        int countValue = (Integer)countMap.get(courseID);
        if(countValue>1) {
            for (int i = 0; i < length; i++) {
                JSONObject courseObject = gradesArray.getJSONObject(i);
                if (courseID.equalsIgnoreCase(courseObject.getString("Course Code"))) {
                    String courseGrade = courseObject.getString("Grade");
                    if (getValue(courseGrade) > 0)
                        return true;

                }
            }
        }
        return false;
    }

    public void countNumberOfTimes(String objectString) throws JSONException {

        JSONObject object = new JSONObject(objectString);
        JSONArray objectArray=object.getJSONArray("grades");
        int length=objectArray.length();

        for(int i=0;i<length;i++)
        {
            JSONObject subjectObject = objectArray.getJSONObject(i);
            String credit = subjectObject.getString("Credit").trim();

            if (credit.length() != 0) {
                String courseCode = subjectObject.getString("Course Code");


                if(!countMap.containsKey(courseCode)){
                    countMap.put(courseCode,1);
                }
                else {
                    int countValue = (Integer)countMap.get(courseCode);
                    countValue+=1;
                    countMap.put(courseCode,countValue);
                }


            }
        }
    }
}
