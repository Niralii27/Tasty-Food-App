package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private FAQAdapter faqAdapter;

    ImageView img_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FAQItem> faqList = new ArrayList<>();
        faqList.add(new FAQItem("How do I place an order?", "To place an order, browse our menu, select your items, and proceed to checkout."));
        faqList.add(new FAQItem("What payment methods are accepted?", "We accept cash on delivery, credit cards, debit cards, and online payment options."));
        faqList.add(new FAQItem("How do I track my order?", "Once your order is placed, you can track its status in the 'My Orders' section."));
        faqList.add(new FAQItem("Can I cancel my order?", "Yes, you can cancel your order before it is dispatched."));
        faqList.add(new FAQItem("What if my order is delayed?", "If your order is delayed, please contact our support team for assistance."));
        faqList.add(new FAQItem("How do I update my address?", "You can update your address in the 'Profile' section of the app."));

        faqAdapter = new FAQAdapter(faqList);
        faqRecyclerView.setAdapter(faqAdapter);
    }




    public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

        private List<FAQItem> faqList;

        public FAQAdapter(List<FAQItem> faqList) {
            this.faqList = faqList;
        }

        @NonNull
        @Override
        public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
            return new FAQViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
            FAQItem faq = faqList.get(position);
            holder.questionTextView.setText(faq.getQuestion());
            holder.answerTextView.setText(faq.getAnswer());

            // Expand/Collapse logic
            holder.itemView.setOnClickListener(v -> {
                if (holder.answerTextView.getVisibility() == View.GONE) {
                    holder.answerTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.answerTextView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return faqList.size();
        }

         class FAQViewHolder extends RecyclerView.ViewHolder {
            TextView questionTextView, answerTextView;

            public FAQViewHolder(@NonNull View itemView) {
                super(itemView);
                questionTextView = itemView.findViewById(R.id.questionTextView);
                answerTextView = itemView.findViewById(R.id.answerTextView);
            }
        }
    }

}