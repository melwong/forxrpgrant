package com.merchant.cryptcard2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.merchant.cryptcard2.databinding.FragmentConfigurationBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationFragment extends Fragment implements SaveConfirmDialogFragment.OnConfirmClickListener {

    private FragmentConfigurationBinding binding;

    private TinyDB tinyDB;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    binding.apiKeyInput.setText(result.getContents());
                }
            });

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentConfigurationBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tinyDB = new TinyDB(getActivity());

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_settings, menu);
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

        binding.merchantIdInput.setText(tinyDB.getString("merchantId"));
        binding.merchantNameInput.setText(tinyDB.getString("merchantName"));
        binding.apiKeyInput.setText(tinyDB.getString("apiKey"));

        List<String> list = new ArrayList<>(Arrays.asList("USDT", "USDC", "DAI", "BUSD", "TUSD", "GUSD", "BTC", "ETH", "XRP", "SOL", "MATIC", "AVAX"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,list);
        binding.currencyInput.setAdapter(adapter);
        binding.currencyInput.setText(tinyDB.getString("currency"),false);

        binding.apiKey.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions scanOptions = new ScanOptions();
                scanOptions.setOrientationLocked(false);
                barcodeLauncher.launch(scanOptions);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String merchId = binding.merchantIdInput.getText().toString();
                String merchName = binding.merchantNameInput.getText().toString();
                String key = binding.apiKeyInput.getText().toString();
                String curr = binding.currencyInput.getText().toString();

                if(TextUtils.isEmpty(merchName)) {
                    binding.merchantName.setError("Field is required");
                    return;
                }

                if(TextUtils.isEmpty(merchId)) {
                    binding.merchantId.setError("Field is required");
                    return;
                }

                if(TextUtils.isEmpty(key)) {
                    binding.apiKey.setError("Field is required");
                    return;
                }

                if(TextUtils.isEmpty(curr)) {
                    binding.currency.setError("Field is required");
                    return;
                }

                SaveConfirmDialogFragment saveConfirmDialogFragment = new SaveConfirmDialogFragment();
                saveConfirmDialogFragment.setOnConfirmClickListener(ConfigurationFragment.this);
                saveConfirmDialogFragment.show(getChildFragmentManager(), "Save_Confirm_Dialog");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onConfirmClick() {
        tinyDB.putString("merchantId", binding.merchantIdInput.getText().toString());
        tinyDB.putString("merchantName", binding.merchantNameInput.getText().toString());
        tinyDB.putString("apiKey", binding.apiKeyInput.getText().toString());
        tinyDB.putString("currency", binding.currencyInput.getText().toString());
        NavHostFragment.findNavController(ConfigurationFragment.this)
                .navigate(R.id.action_ConfigFragment_to_PaymentFragment);
    }
}