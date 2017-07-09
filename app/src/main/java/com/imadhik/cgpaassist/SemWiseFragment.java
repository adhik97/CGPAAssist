package com.imadhik.cgpaassist;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SemWiseFragment extends Fragment implements View.OnClickListener{
    SharedPreferences sp;
    View linearLayout;
    View view;
    int totalCredits;
    int earnedCreditsPoints;
    String objectString;
    Spinner semSpinner;
    Map map;

    Map buttonMap;
    float finalGPA;

    public SemWiseFragment() {
        map=new HashMap();
        buttonMap=new HashMap();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        sp=getActivity().getSharedPreferences("CGPAActivity", Context.MODE_PRIVATE);

        view=inflater.inflate(R.layout.fragment_sem_wise, container, false);
        linearLayout=view.findViewById(R.id.linearLayoutSemWise);
        semSpinner=(Spinner)view.findViewById(R.id.spinner3);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.resetButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetEverything();
            }
        });

        objectString=sp.getString("object",null);
        SpinnerFiller(getActivity());



        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ss=semSpinner.getSelectedItem().toString();
                LayoutSemWiseFiller(getActivity(),ss);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //JSONObject
        //layoutFiller(getActivity(),view,sem);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        actionBar.setTitle("Semester View");
    }

    private void resetEverything() {
        buttonMap.clear();
        earnedCreditsPoints=0;
        totalCredits=0;

        if(((LinearLayout) linearLayout).getChildCount() > 0)
            ((LinearLayout) linearLayout).removeAllViews();
        String sem=semSpinner.getSelectedItem().toString();
        LayoutSemWiseFiller(getActivity(),sem);
    }

    private void LayoutSemWiseFiller(Activity activity,String sem) {
        String examHeld = (String) map.get(sem);
        earnedCreditsPoints=0;
        totalCredits=0;

        if(((LinearLayout) linearLayout).getChildCount() > 0)
            ((LinearLayout) linearLayout).removeAllViews();

        try {
            JSONObject object = new JSONObject(objectString);
            JSONArray objectArray = object.getJSONArray("grades");
            int length = objectArray.length();
            //Toast.makeText(getActivity(),""+length,Toast.LENGTH_SHORT).show();;

            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsCourseCode=new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight=1;
            layoutParamsCourseCode.weight=0;
            int buttonWidth= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutButton=new LinearLayout.LayoutParams(buttonWidth,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutButton.weight=0;
            layoutButton.setMargins(0,0,20,5);
            layoutParamsCourseCode.setMargins(20,0,0,0);
            for(int i=0;i<length;i++) {
                JSONObject ob = objectArray.getJSONObject(i);
                String credit = ob.getString("Credit").trim();
                String examheld = ob.getString("Exam Held");
                String grade=ob.getString("Grade");

                if ((credit.length() != 0) && examHeld.equalsIgnoreCase(examheld))
                {
                    String courseText=ob.getString("Course Title");
                    String courseCode=ob.getString("Course Code");

                    String slno=ob.getString("Sl.No.").trim();
                    int slnoInt=Integer.parseInt(slno);

                    int cr=Integer.parseInt(credit.trim());
                    totalCredits=totalCredits+cr;
                    earnedCreditsPoints=earnedCreditsPoints+getValue(grade)*cr;

                    TextView courseTextTexView = new TextView(activity);
                    courseTextTexView.setText(courseText);
                    courseTextTexView.setTextSize((float)20);
                    courseTextTexView.setGravity(Gravity.LEFT);
                    courseTextTexView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    layoutParams.setMargins(5,0,0,0);

                    courseTextTexView.setLayoutParams(layoutParams);

                    buttonInfo buttonObject=new buttonInfo(grade,grade,Integer.parseInt(credit));
                    buttonMap.put(slno,buttonObject);

                    ((LinearLayout) linearLayout).addView(courseTextTexView);

                    LinearLayout childLinearLayout=new LinearLayout(activity);
                    childLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);






                    TextView courseCodeTextView = new TextView(activity);
                    courseCodeTextView.setText(courseCode);
                    courseCodeTextView.setLayoutParams(layoutParamsCourseCode);
                    courseCodeTextView.setGravity(Gravity.CENTER);

                    childLinearLayout.addView(courseCodeTextView);

                    courseCodeTextView = new TextView(activity);
                    courseCodeTextView.setText("Cr : "+credit);
                    courseCodeTextView.setLayoutParams(layoutParams);
                    courseCodeTextView.setGravity(Gravity.CENTER);

                    childLinearLayout.addView(courseCodeTextView);

                    Button button=new Button(activity);
                    button.setLayoutParams(layoutButton);
                    button.setText(grade);
                    button.setTextColor(Color.parseColor("#ffffff"));
                    button.setId(slnoInt);
                    button.setBackground( getResources().getDrawable(R.drawable.rounded_button));
                    button.setOnClickListener(this);

                    childLinearLayout.addView(button);


                    ((LinearLayout) linearLayout).addView(childLinearLayout);


                }
            }
            TextView cgpaTextView=(TextView)view.findViewById(R.id.semgpaTextView);
            float cgpa=(float)earnedCreditsPoints/totalCredits;
            cgpa=Math.round(cgpa*100.0)/(float)100.0;
            finalGPA=cgpa;

            cgpaTextView.setText(""+cgpa);
            cgpaTextView.setTextColor(Color.parseColor("#4c5154"));

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void SpinnerFiller(Activity activity) {
        try {
            JSONObject object = new JSONObject(objectString);
            JSONArray objectarray = object.getJSONArray("grades");

            List<String> listSem=new ArrayList<String>();
            List<String> list=new ArrayList<String>();

            int semcount=1;
            int lenght = objectarray.length();
            for (int i = 0; i < lenght; i++) {
                JSONObject obb=objectarray.getJSONObject(i);
                String examHeld = obb.getString("Exam Held");
                    if(!listSem.contains(examHeld))
                    {

                        listSem.add(examHeld);
                        String temp="Semester "+semcount;
                        map.put(temp,examHeld);
                        list.add(temp);
                        semcount=semcount+1;
                    }
            }
            ArrayAdapter<String> adp=new ArrayAdapter<String>(activity,
                    android.R.layout.simple_spinner_item,list);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            semSpinner.setAdapter(adp);
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
        TextView cgpaTextView=(TextView)view.findViewById(R.id.semgpaTextView);
        float cgpa=(float)earnedCreditsPoints/totalCredits;
        cgpa=Math.round(cgpa*100.0)/(float)100.0;

        float dif;
        if(cgpa>finalGPA){
            dif=cgpa-finalGPA;
            dif=Math.round(dif*100.0)/(float)100.0;
            cgpaTextView.setText(""+cgpa+" (+"+dif+")");
            cgpaTextView.setTextColor(Color.parseColor("#32b541"));
        }

        if(cgpa<finalGPA){
            dif=cgpa-finalGPA;
            dif=Math.round(dif*100.0)/(float)100.0;
            cgpaTextView.setText(""+cgpa+" ("+dif+")");
            cgpaTextView.setTextColor(Color.parseColor("#b53131"));
        }
        if(cgpa==finalGPA){
            cgpaTextView.setText(""+cgpa+" (0.0)");
            cgpaTextView.setTextColor(Color.parseColor("#706a6a"));
        }


    }


    @Override
    public void onClick(View v) {

        Button button=(Button)view.findViewById(v.getId());
        button.setBackground(getResources().getDrawable(R.drawable.rounded_button_selected));
        buttonInfo object=(buttonInfo)buttonMap.get(""+v.getId());
        String nextG=nextGrade(button.getText().toString());
        updateCgpa(object.currentGrade,nextG,object.credit);
        object.currentGrade=nextG;
        if(object.defaultGrade.equalsIgnoreCase(nextG)) {
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        }
        else
            button.setTextColor(Color.parseColor("#000000"));
        button.setText(nextG);


        //Toast.makeText(getActivity(),""+object.credit,Toast.LENGTH_SHORT).show();

    }

}
