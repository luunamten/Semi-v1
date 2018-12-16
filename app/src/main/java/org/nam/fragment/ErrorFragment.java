package org.nam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.nam.MyApp;
import org.nam.R;

public class ErrorFragment extends Fragment {
    public static final String IMAGE_RESOURCE = "imageResource";
    public static final String MESSAGE = "message";

    public ErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.errorImageView);
        TextView textView= view.findViewById(R.id.errorTextView);
        Bundle args = getArguments();
        int imageResource = args.getInt(IMAGE_RESOURCE);
        String message = args.getString(MESSAGE);
        imageView.setImageResource(imageResource);
        textView.setText(message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setArguments (int imageResource, int message){
        Bundle bundle = new Bundle();
        bundle.putInt(IMAGE_RESOURCE, imageResource);
        bundle.putString(MESSAGE, MyApp.getInstance().getString(message));
        setArguments(bundle);
    }
}
