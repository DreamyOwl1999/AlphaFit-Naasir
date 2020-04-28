package com.example.alphafit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Tetris_UI extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private Space space;
    private int j, k, c, d, type, shortAnimationDuration, height, btnID, comID, canID;
    private String text, buttonID, completeID, cancelID, mealType, mealInfo;
    private int[] colour;
    private boolean noti;
    private CList[][] bList;
    private ArrayList<Stats> sList;
    private ArrayList<Block> aList;
    private AlertDialog.Builder builder;
    private AlertDialog alert;
    private Animation slide_down;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter2 adapter;
    private LinearLayout hl, cl;
    private RelativeLayout cc;
    private AttributeSet attr;
    private CList btnEL;
    private Bitmap TheBitmap;
    private Bitmap bitmap;
    private MotionEvent event;
    private Stats stat;
    private Animator currentAnimator;
    private Sys sys;
    private ArrayList<com.example.alphafit.Task> scheduledTasks;


    private LayoutInflater inflater;
    private View.OnClickListener old2;

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tetris_main);
        //myView = (LovelyView) findViewById(R.id.custView);

        XmlPullParser parser = getResources().getXml(R.xml.attrs);
        try {
            parser.next();
            parser.nextTag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AttributeSet attr = Xml.asAttributeSet(parser);

        sys = Sys.getInstance();
        Almanac al = new Almanac();
        scheduledTasks=al.getTaskList();

        j = al.getItemListLength();
        k = 0;
        c = 0;
        d = 0;
        height = 92;
        btnID = 0;
        comID = 0;
        canID = 0;
        colour = new int[6];
        aList = new ArrayList();
        colour = genColours();
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        builder = new AlertDialog.Builder(this);

        cc = findViewById(R.id.line);
        /*space = new Space(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
        space.setLayoutParams(params);*/
        //cl = (LinearLayout) findViewById(R.id.lin);
        cl = new LinearLayout(this);
        cl.setOrientation(LinearLayout.VERTICAL);

        btn = new Button(this);
        //btn.setOnClickListener(this);
        //int btnID2 = getResources().getIdentifier("undo", "id", this.getPackageName());
        btn = findViewById(R.id.undo);
        //btn.setId(btnID2);
        btn.setText("Undo");
        btn.setBackgroundColor(Color.parseColor("#D0D0D0"));
        btn.setOnClickListener(this);

        //cc.addView(space);
        //cl.addView(btn);
        cc.addView(cl);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //btn = (Button) findViewById(R.id.btn);
        //btn.setOnClickListener(this);

        //ck = (GridLayout) findViewById(R.id.lin2);
        //ck.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.imagegallery2);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getApplicationContext(), 8);
        recyclerView.setLayoutManager(layoutManager);

        //MyLayoutManager layout = new MyLayoutManager();
        sList = prepareDataS();
        Log.d("Testing1","Hello");
        bList = new CList[j][4];
        for (int x = 0; x < j; x++) {
            mealType = sList.get(x).getMealType();
            mealInfo = sList.get(x).getDesc();
            type = sList.get(x).getType();
            noti = sList.get(x).getNoti();
            comID = x;
            canID = x + j;
            for (int y = 0; y < 4; y++) {
                btnEL = new CList();
                bList[x][y] = prepareData(type, ((x*4)+(j*2))+y, btnEL);
                if (y == 3) {
                    hl = prepareDatavLay(mealType,mealInfo, type, noti, comID, canID);
                    View completeH = findViewById(comID);
                    View cancelH = findViewById(canID);
                    for (int z = 0; z < 4; z++) {
                        old2 = buttonSetOnClick(layoutManager, completeH, cancelH, hl, c, type, x);
                        bList[x][z].setLis(old2);
                        c++;
                    }
                }
            }
        }
        ArrayList<CList> aList = prepareDataA(bList);
        //aList=ShapeGenerator(aList);
        aList = howManyBlocks(j,aList);
        adapter = new MyAdapter2(getApplicationContext(), aList,j);
        recyclerView.setAdapter(adapter);
    }
    //btn = (ImageButton) findViewById(R.id.imageButton);
        /*GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.columnSpec = GridLayout.spec(1);
        param.rowSpec = GridLayout.spec(3);
        param.setGravity(Gravity.CENTER);
        cc.setLayoutParams(param);*/

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.undo) {
            undoTask();
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public int[] genColours() {

        //Green
        colour[0] = Color.parseColor("#D4F7D4");
        //Red
        colour[1] = Color.parseColor("#559E54");
        colour[2] = Color.parseColor("#FD3A0F");
        colour[3] = Color.parseColor("#B83214");
        colour[4] = Color.parseColor("#FFFF85");
        //Yellow
        colour[5] = Color.parseColor("#C0C0C0");

        return colour;
    }

    public ArrayList<Stats> prepareDataS() {
        ArrayList<Stats> totalList = new ArrayList();
        for (int i = 0; i < j; i++) {
            stat = new Stats(i);
            totalList.add(stat);
        }
        return totalList;
    }

    private CList prepareData(int type, int i, CList btnEL) {

        btnEL.setColor(colour[type]);
        btnEL.setFill(true);
        btnEL.setId(i);

        return btnEL;
    }

    private LinearLayout prepareDatavLay(final String mealType,final String mealInfo, final int type, final boolean noti, final int comID, final int canID) {

        final LinearLayout ck = new LinearLayout(this);
        final LinearLayout la1 = new LinearLayout(this);
        final LinearLayout la2 = new LinearLayout(this);
        final LinearLayout la3 = new LinearLayout(this);
        final LinearLayout la4 = new LinearLayout(this);
        final Space Vspace1 = new Space(this);
        final Space Vspace2 = new Space(this);
        final Space Vspace3 = new Space(this);
        final Space Vspace4 = new Space(this);
        final Space HspaceMealT1 = new Space(this);
        final Space HspaceMealT2 = new Space(this);
        final Space HspaceMealI1 = new Space(this);
        final Space HspaceMealI2 = new Space(this);
        final Space HspaceCheck1 = new Space(this);
        final Space HspaceCheck2 = new Space(this);
        final Space HspaceBtn1 = new Space(this);
        final Space HspaceBtn2 = new Space(this);
        final TextView txtMT = new TextView(this);
        final TextView txtMI = new TextView(this);
        final TextView txtC = new TextView(this);
        final Button complete = new Button(this);
        final Button cancel = new Button(this);
        final CheckBox box = new CheckBox(this);

        ck.setVisibility(View.INVISIBLE);

        complete.setBackgroundColor(colour[5]);
        complete.setText("Complete");
        complete.setId(comID);

        cancel.setBackgroundColor(colour[5]);
        cancel.setText("Back");
        cancel.setId(canID);

        txtMT.setTextColor(colour[5]);
        txtMT.setText(mealType);
        txtMT.setTextSize(25);
        txtMT.setBackgroundColor(colour[type]);

        txtMI.setTextColor(colour[5]);
        txtMI.setText(mealInfo);
        txtMI.setTextSize(25);
        txtMI.setBackgroundColor(colour[type]);


        LinearLayout.LayoutParams VSparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        LinearLayout.LayoutParams VSparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height / 2);
        LinearLayout.LayoutParams LineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams HSparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

        ck.setOrientation(LinearLayout.VERTICAL);
        la1.setOrientation(LinearLayout.HORIZONTAL);
        la2.setOrientation(LinearLayout.HORIZONTAL);
        la3.setOrientation(LinearLayout.HORIZONTAL);
        la4.setOrientation(LinearLayout.HORIZONTAL);

        ck.setLayoutParams(LineParams);

        la1.setLayoutParams(LineParams);
        la2.setLayoutParams(LineParams);
        la3.setLayoutParams(LineParams);
        la4.setLayoutParams(LineParams);

        Vspace1.setLayoutParams(VSparams);
        Vspace2.setLayoutParams(VSparams);
        Vspace3.setLayoutParams(VSparams2);
        Vspace4.setLayoutParams(VSparams2);

        HspaceMealT1.setLayoutParams(HSparams);
        HspaceMealT2.setLayoutParams(HSparams);
        HspaceMealI1.setLayoutParams(HSparams);
        HspaceMealI2.setLayoutParams(HSparams);
        HspaceCheck1.setLayoutParams(HSparams);
        HspaceCheck2.setLayoutParams(HSparams);
        HspaceBtn1.setLayoutParams(HSparams);
        HspaceBtn2.setLayoutParams(HSparams);

        la1.addView(HspaceMealT1);
        la1.addView(txtMT);
        la1.addView(HspaceMealT2);

        la2.addView(HspaceMealI1);
        la2.addView(txtMI);
        la2.addView(HspaceMealI2);


        la3.addView(HspaceBtn1);
        la3.addView(complete);
        la3.addView(cancel);
        la3.addView(HspaceBtn2);

        ck.addView(Vspace1);

        if (noti == false) {
            ck.addView(Vspace3);
        }

        ck.addView(la1);
        ck.addView(la2);

        if (noti) {
            txtC.setText("Reminder Set: ");
            txtC.setTextColor(colour[5]);
            txtC.setTextSize(25);
            box.setBackgroundColor(colour[5]);
            la4.addView(HspaceCheck1);
            la4.addView(txtC);
            //la4.addView(Hspace3);
            la4.addView(box);
            la4.addView(HspaceCheck2);
            ck.addView(la4);
        } else {
            ck.addView(Vspace4);
        }
        ck.addView(Vspace2);
        ck.addView(la3);
        cc.addView(ck);
        return ck;
    }

    public View.OnClickListener buttonSetOnClick(final RecyclerView.LayoutManager btn, final View completeH, final View cancelH, final LinearLayout hl, final int c, final int type, final int d) {
        View.OnClickListener old = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(btn, completeH, cancelH, hl, c, d);
                hl.setBackgroundColor(colour[type]);
            }
        };
        return old;
    }

    private ArrayList<CList> prepareDataA(CList[][] bList) {
        ArrayList<CList> aList = new ArrayList<>();
        for (int i = 0; i < j; i++) {
            for (int x = 0; x < 4; x++) {
                aList.add(bList[i][x]);
            }
        }
        return aList;
    }

    private ArrayList<CList> howManyBlocks(int j,ArrayList<CList> aList){

        if(j==1){
            aList=hardCoded1(aList);
        }
        else if(j==2){
            aList=hardCoded2(aList);
        }
        else if(j==2){
            aList=hardCoded2(aList);
        }
        else if(j==3){
            aList=hardCoded3(aList);
        }
        else if(j==4){
            aList=hardCoded4(aList);
        }
        else {
            aList = hardCoded5(aList);
        }

        return aList;
    }

    private ArrayList<CList> hardCoded1(ArrayList<CList> aList) {

        ArrayList<CList> aListTemp = aList;
        CList hold;
        int from;
        int to;
        int total;

        total = 16-aListTemp.size();

        for(int i=0;i<total;i++)
        {
            hold=new CList();
            aListTemp.add(hold);
        }

        from = 3;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        for(int d=0;d<32;d++)
        {
            hold=new CList();
            aListTemp.add(0,hold);
        }

        return aList;
    }

    private ArrayList<CList> hardCoded2(ArrayList<CList> aList) {

        ArrayList<CList> aListTemp = aList;
        CList hold;
        int from;
        int to;
        int total;

        total = 16-aListTemp.size();

        for(int i=0;i<total;i++)
        {
            hold=new CList();
            aListTemp.add(hold);
        }

        from = 3;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 6;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 7;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        for(int d=0;d<32;d++)
        {
            hold=new CList();
            aListTemp.add(0,hold);
        }

        return aList;
    }

    private ArrayList<CList> hardCoded3(ArrayList<CList> aList) {

        ArrayList<CList> aListTemp = aList;
        CList hold;
        int from;
        int to;
        int total;

        total = 16-aListTemp.size();

        for(int i=0;i<total;i++)
        {
            hold=new CList();
            aListTemp.add(hold);
        }

        from = 3;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 6;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 7;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        for(int d=0;d<32;d++)

        {
            hold=new CList();
            aListTemp.add(0,hold);
        }

        return aList;
    }

    private ArrayList<CList> hardCoded4(ArrayList<CList> aList) {

        ArrayList<CList> aListTemp = aList;
        CList hold;
        int from;
        int to;
        int total;

        total = 32-aListTemp.size();

        for(int i=0;i<total;i++)
        {
            hold=new CList();
            aListTemp.add(hold);
        }

        from = 3;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 6;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 7;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 12;
        to = 17;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 12;
        to = 17;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        Collections.swap(aListTemp, 12, 14);
        Collections.swap(aListTemp, 13, 15);

        from = 18;
        to = 21;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 18;
        to = 21;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        Collections.swap(aListTemp, 16, 18);
        Collections.swap(aListTemp, 16, 30);

        for(int d=0;d<32;d++)
        {
            hold=new CList();
            aListTemp.add(0,hold);
        }

        return aList;
    }

    private ArrayList<CList> hardCoded5(ArrayList<CList> aList) {

        ArrayList<CList> aListTemp = aList;
        CList hold;
        int from;
        int to;
        int total;

        total = 32-aListTemp.size();

        for(int i=0;i<total;i++)
        {
            hold=new CList();
            aListTemp.add(hold);
        }

        from = 3;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 6;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 15;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 7;
        to = 8;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 9;
        to = 11;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 12;
        to = 17;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 12;
        to = 17;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        Collections.swap(aListTemp, 12, 14);
        Collections.swap(aListTemp, 13, 15);

        from = 18;
        to = 21;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        from = 18;
        to = 21;
        hold = aListTemp.get(from);
        aListTemp.remove(from);
        aListTemp.add(to, hold);

        Collections.swap(aListTemp, 16, 18);
        Collections.swap(aListTemp, 16, 30);
        Collections.swap(aListTemp, 19, 31);
        Collections.swap(aListTemp, 16, 27);

        for(int d=0;d<32;d++)
        {
            hold=new CList();
            aListTemp.add(0,hold);
        }

        return aList;
    }

    private ArrayList<CList> ShapeGenerator(ArrayList<CList> aList) {

        ArrayList<CList> aListTemp = aList;
        ArrayList<Integer> temp = new ArrayList();
        CList hold;
        CList hold2;
        int TotalInRow = 8;
        int columns = 8;
        int currentBlock = 0;
        int w = 0;
        int x = 0;
        int j = 0;
        int k = 0;
        int z = 0;
        int topRow = aListTemp.size() % columns;
        int rows = (aListTemp.size() - topRow) / columns;
        int y = rows * columns;

        int lBlock[] = new int[4];
        int nRow[] = new int[2];

        lBlock[0] = x;
        lBlock[1] = x + 1;
        lBlock[2] = x + 2;
        lBlock[3] = x + 8;

        nRow[1] = 4;

        for (int i = 0; i < y; i++) {
            Log.d(getClass().getName(), "w==");
            Log.d(getClass().getName(), w + "");
            if ((TotalInRow - currentBlock < 3 && w == 0 && nRow[1] - nRow[0] < 4) || j == 2) {
                currentBlock += 1;
                Log.d(getClass().getName(), "currentBlock");
                Log.d(getClass().getName(), currentBlock + "");
                //Log.d(getClass().getName(),"lBlock");
                //Log.d(getClass().getName(),lBlock[x]+"");
                if (lBlock[x] < TotalInRow) {
                    z++;
                    Log.d(getClass().getName(), "z1==");
                    Log.d(getClass().getName(), z + "");
                }
                if (TotalInRow - currentBlock < 3) {
                    z++;
                    Log.d(getClass().getName(), "z2==");
                    Log.d(getClass().getName(), z + "");
                }
                x++;
                if (x == 4) {
                    for (int d = 0; d < lBlock.length; d++) {
                        lBlock[d] = lBlock[d] + z;
                    }
                    x = 0;
                    z = 0;
                    Log.d(getClass().getName(), "z==");
                    Log.d(getClass().getName(), z + "");
                }
            } else {
                Log.d(getClass().getName(), "main");
                Log.d(getClass().getName(), currentBlock + "");
                Log.d(getClass().getName(), lBlock[w] + "");
                Log.d(getClass().getName(), TotalInRow + "");
                if (lBlock[w] >= y) {
                    break;
                } else {
                    if (lBlock[w] != currentBlock) {

                        hold = aListTemp.get(currentBlock);
                        aListTemp.remove(currentBlock);
                        aListTemp.add(lBlock[w], hold);

                        for (int f = 0; f < temp.size(); f++) {
                            hold2 = aListTemp.get(temp.get(f) - 1);
                            aListTemp.remove(temp.get(f) - 1);
                            aListTemp.add(temp.get(f) - 1, aListTemp.get(temp.get(f) - 1));
                            aListTemp.remove(temp.get(f) - 1);
                            aListTemp.add(temp.get(f), hold2);
                        }

                        temp.add(lBlock[w]);
                        nRow[j] = lBlock[w];
                        Log.d(getClass().getName(), "nRow");
                        Log.d(getClass().getName(), nRow[j] + "");
                        j++;
                        Log.d(getClass().getName(), "j==");
                        Log.d(getClass().getName(), j + "");
                        //aListTemp.set(lBlock[w], aListTemp.get(currentBlock));
                    }
                }
                if (lBlock[w] < TotalInRow) {
                    currentBlock += 1;
                    z++;
                    Log.d(getClass().getName(), "currentBlock");
                    Log.d(getClass().getName(), currentBlock + "");

                }
                w++;
                if (w == 4) {
                    for (int d = 0; d < lBlock.length; d++) {
                        lBlock[d] = lBlock[d] + z;
                    }
                    z = 0;
                    w = 0;
                }
            }
            if (currentBlock >= TotalInRow) {
                TotalInRow = TotalInRow + 8;
                k++;
            }
            if (k == 2) {
                /*for (int d = 0; d < lBlock.length; d++) {
                    lBlock[d] = lBlock[d] + ((currentBlock)/2)-1;
                }*/
                Log.d(getClass().getName(), "lBlock");
                Log.d(getClass().getName(), lBlock[3] + "");
                Log.d(getClass().getName(), "currentBlock");
                Log.d(getClass().getName(), currentBlock + "");
                nRow[0] = 0;
                nRow[1] = 4;
                j = 0;
                k = 0;
                z = 0;
            }
        }
        return aListTemp;
    }

    private void undoTask() {
        if (k != 0) {
            k = k - 1;
            for (int b = (j*2)+(k*4); b < (j*2)+((k + 1) * 4); b++) {
                findViewById(b).setVisibility(View.VISIBLE);
                findViewById(b).startAnimation(slide_down);
            }
        }
    }

    public void warningMessage() {
        alert = new AlertDialog.Builder(Tetris_UI.this).create();
        alert.setMessage("Must complete previous task in order to progress");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public void toastI(int i) {
        String hello = Integer.toString(i);
        Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
    }

    public void toastCan() {
        Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
    }

    public void toast() {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void zoomImageFromThumb(final RecyclerView.LayoutManager layoutManager, View complete, View cancel, final LinearLayout ck, final int i, final int d) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final LinearLayout expandedImageView = ck;
        final View thumbView = layoutManager.findViewByPosition(i);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.line).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        recyclerView.setAlpha(0F);
        btn.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        //.with(ObjectAnimator.ofInt(expandedImageView, "topMargin", topMargin, 0));
        //set.setInterpolator(new LinearInterpolator());
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;
        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }
                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerView.setAlpha(1f);
                        btn.setAlpha(1f);
                        expandedImageView.setVisibility(View.INVISIBLE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        recyclerView.setAlpha(1F);
                        btn.setAlpha(1f);
                        expandedImageView.setVisibility(View.INVISIBLE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }
                if (k < d) {
                    warningMessage();
                    alert.show();
                    return;
                }
                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Animation fadeout = new AlphaAnimation(1.f, 0.f);
                        fadeout.setDuration(600);
                        for (int b = (j*2)+(d*4); b < (j*2)+((d + 1) * 4); b++) {
                            findViewById(b).startAnimation(fadeout);
                            findViewById(b).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (int y = (j*2)+(d*4); y < (j*2)+((d + 1) * 4); y++) {
                                        findViewById(y).setVisibility(View.INVISIBLE);
                                    }
                                }
                            }, shortAnimationDuration);
                        }
                        recyclerView.setAlpha(1f);
                        k = k + 1;
                        sys.moveScheduledToDiary(scheduledTasks.get(d));
                        expandedImageView.setVisibility(View.INVISIBLE);
                        btn.setAlpha(1f);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Animation fadeout = new AlphaAnimation(1.f, 0.f);
                        fadeout.setDuration(600);
                        for (int b = (j*2)+(d*4); b < (j*2)+((d + 1) * 4); b++) {
                            findViewById(b).startAnimation(fadeout);
                            findViewById(b).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (int y = (j*2)+(d*4); y < (j*2)+((d + 1) * 4); y++) {
                                        findViewById(y).setVisibility(View.INVISIBLE);
                                    }
                                }
                            }, shortAnimationDuration);
                        }
                        recyclerView.setAlpha(1f);
                        k = k + 1;
                        expandedImageView.setVisibility(View.INVISIBLE);
                        btn.setAlpha(1f);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

}