package com.rackluxury.explorerforreddit.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import com.rackluxury.explorerforreddit.R;
import com.rackluxury.explorerforreddit.UploadImageEnabledActivity;
import com.rackluxury.explorerforreddit.adapters.UploadedImagesRecyclerViewAdapter;

public class UploadedImagesBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    public static final String EXTRA_UPLOADED_IMAGES = "EUI";

    private UploadImageEnabledActivity activity;

    public UploadedImagesBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_uploaded_images_bottom_sheet, container, false);
        MaterialButton uploadButton = rootView.findViewById(R.id.upload_button_uploaded_images_bottom_sheet_fragment);
        MaterialButton captureButton = rootView.findViewById(R.id.capture_button_uploaded_images_bottom_sheet_fragment);

        uploadButton.setOnClickListener(view -> {
            activity.uploadImage();
            dismiss();
        });

        captureButton.setOnClickListener(view -> {
            activity.captureImage();
            dismiss();
        });

        RecyclerView uploadedImagesRecyclerView = rootView.findViewById(R.id.recycler_view_uploaded_images_bottom_sheet);
        UploadedImagesRecyclerViewAdapter adapter = new UploadedImagesRecyclerViewAdapter(
                getArguments().getParcelableArrayList(EXTRA_UPLOADED_IMAGES), uploadedImage -> {
            activity.insertImageUrl(uploadedImage);
            dismiss();
        });
        uploadedImagesRecyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (UploadImageEnabledActivity) context;
    }
}