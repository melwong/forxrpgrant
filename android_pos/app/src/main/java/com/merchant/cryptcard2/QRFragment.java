package com.merchant.cryptcard2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.merchant.cryptcard2.databinding.FragmentQrBinding;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRFragment extends Fragment {

    private FragmentQrBinding binding;

    private TinyDB tinyDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tinyDB = new TinyDB(getActivity());

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_display_mode) {
                    boolean nightMode = tinyDB.getBoolean("nightMode");
                    if (!nightMode) {
                        tinyDB.putBoolean("nightMode", true);
                    } else {
                        tinyDB.putBoolean("nightMode", false);
                    }
                    getActivity().recreate();
                    return true;
                }

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                return NavigationUI.onNavDestinationSelected(menuItem, navController);
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Log.d("link", getArguments().getString("link"));
            Bitmap bitmap = barcodeEncoder.encodeBitmap(getArguments().getString("link"), BarcodeFormat.QR_CODE, 400, 400);
            binding.imageView.setImageBitmap(bitmap);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}