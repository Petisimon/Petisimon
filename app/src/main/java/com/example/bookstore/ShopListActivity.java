package com.example.bookstore;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private static final String PREF_KEY = ShopListActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<Book> mItemList;
    private BookAdapter mAdapter;
    private int gridNumber=1;
    private boolean viewRow = true;
    private FrameLayout circle;
    private int cartItems = 0;
    private TextView contentTextView;
    private FirebaseFirestore mFirestore;
    private CollectionReference mBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG,"Authenticated user");
        }
        else{
            Log.d(LOG_TAG,"Couldn't authenticate user");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new BookAdapter(this,mItemList);
        mRecyclerView.setAdapter(mAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mBooks = mFirestore.collection("Books");
        queryData();
    }

    private void queryData(){
        mItemList.clear();

        //mBooks.whereEqualTo()
        mBooks.orderBy("name").limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                Book book = document.toObject(Book.class);
                mItemList.add(book);
            }
            if(mItemList.size() == 0){
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] booksList = getResources().getStringArray(R.array.book_names);
        String[] booksInfo = getResources().getStringArray(R.array.book_descs);
        String[] booksPrice = getResources().getStringArray(R.array.book_prices);
        TypedArray booksImageResource = getResources().obtainTypedArray(R.array.book_images);


        for (int i = 0; i < booksList.length; i++) {
            mBooks.add(new Book(booksList[i],booksInfo[i],booksPrice[i],booksImageResource.getResourceId(i,0)));
        }

        booksImageResource.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu,menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG,s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logoutButton:
                Log.d(LOG_TAG,"logoutButton");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settingsButton:
                Log.d(LOG_TAG,"settingsButton");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG,"cart");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG,"view_selector");
                if(viewRow){
                    changeSpanCount(item, R.drawable.ic_view_grid,1);
                }else{
                    changeSpanCount(item, R.drawable.ic_view_row,2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        circle = (FrameLayout) rootView.findViewById(R.id.view_alert_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_tv);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(){
        cartItems+=1;
        if(0 < cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        }else{
            contentTextView.setText("");
        }
        circle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }
}