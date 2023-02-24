package com.example.gosagentnewrelease.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosagentnewrelease.data.PluginModel;
import com.example.gosagentnewrelease.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class AdapterInfoWindow extends RecyclerView.Adapter<AdapterInfoWindow.LotViewHolder> {
    static class LotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final static String ADD_FAVORITES_LOT = "Добавить в избранные";
        private final static String DELETE_FAVORITES_LOT = "Удалить из избранных";

        private final Button searchButton;
        private final ImageView imageView;
        private final TextView textNumber;
        private final TextView textLot;
        private ViewFlipper viewFlipper;
        private final TextView textDescription;
        private final Button link;
        private String linkText;
        private final TextView textAddresses;
        private final TextView textPrice;

        private InformationPages informationPages;

        private Context context;

        public LotViewHolder(View itemView) {
            super(itemView);
            searchButton = itemView.findViewById(R.id.search_button);
            link = itemView.findViewById(R.id.textLink);
            imageView = itemView.findViewById(R.id.imageType);
            textNumber = itemView.findViewById(R.id.textNumber);
            textLot = itemView.findViewById(R.id.textLot);
            viewFlipper = itemView.findViewById(R.id.view_flipper);
            textDescription = itemView.findViewById(R.id.textDescription);
            textAddresses = itemView.findViewById(R.id.textAdress);
            textPrice = itemView.findViewById(R.id.textPrice);
            viewFlipper = itemView.findViewById(R.id.view_flipper);

            searchButton.setOnClickListener(this);
            link.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setOnPageInformation(InformationPages information, Context context) {
            informationPages = information;

            imageView.setBackground(information.getImage());
            textNumber.setText(information.getNameOfLot());
            textLot.setText(information.getTextLot());
            textDescription.setText(information.getDescription());
            textAddresses.setText(information.getAddresses());

            NumberFormat format = NumberFormat.getCurrencyInstance();
            Double priceValue = Double.valueOf(information.getPrice());
            textPrice.setText(format.format(priceValue));

            linkText = information.getLink();

            ImageLoader imageLoader = new ImageLoader();
            imageLoader.execute(context, information);

            setTextFavoritesLot(PluginModel.Data.existFavoritesLots(information.getNameOfLot()));
        }

        class ImageLoader {
            ExecutorService executor;
            Handler handler;

            public void execute(Context context, InformationPages information) {
                executor = Executors.newSingleThreadExecutor();
                handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    List<Bitmap> image = new ArrayList<>();
                    SSLContext trustCertContext = null;
                    try {
                        trustCertContext = trustCert(context);
                    } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
                        e.printStackTrace();
                    }

                    for (String src : information.getImageLink()) {
                        assert trustCertContext != null;
                        Bitmap loadImage;
                        String typeConnection = src.split(":")[0];
                        if (typeConnection.equals("http"))
                            loadImage = getBitmapFromURLHTTP(src);
                        else
                            loadImage = getBitmapFromURL(src, trustCertContext);
                        if (loadImage != null)
                            image.add(loadImage);
                    }
                    handler.post(() ->  loadImageToFliper(image, context));
                });
            }
        }

        private Bitmap getBitmapFromURLHTTP(String src) {
            try {
                Log.e("src", src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);

                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap","returned");
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
                return null;
            }
        }

        private Bitmap getBitmapFromURL(String src, SSLContext trustCert) {
            try {
                Log.e("src", src);
                URL url = new URL(src);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);

                connection.setSSLSocketFactory(trustCert.getSocketFactory());

                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap","returned");
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
                return null;
            }
        }

        private SSLContext trustCert(Context context) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
            AssetManager assetManager = context.getAssets();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(assetManager.open("fyicenter-certificate-8946.crt"));

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext;
        }

        void loadImageToFliper(List<Bitmap>  information, Context context) {
            for (Bitmap image: information) {
                ImageView imageView = new ImageView(context);
                imageView.setImageBitmap(image);
                imageView.setOnClickListener(this);
                viewFlipper.addView(imageView);
            }
        }

        void setTextFavoritesLot(Boolean isDelete) {
            if (isDelete)
                searchButton.setText(DELETE_FAVORITES_LOT);
            else
                searchButton.setText(ADD_FAVORITES_LOT);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView) {
                viewFlipper.showNext();
            }

            fListener.onButtonClick(v, informationPages);

            if (v.getId() == R.id.search_button)
                setTextFavoritesLot(PluginModel.Data.existFavoritesLots(informationPages.getNameOfLot()));
        }
    }

    public interface OnLotClickListener {
        void onButtonClick(View view, InformationPages informationPages);
    }

    private final List<InformationPages> informationOnPageList;
    public static OnLotClickListener fListener;

    private final Context context;

    public void setLotClickListener(OnLotClickListener listener) {
        fListener = listener;
    }

    public AdapterInfoWindow(List<InformationPages> informationOnPageList, Context context) {
        this.informationOnPageList = informationOnPageList;
        this.context = context;
    }

    public void clear() {
        informationOnPageList.clear();
    }

    @NonNull
    @Override
    public LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.information_lot_pattern,
                                                                                            parent, false));
    }

    @Override
    public void onBindViewHolder(LotViewHolder lotViewHolder, int position) {
        lotViewHolder.setOnPageInformation(informationOnPageList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return informationOnPageList.size();
    }

}
