package disca.dadm.valenbike.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.InformationAdapter;
import disca.dadm.valenbike.models.Information;

public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Information> informationList;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewContact);
        ImageView ivWeb = view.findViewById(R.id.ivWeb);
        ImageView ivInstagram = view.findViewById(R.id.ivInstagram);
        ImageView ivFacebook = view.findViewById(R.id.ivFacebook);
        ImageView ivTwitter = view.findViewById(R.id.ivTwitter);

        intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        ivWeb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView imageView = (ImageView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            imageView.getDrawable().setColorFilter(new BlendModeColorFilter(0x77000000, BlendMode.SRC_ATOP));
                        }
                        imageView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        intent.setData(Uri.parse("http://cas.valenbisi.es/"));
                        startActivity(intent);
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView imageView = (ImageView) v;
                        imageView.getDrawable().clearColorFilter();
                        imageView.invalidate();
                    }
                }
                return false;
            }
        });

        ivInstagram.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView imageView = (ImageView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            imageView.getDrawable().setColorFilter(new BlendModeColorFilter(0x77000000, BlendMode.SRC_ATOP));
                        }
                        imageView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        intent.setData(Uri.parse("https://www.instagram.com/valenbisioficial/?hl=es"));
                        startActivity(intent);
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView imageView = (ImageView) v;
                        imageView.getDrawable().clearColorFilter();
                        imageView.invalidate();
                    }
                }
                return false;
            }
        });

        ivFacebook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView imageView = (ImageView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            imageView.getDrawable().setColorFilter(new BlendModeColorFilter(0x77000000, BlendMode.SRC_ATOP));
                        }
                        imageView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        intent.setData(Uri.parse("https://www.facebook.com/ValenbisiOficial/"));
                        startActivity(intent);
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView imageView = (ImageView) v;
                        imageView.getDrawable().clearColorFilter();
                        imageView.invalidate();
                    }
                }
                return false;
            }
        });

        ivTwitter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView imageView = (ImageView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            imageView.getDrawable().setColorFilter(new BlendModeColorFilter(0x77000000, BlendMode.SRC_ATOP));
                        }
                        imageView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        intent.setData(Uri.parse("https://twitter.com/ValenbisiOfi"));
                        startActivity(intent);
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView imageView = (ImageView) v;
                        imageView.getDrawable().clearColorFilter();
                        imageView.invalidate();
                    }
                }
                return false;
            }
        });

        initData();
        initRecyclerView();

        return view;
    }

    private void initData() {
        informationList = new ArrayList<>();
        informationList.add(new Information(getResources().getString(R.string.cardview_contact_title1), getResources().getString(R.string.cardview_contact_body1)));
        informationList.add(new Information(getResources().getString(R.string.cardview_contact_title2), getResources().getString(R.string.cardview_contact_body2)));
        informationList.add(new Information(getResources().getString(R.string.cardview_contact_title3), getResources().getString(R.string.cardview_contact_body3)));
        informationList.add(new Information(getResources().getString(R.string.cardview_contact_title4), getResources().getString(R.string.cardview_contact_body4)));

    }

    private void initRecyclerView() {
        InformationAdapter informationAdapter = new InformationAdapter(informationList);
        recyclerView.setAdapter(informationAdapter);
    }
}
