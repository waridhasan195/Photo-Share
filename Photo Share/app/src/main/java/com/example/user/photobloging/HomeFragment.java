package com.example.user.photobloging;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView blog_list_view;

    private List<BlogPost> blog_list;
    private BlogRecyclerAdapter blogRecyclerAdapter;


    FirebaseFirestore firebaseFirestore;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);

        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogRecyclerAdapter);

        firebaseFirestore =FirebaseFirestore.getInstance();

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        blog_list = new ArrayList<>();

        blog_list_view =getActivity().findViewById(R.id.blog_list_view);

        firebaseFirestore.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);

                        blog_list.add(blogPost);

                        blogRecyclerAdapter.notifyDataSetChanged();

                    }
                }


            }
        });



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
