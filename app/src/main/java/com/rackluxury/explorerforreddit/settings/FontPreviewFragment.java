package com.rackluxury.explorerforreddit.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.rackluxury.explorerforreddit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FontPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FontPreviewFragment extends Fragment {

    public FontPreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_font_preview, container, false);
    }
}