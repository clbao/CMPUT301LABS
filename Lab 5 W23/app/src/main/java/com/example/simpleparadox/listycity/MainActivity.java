package com.example.simpleparadox.listycity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView cityList;
    ArrayAdapter<City> cityAdapter;
    ArrayList<City> cityDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG = "Sample";
        Button addCityButton;
        final EditText addCityEditText;
        final EditText addProvinceEditText;
        FirebaseFirestore db;

        addCityButton = findViewById(R.id.add_city_button);
        addCityEditText = findViewById(R.id.add_city_field);
        addProvinceEditText = findViewById(R.id.add_province_edit_text);

        cityList = findViewById(R.id.city_list);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Cities");

        cityDataList = new ArrayList<>();
        cityAdapter = new CustomList(this, cityDataList);
        cityList.setAdapter(cityAdapter);

        // collectionReference added here to ensure app starts itself with a filled list if database has docs
        // will probably make a separate function for this as it is called about 3 times right now
        RefreshList(collectionReference);

        addCityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieving the city name and the province name from the EditText fields
                final String cityName = addCityEditText.getText().toString();
                final String provinceName = addProvinceEditText.getText().toString();

                // Stores data in the form of Key-value pairs
                HashMap<String, String> data = new HashMap<>();
                if (cityName.length()>0 && provinceName.length()>0) {
                    data.put("Province Name", provinceName);

                    // The set method sets a unique id for the document
                    collectionReference
                            .document(cityName)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // These are a method which gets executed when the task is succeeded
                                    Log.d(TAG, "Data has been added successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception error) {
                                    // These are a method which gets executed if there’s any problem
                                    Log.d(TAG, "Data could not be added!" + error.toString());
                                }
                            });
                    addCityEditText.setText("");
                    addProvinceEditText.setText("");

                    RefreshList(collectionReference);
                }
            }
        });

        //delete city code
        cityList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                City deleteCity = (City) adapterView.getItemAtPosition(i);

                final String cityName = deleteCity.getCityName();
                final String provinceName = deleteCity.getProvinceName();

                // Stores data in the form of Key-value pairs
                HashMap<String, String> data = new HashMap<>();
                data.put("Province Name", provinceName);
                // The set method sets a unique id for the document
                collectionReference
                        .document(cityName)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // These are a method which gets executed when the task is succeeded
                                Log.d(TAG, "Data has been deleted successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception error) {
                                // These are a method which gets executed if there’s any problem
                                Log.d(TAG, "Data could not be deleted!" + error.toString());
                            }
                        });
                RefreshList(collectionReference);
                cityAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "City Deleted", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    public void RefreshList(CollectionReference collectionReference){
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                // Clear the old list
                cityDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d("Province Name", String.valueOf(doc.getData().get("Province Name")));
                    String city = doc.getId();
                    String province = (String) doc.getData().get("Province Name");
                    cityDataList.add(new City(city, province)); // Adding the cities and provinces from FireStore
                }
            }
        });
    }
}
