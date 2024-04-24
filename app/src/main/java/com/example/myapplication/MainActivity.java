package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView plantListView;
    private String[] plantSpecies;
    private int selectedPosition = -1; // -1 indicates no selection
    private int selectedPlantImageId = -1;
    private int selectedPlantImageIdOne = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        plantListView = (ListView) findViewById(R.id.plant_list);
        //plant specie array
        plantSpecies = new String[]{
                "Abelia",
                "Acanthus",
                "African Violet",
                "Agapanthus",
                "Agave",
                "Allium",
                "Alocasia",
                "Aloe",
                "Anemone",
                "Annual Leucanthemum",
                "Anthurium",
                "Aucuba",
                "Auriculas",
                "Border Buttercups",
                "Bromeliad",
                "Brugmansia",
                "Calamagrostis",
                "Calendula",
                "Camellia",
                "Ceanothus",
                "Conservatory Passion",
                "Daffodil",
                "Echeveria",
                "Echinacea",
                "Ferocactus",
                "Foxglove",
                "Garrya",
                "Gerbera",
                "Gunnera",
                "Hoya",
                "Hypoestes",
                "Knifophia",
                "Lavatera",
                "Miscanthus",
                "Monarda",
                "Nerine",
                "Nigella",
                "Phlox",
                "Poinsettia",
                "Pyracantha",
                "Rose",
                "Sansevieria",
                "Scabious",
                "Sea Holly",
                "Sempervivum",
                "Spider",
                "Strelitzia",
                "String of Beads",
                "Tree Peonies",
                "Tulip",
                "Venus Fly Trap",
                "Veronica",
                "Yew",
        };

        // Create an ArrayAdapter with the plant species data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, plantSpecies);
        // Set the adapter on the ListView to display the plant names
        plantListView.setAdapter(adapter);

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search Plant");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // This method will not be used
                searchView.clearFocus();

                // Append the search query to the base URL
                String baseUrl = "https://garden.org/plants/search/text.php?q=";
                String url = baseUrl + query;

                // Open the webpage in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

                return true;
                //return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchQuery = newText.toLowerCase(); // Convert to lowercase for case-insensitive search
                String[] filteredPlants = new String[plantSpecies.length]; // Temporary array for filtered plants
                int filteredCount = 0;

                for (String plant : plantSpecies) {
                    if (plant.toLowerCase().contains(searchQuery)) {
                        filteredPlants[filteredCount] = plant;
                        filteredCount++;
                    }
                }
                // Create a new adapter with the filtered plant names (resize filteredPlants if needed)
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, Arrays.copyOf(filteredPlants, filteredCount));
                plantListView.setAdapter(adapter);

                // Update ListView visibility to show filtered plants
                plantListView.setVisibility(View.VISIBLE);

                // Set an OnItemClickListener to handle item selection
                plantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedPlant = (String) parent.getItemAtPosition(position);
                        // Set the selected plant name as the query in the SearchView
                        searchView.setQuery(selectedPlant, true);

                        // Reset the adapter to show all plants
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, plantSpecies);
                        plantListView.setAdapter(adapter);
                        plantListView.setVisibility(View.GONE); // Hide the ListView
                    }
                });

                return false;
            }
        });

        // Handle clearing search query (clicking "x" button)
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Reset adapter to show all plants
                plantListView.setAdapter(adapter);
                plantListView.setVisibility(View.GONE); // Hide the ListView
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.clearFocus();
                return true;
            }
        });


        //navigation buttons had to be implemented as imageButtons so i can use the pixel art
        ImageButton waterButton, cameraButton, homeButton, weatherButton, oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton;
        waterButton = (ImageButton) findViewById(R.id.water);
        cameraButton = (ImageButton) findViewById(R.id.camera);
        homeButton = (ImageButton) findViewById(R.id.homeButton);
        weatherButton = (ImageButton) findViewById(R.id.WeatherButton);
        oneButton = (ImageButton) findViewById(R.id.one);
        twoButton = (ImageButton) findViewById(R.id.two);
        threeButton = (ImageButton) findViewById(R.id.three);
        fourButton = (ImageButton) findViewById(R.id.four);
        fiveButton = (ImageButton) findViewById(R.id.five);
        sixButton = (ImageButton) findViewById(R.id.six);
        sevenButton = (ImageButton) findViewById(R.id.seven);
        eightButton = (ImageButton) findViewById(R.id.eight);
        nineButton = (ImageButton) findViewById(R.id.nine);

        // Set onClickListener for ListView to track selection
        plantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
            }
        });

        oneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)     {
                showPlantListDialog((ImageButton)v);
            }
        });

        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, NotificationScreen.class);
                startActivity(intent1);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, PlantIdentification.class);
                startActivity(intent2);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent4);
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(MainActivity.this, WeatherScreen.class);
                startActivity(intent4);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }//oncreate finish

    private void showPlantListDialog(ImageButton clickedButton) {
        // Create a new ListView for the dialog
        ListView plantList = new ListView(this);

        // Create an ArrayAdapter with the plant species data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, plantSpecies);
        plantList.setAdapter(adapter);

        plantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlant = plantSpecies[position];

                // Set the selected plant image resource ID based on selection (modify as needed)
                switch (selectedPlant) {
                    case "Aloe":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_aloe;
                        } else if (clickedButton.getId() == R.id.two){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.three){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.four){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.five){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.six){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.seven){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.eight){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }else if (clickedButton.getId() == R.id.nine){
                            selectedPlantImageIdOne = R.drawable.pixel_aloe;
                        }
                        break;
                    case "Daffodil":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_daffodil;
                        } else if (clickedButton.getId() == R.id.two){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.three){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.four){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.five){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.six){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.seven){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.eight){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }else if (clickedButton.getId() == R.id.nine){
                            selectedPlantImageIdOne = R.drawable.pixel_daffodil;
                        }
                        break;

                    case "Ferocactus":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_ferocactus;
                        } else if (clickedButton.getId() == R.id.two){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.three){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.four){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.five){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.six){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.seven){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.eight){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }else if (clickedButton.getId() == R.id.nine){
                            selectedPlantImageIdOne = R.drawable.pixel_ferocactus;
                        }
                        break;

                    case "Foxglove":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_foxglove;
                        } else if (clickedButton.getId() == R.id.two){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.three){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.four){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.five){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.six){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.seven){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.eight){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }else if (clickedButton.getId() == R.id.nine){
                            selectedPlantImageIdOne = R.drawable.pixel_foxglove;
                        }
                        break;

                    case "Rose":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_rose;
                        } else if (clickedButton.getId() == R.id.two){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.three){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.four){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.five){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.six){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.seven){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.eight){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }else if (clickedButton.getId() == R.id.nine){
                            selectedPlantImageIdOne = R.drawable.pixel_rose;
                        }
                        break;
                    case "Spider":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_spiderplant;
                        } else if (clickedButton.getId() == R.id.two){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.three){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.four){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.five){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.six){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.seven){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.eight){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }else if (clickedButton.getId() == R.id.nine){
                            selectedPlantImageIdOne = R.drawable.pixel_spiderplant;
                        }
                        break;
                    case "Tree Peonies":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_tree_peony;
                        } else if (clickedButton.getId() == R.id.two) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.three) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.four) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.five) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.six) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.seven) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.eight) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }else if (clickedButton.getId() == R.id.nine) {
                            selectedPlantImageIdOne = R.drawable.pixel_tree_peony;
                        }
                        break;
                    case "Tulip":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_tulip;
                        } else if (clickedButton.getId() == R.id.two) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.three) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.four) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.five) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.six) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.seven) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.eight) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }else if (clickedButton.getId() == R.id.nine) {
                            selectedPlantImageIdOne = R.drawable.pixel_tulip;
                        }
                        break;
                    case "Venus Fly Trap":
                        if (clickedButton.getId() == R.id.one) {
                            selectedPlantImageId = R.drawable.pixel_venus_flytrap;
                        } else if (clickedButton.getId() == R.id.two) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.three) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.four) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.five) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.six) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.seven) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.eight) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }else if (clickedButton.getId() == R.id.nine) {
                            selectedPlantImageIdOne = R.drawable.pixel_venus_flytrap;
                        }
                        break;
                        default:
                            if (clickedButton.getId() == R.id.one) {
                                selectedPlantImageId = R.drawable.pixel_generic;
                            } else if (clickedButton.getId() == R.id.two) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.three) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.four) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.five) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.six) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.seven) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.eight) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }else if (clickedButton.getId() == R.id.nine) {
                                selectedPlantImageIdOne = R.drawable.pixel_generic;
                            }

                }

                // Update plant image visibility based on selection
                ImageView plantImageView = findViewById(clickedButton.getId());
                plantImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (clickedButton.getId() == R.id.one && selectedPlantImageId != -1) {
                    plantImageView.setImageResource(selectedPlantImageId);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                } else if (clickedButton.getId() == R.id.two && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                } else if (clickedButton.getId() == R.id.three && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else if (clickedButton.getId() == R.id.four && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else if (clickedButton.getId() == R.id.five && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else if (clickedButton.getId() == R.id.six && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else if (clickedButton.getId() == R.id.seven && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else if (clickedButton.getId() == R.id.eight && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else if (clickedButton.getId() == R.id.nine && selectedPlantImageIdOne != -1) {
                    plantImageView.setImageResource(selectedPlantImageIdOne);
                    plantImageView.setVisibility(View.VISIBLE);
                    plantImageView.setBackground(null);
                }else {
                    plantImageView.setVisibility(View.INVISIBLE);
                }

                Toast toast = Toast.makeText(MainActivity.this, "Selected plant: " + selectedPlant, Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        // Build the AlertDialog with the ListView
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Plant");
        builder.setView(plantList);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}


