package ml.docilealligator.infinityforreddit.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import ml.docilealligator.infinityforreddit.Infinity;
import ml.docilealligator.infinityforreddit.R;

public class PremiumActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private TextView tvStatus;
    private Button btnPremium;
    private TransactionDetails purchaseTransactionDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tv_status);
        btnPremium = findViewById(R.id.btn_premium);

        bp = new BillingProcessor(this, getResources().getString(R.string.play_console_license), this);
        bp.initialize();
    }

    private boolean hasSubscription() {
        if (purchaseTransactionDetails != null) {
            return purchaseTransactionDetails.purchaseInfo != null;
        }
        return false;
    }

    @Override
    public void onBillingInitialized() {

        Log.d("PremiumActivity", "onBillingInitialized: ");

        String premium = getResources().getString(R.string.premium);

        purchaseTransactionDetails = bp.getSubscriptionTransactionDetails(premium);

        bp.loadOwnedPurchasesFromGoogle();

        btnPremium.setOnClickListener(v -> {
            if (bp.isSubscriptionUpdateSupported()) {
                bp.subscribe(this, premium);
            } else {
                Log.d("PremiumActivity", "onBillingInitialized: Subscription updated is not supported");
            }
        });

        if (hasSubscription()) {
            tvStatus.setText("Status: Premium");

            ((Infinity) this.getApplication()).setSomeVariable("Premium");
        } else {
            tvStatus.setText("Status: Free");

            ((Infinity) this.getApplication()).setSomeVariable("Free");
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d("PremiumActivity", "onProductPurchased: ");
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d("PremiumActivity", "onPurchaseHistoryRestored: ");

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d("PremiumActivity", "onBillingError: ");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


}