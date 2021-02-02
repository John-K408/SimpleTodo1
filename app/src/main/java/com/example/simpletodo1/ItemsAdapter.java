package com.example.simpletodo1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//This class is the recycler view adapter that will contain our viewHolder to render the item
//It takes the data at a particular position and puts it into the viewHolder
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    //Since we need to pass information from our mainActivity to our items adapter, we will create
    //an interface in the itemsAdapter that the main activity will implement.
    //This interface will communicate to mainActivity the position of what was clicked(or longClicked)


    //NB: You can define an interface inside a class
    public interface OnClickListener{
        void onItemClicked(int position);
    }
    public interface OnLongClickListener{
        void onItemLongClicked(int position);
    }

    List<String> items;
    OnLongClickListener longClickListener;
    OnClickListener clickListener;
    //This is the constructor for this  class. In this constructor, you get hold of the entities you
    //will need - items, longClickListener, clickListener - so you can use them throughout the class.
    public ItemsAdapter(List<String> items, OnLongClickListener longClickListener, OnClickListener clickListener) {
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Use layout inflation to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);
        //wrap it inside a view Holder and return it
        return new ViewHolder(todoView);
    }
    //Responsible for binding data to a particular ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    //Grab item at a particular position
        String item = items.get(position);

        //Bind item to specified viewHolder
        holder.bind(item);
    }
    //Tells the recycler view how many items are in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    //ViewHolder:container to provide easy access to views that represent each row of the list

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
        }

        //Update the view inside the viewHolder with this data
        public void bind(String item) {
            tvItem.setText(item);
            //Update a view


            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                //when android notifies us that given view has been clicked
                public void onClick(View view) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });

            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                //When android notifies us that given view has been longClicked....
                public boolean onLongClick(View view) {
                    //Remove the item from the recycler view [The difficulty here lies in the fact
                    //that we cannot communicate to the adapter at this point so the best we can do is
                    //to communicate to the MainActivity that specified item was clicked then we can
                    //delete that item from the list in the MainActivity session

                    //Notify the listener which item was long clicked
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
