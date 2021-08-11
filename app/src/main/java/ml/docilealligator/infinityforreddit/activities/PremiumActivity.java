package ml.docilealligator.infinityforreddit.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ml.docilealligator.infinityforreddit.Infinity;
import ml.docilealligator.infinityforreddit.R;

public class PremiumActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private TextView tvStatus;
    private Button btnPremium;
    private TransactionDetails purchaseTransactionDetails = null;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

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
            StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased");
            Uri uri1 = Uri.parse("android.resource://com.rackluxury.rolex/drawable/expensive_checker");
            UploadTask uploadTask = imageReference1.putFile(uri1);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toasty.error(ExpensiveCheckerActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                }
            });
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toasty.success(ExpensiveCheckerActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();
                    finish();
                    Intent openExpensiveFromExpensiveChecker = new Intent(ExpensiveCheckerActivity.this, ExpensiveActivity.class);
                    startActivity(openExpensiveFromExpensiveChecker);
                    Animatoo.animateSwipeRight(ExpensiveCheckerActivity.this);
                }
            });

            FirebaseMessaging.getInstance().unsubscribeFromTopic("purchase_expensive");


        } else {
            tvStatus.setText("Status: Free");


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