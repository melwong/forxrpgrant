package com.merchant.cryptcard2;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.merchant.cryptcard2.databinding.FragmentPaymentBinding;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;

import java.text.DecimalFormat;

public class PaymentFragment extends Fragment implements NumberKeyboardListener {

    private FragmentPaymentBinding binding;

    private static final double MAX_ALLOWED_AMOUNT = 9999999.99;
    private static final int MAX_ALLOWED_DECIMALS = 2;
    private String amountText = "";
    private TinyDB tinyDB;
    private final DecimalFormat df = new DecimalFormat("###,###,##0.00");

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPaymentBinding.inflate(inflater, container, false);
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

        String merchantId = tinyDB.getString("merchantId");
        String merchantName = tinyDB.getString("merchantName");
        String currency = tinyDB.getString("currency");
        String apiKey = tinyDB.getString("apiKey");

        if(TextUtils.isEmpty(merchantId) || TextUtils.isEmpty(merchantName)
                || TextUtils.isEmpty(currency) || TextUtils.isEmpty(apiKey)) {
            NavHostFragment.findNavController(PaymentFragment.this)
                    .navigate(R.id.action_PaymentFragment_to_ConfigurationFragment);
            return;
        }

        binding.textView.setText("Enter amount in " + currency);

        binding.numberKeyboard.setListener(this);
        binding.generatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link = "https://metamask.app.link/dapp/https://thegoalslingers.com/pos/" +
                        "pos-usdc.html?amount=" + amountText + "&label=" + merchantName +
                        "&merchantid=" + merchantId + "&currency=" + currency;

                Bundle bundle = new Bundle();
                bundle.putString("link",link);
                NavHostFragment.findNavController(PaymentFragment.this)
                        .navigate(R.id.action_PaymentFragment_to_QRFragment, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNumberClicked(int number) {
        if (amountText.isEmpty() && number == 0) {
            return;
        }
        updateAmount(amountText + number);
    }

    @Override
    public void onLeftAuxButtonClicked() {}

    @Override
    public void onRightAuxButtonClicked() {
        // Delete button
        if (amountText.isEmpty()) {
            return;
        }
        String newAmountText;
        if (amountText.length() <= 1) {
            newAmountText = "";
        } else {
            newAmountText = amountText.substring(0, amountText.length() - 1);
            if (newAmountText.charAt(newAmountText.length() - 1) == ',') {
                newAmountText = newAmountText.substring(0, newAmountText.length() - 1);
            }
            if ("0".equals(newAmountText)) {
                newAmountText = "";
            }
        }
        updateAmount(newAmountText);
    }

    @Override
    public void onModifierButtonClicked(int number) {}

    /**
     * Update new entered amount if it is valid.
     */
    private void updateAmount(String newAmountText) {
        newAmountText = insertDecimal(newAmountText);
        double newAmount = newAmountText.isEmpty() ? 0.0 : Double.parseDouble(newAmountText);
        if (newAmount >= 0.0 && newAmount <= MAX_ALLOWED_AMOUNT
                && getNumDecimals(newAmountText) <= MAX_ALLOWED_DECIMALS) {
            amountText = newAmountText;
            showAmount(newAmount);
        }
    }

    /**
     * Insert decimal to the entered amount
     */
    private String insertDecimal(String str) {
        str = str.replace(".","").replaceFirst("^0+(?!$)", "");
        if(str.length() == 0) {
            return "0.00";
        } else if(str.length() == 1) {
            return "0.0" + str;
        } else if(str.length() == 2) {
            return "0." + str;
        } else {
            return str.substring(0, str.length() - 2) + "." + str.substring(str.length() - 2);
        }
    }

    /**
     * Shows amount in UI.
     */
    private void showAmount(double amount) {
        binding.editText.setText(df.format(amount));
    }

    /**
     * Checks whether the string has a comma.
     */
    private boolean hasComma(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculate the number of decimals of the string.
     */
    private int getNumDecimals(String num) {
        if (!hasComma(num)) {
            return 0;
        }
        return num.substring(num.indexOf(',') + 1).length();
    }
}