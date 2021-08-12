package ml.docilealligator.infinityforreddit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ml.docilealligator.infinityforreddit.R;

public class PremiumActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    public static final String PREF_FILE= "MyPref";
    public static final String PURCHASE_KEY= "premium";
    public static final String PRODUCT_ID= "premium";

    TextView purchaseStatus;
    Button purchaseButton;

    private BillingClient billingClient;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        purchaseStatus=(TextView) findViewById(R.id.purchase_status);
        purchaseButton=(Button) findViewById(R.id.purchase_button);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // Establish connection to billing client
        //check purchase status from google play store cache
        //to check if item already Purchased previously or refunded
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                    //if purchase list is empty that means item is not purchased
                    //Or purchase is refunded or canceled
                    else{
                        savePurchaseValueToPref(false);
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });

        //item Purchased
        if(getPurchaseValueFromPref()){
            purchaseButton.setVisibility(View.GONE);
            purchaseStatus.setText("Purchase Status : Upgraded to pro");
        }
        //item not Purchased
        else{
            purchaseButton.setVisibility(View.VISIBLE);
            purchaseStatus.setText("Purchase Status : Free Version");
        }
    }

    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }
    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }
    private boolean getPurchaseValueFromPref(){
        return getPreferenceObject().getBoolean( PURCHASE_KEY,false);
    }
    private void savePurchaseValueToPref(boolean value){
        getPreferenceEditObject().putBoolean(PURCHASE_KEY,value).commit();
    }

    //initiate purchase on button click
    public void purchase(View view) {
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase();
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase();
                    } else {
                        Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }
    private void initiatePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(PremiumActivity.this, flowParams);
                            }
                            else{
                                //try to add item/product id "purchase" inside managed product in google play console
                                Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    " Error "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is purchased
            if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }
                //else item is purchased and also acknowledged
                else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    if(!getPurchaseValueFromPref()){

                        StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased");
                        Uri uri1 = Uri.parse("android.resource://com.rackluxury.explorerforreddit/drawable/expensive_checker");
                        UploadTask uploadTask = imageReference1.putFile(uri1);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(PremiumActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                            }
                        });

                        savePurchaseValueToPref(true);
                        Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                        this.recreate();
                    }
                }
            }
            //if purchase is pending
            else if( PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
            //if purchase is unknown
            else if(PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
                savePurchaseValueToPref(false);
                purchaseButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }
    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity

                StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased");
                Uri uri1 = Uri.parse("android.resource://com.rackluxury.explorerforreddit/drawable/expensive_checker");
                UploadTask uploadTask = imageReference1.putFile(uri1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(PremiumActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                    }
                });
                savePurchaseValueToPref(true);
                Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                PremiumActivity.this.recreate();


            }
        }
    };

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxQ7nzMIbyikC49rOrSpZfWd1i6SP0zlP9lZUuIn0k1QAQjSdyfy+ABEPa++//tfVze+zBhyJFXhU6oAAm9PnpLDPkBWvQg7qFOys9kC30OY3ipI9cJr6cvcYxVeHgApKcL6021JOxoFu+ml8wZV4Yys4MGgRdlyKGxRbxdVy5XGELc9G/vmVFrguo3p7g1BDMIetRoD9Y3niCnEUIXHtxe49UHr7rYMiv4lfmTRS6OXiRKcDg4he430FOksAfp1JFli5E9PFCYy5Hcu6T/hgG1o/wBqWTnIw3H48SuwUYC3hoix5EP8u78HuwWuAxd9sbfsJDbYB97nHrTj7egqP2wIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }
}