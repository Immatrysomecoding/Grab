package com.example.myapplication.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubscriptionDetailActivity extends AppCompatActivity {

    private String subscriptionType; // "bike" or "car"
    private String subscriptionId;
    private DatabaseReference mDatabase;

    // UI Components
    private ImageButton btnBack;
    private TextView tvSubscriptionTitle, tvSubscriptionSubtitle;
    private TextView tvPrice, tvDuration;
    private TextView tvDiscountPercent, tvWorthAmount;
    private ImageView ivBikeService, ivCarService;
    private TextView tvBikeDiscount, tvCarDiscount;
    private TextView tvBikeLimit, tvCarLimit;
    private LinearLayout bikeInfoLayout, carInfoLayout;

    // Expandable sections
    private LinearLayout benefitsCard, howPackagesWorkCard, termsCard;
    private LinearLayout benefitsContent, howPackagesWorkContent, termsContent;
    private TextView tvBenefitsArrow, tvHowPackagesArrow, tvTermsArrow;
    private TextView tvBenefitsContentText, tvHowPackagesWorkContentText, tvTermsContentText;

    private Button btnChoosePackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_detail);

        // Get subscription type from intent
        subscriptionType = getIntent().getStringExtra("subscription_type");
        if (subscriptionType == null) {
            subscriptionType = "bike"; // Default to bike
        }

        subscriptionId = subscriptionType.equals("bike") ? "bike_subscription" : "car_subscription";

        initViews();
        setupDatabase();
        loadSubscriptionDetails();
        setupListeners();
        setupExpandableSections();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSubscriptionTitle = findViewById(R.id.tvSubscriptionTitle);
        tvSubscriptionSubtitle = findViewById(R.id.tvSubscriptionSubtitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvDuration = findViewById(R.id.tvDuration);
        tvDiscountPercent = findViewById(R.id.tvDiscountPercent);
        tvWorthAmount = findViewById(R.id.tvWorthAmount);

        ivBikeService = findViewById(R.id.ivBikeService);
        ivCarService = findViewById(R.id.ivCarService);
        tvBikeDiscount = findViewById(R.id.tvBikeDiscount);
        tvCarDiscount = findViewById(R.id.tvCarDiscount);
        tvBikeLimit = findViewById(R.id.tvBikeLimit);
        tvCarLimit = findViewById(R.id.tvCarLimit);

        bikeInfoLayout = findViewById(R.id.bikeInfoLayout);
        carInfoLayout = findViewById(R.id.carInfoLayout);

        // Expandable sections
        benefitsCard = findViewById(R.id.benefitsCard);
        howPackagesWorkCard = findViewById(R.id.howPackagesWorkCard);
        termsCard = findViewById(R.id.termsCard);

        benefitsContent = findViewById(R.id.benefitsContent);
        howPackagesWorkContent = findViewById(R.id.howPackagesWorkContent);
        termsContent = findViewById(R.id.termsContent);

        tvBenefitsArrow = findViewById(R.id.tvBenefitsArrow);
        tvHowPackagesArrow = findViewById(R.id.tvHowPackagesArrow);
        tvTermsArrow = findViewById(R.id.tvTermsArrow);

        tvBenefitsContentText = findViewById(R.id.tvBenefitsContentText);
        tvHowPackagesWorkContentText = findViewById(R.id.tvHowPackagesWorkContentText);
        tvTermsContentText = findViewById(R.id.tvTermsContentText);

        btnChoosePackage = findViewById(R.id.btnChoosePackage);
    }

    private void setupDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupExpandableSections() {
        // Set content for benefit terms
        String benefitsContent = "• Gói có thời gian sử dụng trong vòng 15 ngày.\n" +
                "- Ưu đãi giảm 25%, tối đa 30.000đ cho dịch vụ GrabBike. Số lượng: 99 lần sử dụng. Áp dụng cho tất cả tỉnh thành.\n" +
                "- Ưu đãi giảm 10%, tối đa 50.000đ cho dịch vụ GrabCar. Số lượng: 10 lần sử dụng. Áp dụng cho tất cả tỉnh thành.\n" +
                "- Ưu đãi áp dụng cho tất cả các phương thức thanh toán.";
        tvBenefitsContentText.setText(benefitsContent);

        // Set content for how packages work
        String howPackagesWork = "• Sau khi mua Gói Ưu Đãi, bạn sẽ nhận được các mã ưu đãi tại mục \"Ưu Đãi Của Tôi\" trên ứng dụng Grab\n" +
                "Để sử dụng ưu đãi, nhấn chọn \"Ưu Đãi\" trong màn hình thanh toán, tìm ưu đãi tương ứng từ Gói Ưu Đãi của bạn và chọn \"Sử dụng bây giờ\" (ưu đãi sẽ được tự động áp dụng trên mỗi chuyến xe không giới hạn). Sử dụng để tận hưởng dịch vụ đi chuyển Grab để tiết kiệm hơn!";
        tvHowPackagesWorkContentText.setText(howPackagesWork);

        // Set content for terms
        String termsContent = "• Các Gói Ưu Đãi chỉ có thể được thanh toán thông qua Ví điện tử/Thẻ Ngân hàng trên ứng dụng Grab.\n" +
                "Số lượng mỗi Gói đều có giới hạn. Khi các Gói đã được mua hết, các Gói sẽ không còn hiển thị trong ứng dụng Grab. Xin lưu ý rằng Gói Ưu Đãi không thể chuyển nhượng, không thể trao đổi.\n" +
                "Bạn sẽ không nhận được điểm thưởng GrabRewards khi mua Gói Ưu Đãi.\n" +
                "Grab có quyền thay đổi các Gói Ưu Đãi bất cứ lúc nào. Bạn sẽ được thông báo trước qua email và/hoặc thông báo trong ứng dụng.\n" +
                "Grab có quyền hủy bỏ, thay đổi và/hoặc điều chỉnh bất kỳ phần nào hoặc toàn bộ các Điều Khoản và Điều Kiện của Gói Ưu Đãi vào bất cứ thời điểm nào mà không cần thông báo trước.\n" +
                "Grab cũng có quyền loại trừ và/ hoặc từ chối bất kỳ người dùng nào cho các Gói Ưu Đãi (giới hạn), bao gồm những không giới hạn các trường hợp người dùng sử dụng nhiều thiết bị đã bị sửa đổi, thiết bị không có bản quyền hoặc thiết bị có cài đặt ứng dụng đã bị sửa đổi - có khả năng thay đổi trải nghiệm sử dụng của người dùng Grab và can thiệp vào quyền riêng tư và bảo mật tài khoản người dùng.\n" +
                "Gói Ưu Đãi chỉ được hiển thị trên phiên bản mới nhất của ứng dụng Grab.\n" +
                "Gói Ưu Đãi chỉ áp dụng cho những người dùng nhận được thông tin Gói Ưu Đãi trên ứng dụng Grab.";
        tvTermsContentText.setText(termsContent);
    }

    private void loadSubscriptionDetails() {
        mDatabase.child("subscriptions").child(subscriptionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);
                    String duration = snapshot.child("duration").getValue(String.class);
                    String discountPercent = snapshot.child("discountPercent").getValue(String.class);
                    String worthAmount = snapshot.child("worthAmount").getValue(String.class);
                    String bikeDiscount = snapshot.child("bikeDiscount").getValue(String.class);
                    String carDiscount = snapshot.child("carDiscount").getValue(String.class);
                    String bikeLimit = snapshot.child("bikeLimit").getValue(String.class);
                    String carLimit = snapshot.child("carLimit").getValue(String.class);

                    // Set data to views
                    tvSubscriptionTitle.setText(title);
                    tvSubscriptionSubtitle.setText(title + " • From " + price + "/" + duration);
                    tvPrice.setText(price);
                    tvDuration.setText("/ " + duration);
                    tvDiscountPercent.setText("Giảm đến " + discountPercent);
                    tvWorthAmount.setText("Worth up to " + worthAmount);

                    tvBikeDiscount.setText("Giảm " + bikeDiscount + " GrabBike");
                    tvCarDiscount.setText("Giảm " + carDiscount + " GrabCar");
                    tvBikeLimit.setText(bikeLimit + " / " + duration);
                    tvCarLimit.setText(carLimit + " / " + duration);

                    // Show/hide service info based on subscription type
                    if (subscriptionType.equals("bike")) {
                        bikeInfoLayout.setVisibility(View.VISIBLE);
                        carInfoLayout.setVisibility(View.VISIBLE); // Changed to VISIBLE to show both
                    } else {
                        bikeInfoLayout.setVisibility(View.VISIBLE); // Changed to VISIBLE to show both
                        carInfoLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Expandable sections
        benefitsCard.setOnClickListener(v -> {
            toggleSection(benefitsContent, tvBenefitsArrow);
        });

        howPackagesWorkCard.setOnClickListener(v -> {
            toggleSection(howPackagesWorkContent, tvHowPackagesArrow);
        });

        termsCard.setOnClickListener(v -> {
            toggleSection(termsContent, tvTermsArrow);
        });

        btnChoosePackage.setOnClickListener(v -> {
            // Go to subscription review screen
            Intent intent = new Intent(SubscriptionDetailActivity.this, SubscriptionReviewActivity.class);
            intent.putExtra("subscription_type", subscriptionType);
            intent.putExtra("subscription_id", subscriptionId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void toggleSection(LinearLayout content, TextView arrow) {
        if (content.getVisibility() == View.VISIBLE) {
            content.setVisibility(View.GONE);
            arrow.setText("▼"); // Down arrow
        } else {
            content.setVisibility(View.VISIBLE);
            arrow.setText("▲"); // Up arrow
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}