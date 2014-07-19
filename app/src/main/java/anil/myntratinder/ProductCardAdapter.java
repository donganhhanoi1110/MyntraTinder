package anil.myntratinder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.Background;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anil on 7/18/2014.
 */
public class ProductCardAdapter extends BaseAdapter {

    List<Product> mItems;
    Context mContext;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    public ProductCardAdapter(Context context) {
        // todo: here you need to populate mItems from the json file,
        // should we download the json file here?
        mContext = context;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        if (isNetworkAvailable()){
            downloadJsonToFile("url", "postdata", "products.json"); // todo: update url, post data here
            mItems = ProductsJSONPullParser.getProductsFromFile(mContext, "products.json");
        } else {
            // todo: notify network isn't available
            mItems = new ArrayList<Product>();
            for (int i = 1; i < 15; i++){
                Product p = new Product(i);
                p.setImageUrl("sampleImageurl"); //todo: add sample image url here
                mItems.add(p);
            }
        }
    }

    @Background
    private void downloadJsonToFile(String url, String postdata, String filename) {
        try {
            Downloader.downloadFromUrl(url, postdata, mContext.openFileOutput(filename, Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Product getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        //todo: update this function properly
        SingleProductView singleProductView;
        if (convertView == null) {
            singleProductView = SingleProductView_.build(mContext);
        } else {
            singleProductView = (SingleProductView) convertView;
        }

        singleProductView.bind(getItem(position));

        ImageView productImage = (ImageView)singleProductView.findViewById(R.id.picture);
        // todo: maybe we need a progressbar when the image is loading?
        // todo: update product_card layout to include discounted price/discount progressbar and etc

        ImageLoadingListener listener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };
        imageLoader.displayImage(getItem(position).getImageUrl(), productImage, options, listener);

        return singleProductView;
    }
}
