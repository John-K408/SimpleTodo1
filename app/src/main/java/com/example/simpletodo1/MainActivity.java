package com.example.simpletodo1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//List variables above the onCreate line and initialize them in the onCreate so variables are
    //created only when we run the app.

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set it so that we are able to view content on the creation of this activity - activity_main
        setContentView(R.layout.activity_main);
    //We load items only when the app starts
        loadItems();
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);
        //etItem.setText("I'm doing this from java");
        //After we add persistence we no longer need the mock data
//        items.add("Buy milk");
//        items.add("Go to the gym");
//        items.add("Have fun!");

        //Instantiate the OnLongClickListener interface
         ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){

            @Override
            public void onItemLongClicked(int position) {

                //Delete the item from the model;
                items.remove(position);
                //Notify the adapter of the position the item was removed from
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item was successfully removed",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

         //Instantiate the clickListener interface
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                //To test that this method is reached when we single-click an item, we can run a log
                Log.d("MainActivity","single click at position " + position);

                //create the new activity
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                    //MainActivity.this -> current instance of main activity
                    //EditActivity.class -> this indicates there is no instance but this is the class type
                        //we would like to have then the android system takes care of creating that class.


                //pass the item being editted
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the new activity
                    //there are lots of ways to start activity but we will use the startActivityForResuilt
                    //method because we expect a result: the updated to do Item.
                startActivityForResult(i,EDIT_TEXT_CODE);

            }
        };
        //Create or instantiate adapter
        itemsAdapter = new ItemsAdapter(items,onLongClickListener, onClickListener);

        //Set adapter on recycler view
        rvItems.setAdapter(itemsAdapter);

        //set layout manager: we will use the default android layout manager which automatically puts things
        //in a vertical way
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //To add item to the list when the add button is pressed, we will add an onClickListener to
        //the button so we can perform action specified in method when the addbutton is clicked

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Once someone clicks the button, we want to get the text inside the etItem space
                //Since text will be an edittable, we want to ge the string form so we call toString on
                //it.
                String todoItem = etItem.getText().toString();
                //Add new todo item to the model
                items.add(todoItem);
                // Notify the adapter that we've modified the list
                itemsAdapter.notifyItemInserted(items.size() - 1);

                //Finally and most importantly, we want to clear the edittext once list has been modified.
                etItem.setText("");

                //We can make something appear that shows user text was successfully added; this is called a Toast
                Toast.makeText(getApplicationContext(),"Item was successfully added",Toast.LENGTH_SHORT).show();
                //We save items when we add an item and when we remove an item (see last line of onItemLongClicked above)
                saveItems();
            }
        });

         }
         //handle result (data) from edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve data
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //Update model with retrieved data at the right position
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // persist changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item successfully updated", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
            return new File(getFilesDir(), "data.txt");
         }

    //This line will load items by reading every line of our data.txt file
    private void loadItems(){
        try {
            //Read from data.txt to items arraylist
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items",e);
            items = new ArrayList<>();
        }
    }
    //This function saves items by writing them into the data.txt file

    private void saveItems(){
        try {
            //Write from items arraylist into data.txt
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error writing items",e);
        }
    }
}