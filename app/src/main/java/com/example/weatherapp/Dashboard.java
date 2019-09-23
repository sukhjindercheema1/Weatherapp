package com.example.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends Fragment {

    TextView txt_dashname,txt_wename,txt_mintemp,txt_maxtemp,txt_acttemp,txt_humidity,txt_predect;
    FirebaseUser user;
    FirebaseFirestore db;
    ImageView img_we;
    ArrayList<WeatherDate> weathers;
    RecyclerView recyclerView ;

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
        db = FirebaseFirestore.getInstance();

        System.out.println("Dashboard on Create Called!");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_dashname = view.findViewById(R.id.dash_name);
        txt_wename = view.findViewById(R.id.dash_wename);
        img_we = view.findViewById(R.id.dash_actimg);
        txt_mintemp = view.findViewById(R.id.txt_mintemp);
        txt_maxtemp = view.findViewById(R.id.txt_maxtemp);
        txt_acttemp = view.findViewById(R.id.txt_actemp);

        txt_humidity = view.findViewById(R.id.txt_humidity);
        txt_predect = view.findViewById(R.id.txt_prec);

        recyclerView = view.findViewById(R.id.recycleV);



        readFireStore();
        getWeather();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void readFireStore()
    {
        DocumentReference docref = db.collection("users").document(user.getUid());

        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        System.out.println(doc.getData());

                        txt_dashname.setText("Welcome "+doc.get("name")+" !");
                    }

                }

            }
        });
    }

    public void getWeather()
    {

        GetDataService service = RetrofitURL.getRetrofitInstance().create(GetDataService.class);
        Call<WeatherMontreal> call = service.getWeather();

        call.enqueue(new Callback<WeatherMontreal>() {
            @Override
            public void onResponse(Call<WeatherMontreal> call, Response<WeatherMontreal> response) {

                System.out.println("Response Called!");

                WeatherMontreal weatherMontreal = response.body();

                weathers = new ArrayList<>(weatherMontreal.getWeatherDates());

                System.out.println("Check Size :"+weathers.size());

                setData(weathers);
            }

            @Override
            public void onFailure(Call<WeatherMontreal> call, Throwable t) {

                System.out.println("Failure Called! :" +t.getMessage());
            }
        });

    }

    public void setData(ArrayList<WeatherDate> wearray)
    {
        System.out.println("Size From method :"+wearray.size());
        txt_wename.setText(wearray.get(0).getWeatherStateName());
        setImage(img_we,getWeImage(wearray.get(0).getWeatherStateAbbr()));

        String thetemp = String.format("%.2f",wearray.get(0).getTheTemp());
        String mintemp = String.format("%.2f",wearray.get(0).getMinTemp());
        String maxtemp = String.format("%.2f",wearray.get(0).getMaxTemp());

        txt_mintemp.setText(mintemp);
        txt_maxtemp.setText(maxtemp);
        txt_acttemp.setText(thetemp);

        txt_humidity.setText("Humidity : "+wearray.get(0).getHumidity().toString());
        txt_predect.setText("Predictability : "+wearray.get(0).getPredictability().toString()+"%");

        initView(wearray);

    }

    public String getWeImage(String code)
    {
        return "https://www.metaweather.com/static/img/weather/png/"+code+".png";
    }

    public void setImage(ImageView img,String link)
    {
        Picasso.get().load(link).into(img);
    }

    public void initView(ArrayList<WeatherDate> wearray)
    {
        wearray.remove(0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(layoutManager);
        WeatherAdapter adapter = new WeatherAdapter(wearray,getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
    }


}
