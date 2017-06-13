package com.example.waleed.inventory;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.waleed.inventory.data.ItemContract.ItemEntry;

import static android.R.attr.id;
import static com.example.waleed.inventory.R.mipmap.ic_launcher;

/**
 * Created by Waleed on 31/05/17.
 */

public class ItemCursorAdapter extends CursorAdapter {
    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentItemUri;
    int availableItems = 0;
    public ItemCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView productImageView = (ImageView) view.findViewById(R.id.image);
        Button quantityButton = (Button) view.findViewById(R.id.button);

        // Find the columns of item attributes that we're interested in
        int IDColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
        final int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_CURRENT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int pictureColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PICTURE);

        // Read the item attributes from the Cursor for the current item
        final int ID = Integer.parseInt(cursor.getString(IDColumnIndex));
        final String itemName = cursor.getString(nameColumnIndex);
        final int itemQuantity = Integer.parseInt(cursor.getString(quantityColumnIndex));
        final String itemPrice = cursor.getString(priceColumnIndex);
        final Uri thumbUri = Uri.parse(cursor.getString(pictureColumnIndex));

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));
        priceTextView.setText(itemPrice + "$");
        Glide.with(context).load(thumbUri).placeholder(ic_launcher).error(ic_launcher).crossFade().centerCrop().into(productImageView);
        quantityButton.setTag(ID);
        quantityButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                int id = (int)v.getTag();
                Log.i("quantity",quantityTextView.getText().toString());
                mCurrentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                availableItems = itemQuantity;
                Log.i("availableItems", String.valueOf(availableItems));
                if(availableItems > 0) {
                    availableItems = itemQuantity - 1;
                    quantityTextView.setText(String.valueOf(availableItems));
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_CURRENT_QUANTITY, availableItems);
                    context.getContentResolver().update(mCurrentItemUri , values, null, null);
                    Log.i("availableItems", String.valueOf(availableItems));
                }
                else {
                    Toast.makeText(context, "Inventory is empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}

