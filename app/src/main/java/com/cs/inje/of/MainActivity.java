package com.cs.inje.of;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.daum.mf.map.api.CameraUpdateFactory;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;



//Toast.makeText(MainActivity.this, ,Toast.LENGTH_SHORT).show();
public class MainActivity extends Activity implements MapView.POIItemEventListener{

    private final String dbName = "Shoptabase";
    private final String tableName = "shopInformations";

    SQLiteDatabase db;

    InjeUni inje = new InjeUni();
    SamBangDong sbd = new SamBangDong();
    Observatory obs = new Observatory();

    boolean isPageOpen = false;
    Animation translateRightAnim;
    Animation translateLeftAnim;
    LinearLayout slideMenu;


    MapView mapView;
    ViewGroup mapContainer;
    EditText userSearcher;
    Button userSearchButton;
    int pinPoint = 0;
    ///////////////////////////////////////////////////////////////////////////
    JavaExcel je = new JavaExcel();
    ArrayList<String[]> myArrayList = new ArrayList<String[]>();

    ///////////////////////////////////////////////////////////////////////////
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayList<ShopInfo> shopInfoArrayList = new ArrayList<ShopInfo>();
    ShopInfo si=new ShopInfo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try
        {
            db = this.openOrCreateDatabase(dbName,MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (shop_name VARCHAR(20), shop_type VARCHAR(20)," +
                    "shop_rating NUMERIC");

            //테이블이 존재하는 경우 기존 데이터를 지우기 위해서 사용합니다.  
            db.execSQL("DELETE FROM " + tableName);

            //새로운 데이터를 테이블에 집어넣습니다.. 
            /*for (int i=0; i<name.length; i++ ) {  
                sampleDB.execSQL("INSERT INTO " + tableName 
            + " (name, phone)  Values ('" + name[i] + "', '" + phone[i]+"');");  
            }  
  
            sampleDB.close();*/

        }catch(SQLiteException se)
        {
            Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",se.getMessage());
        }

        
        mapView = new MapView(this);
        mapView.setMapViewEventListener(new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {
                Toast.makeText(MainActivity.this,"Map Online",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewZoomLevelChanged(MapView mapView, int i) {

            }

            @Override
            public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

            }
        });
        mapView.setPOIItemEventListener(this);

        mapView.setDaumMapApiKey("5a6540f3c54f415749f330a9e5e83449");
        mapContainer = (ViewGroup)findViewById(R.id.map_view);
        mapContainer.addView(mapView);

        slideMenu = (LinearLayout)findViewById(R.id.slidemenu);

        translateLeftAnim = AnimationUtils.loadAnimation(this,R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this,R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        translateRightAnim.setAnimationListener(animListener);
        translateLeftAnim.setAnimationListener(animListener);

        userSearcher = (EditText)findViewById(R.id.TextSearch);
        userSearcher.setText("카페");
        userSearchButton = (Button)findViewById(R.id.search_Button);


        Spinner locationSpinner = (Spinner)findViewById(R.id.spinner_location);
        final ArrayAdapter locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        final Spinner infoSpinner = (Spinner)findViewById(R.id.spinner_info);
        final ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrayList);
        infoSpinner.setAdapter(arrAdapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                                  {
                                                      @Override
                                                      public void onItemSelected(AdapterView<?> parent, View view,
                                                                                 int position, long id)
                                                      {

                                                          final Searcher searcher = new Searcher();
                                                          int radius = 2000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                                                          int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                                                          String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;
                                                          String query="";
                                                          try {
                                                              query = URLEncoder.encode(userSearcher.getText().toString(),"UTF-8");
                                                          } catch (UnsupportedEncodingException e) {
                                                              e.printStackTrace();
                                                          }

                                                          if(position==0)
                                                          {
                                                              pinPoint = 0;
                                                              mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(inje.latitude, inje.longitude), true);
                                                              searcher.searchKeyword(getApplicationContext(), userSearcher.getText().toString(), inje.latitude, inje.longitude, radius, page, apikey, new OnFinishSearchListener()
                                                              {
                                                                  @Override
                                                                  public void onSuccess(List<Item> itemList){
                                                                      mapView.removeAllPOIItems();
                                                                      showResult(itemList); // 검색 결과 보여줌
                                                                  }
                                                                  @Override
                                                                  public void onFail() {
                                                                  }
                                                              });
                                                              XMLParser(query,position);
                                                              arrayList.clear();
                                                              for(int i=0;i<shopInfoArrayList.size();i++)
                                                              {
                                                                  arrayList.add(shopInfoArrayList.get((i)).title);
                                                              }
                                                              arrAdapter.notifyDataSetChanged();
                                                          }
                                                          else if(position==1)
                                                          {
                                                              //latitude = 35.257637;
                                                              //longitude = 128.906903;
                                                              pinPoint = 1;

                                                              mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(sbd.latitude, sbd.longitude), true);
                                                              searcher.searchKeyword(getApplicationContext(), userSearcher.getText().toString(), sbd.latitude, sbd.longitude, radius, page, apikey, new OnFinishSearchListener()
                                                              {
                                                                  @Override
                                                                  public void onSuccess(List<Item> itemList) {
                                                                      mapView.removeAllPOIItems();
                                                                      showResult(itemList); // 검색 결과 보여줌
                                                                  }
                                                                  @Override
                                                                  public void onFail() {
                                                                  }
                                                              });
                                                              XMLParser(query,position);
                                                              arrayList.clear();
                                                              for(int i=0;i<shopInfoArrayList.size();i++)
                                                              {
                                                                  arrayList.add(shopInfoArrayList.get((i)).title);
                                                              }
                                                              arrAdapter.notifyDataSetChanged();
                                                          }
                                                          else if(position==2)
                                                          {
                                                              //latitude = 35.252767;
                                                              //longitude = 128.887351;
                                                              pinPoint = 2;

                                                              mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(obs.latitude, obs.longitude), true);
                                                              searcher.searchKeyword(getApplicationContext(), userSearcher.getText().toString(), obs.latitude, obs.longitude, radius, page, apikey, new OnFinishSearchListener()
                                                              {
                                                                  @Override
                                                                  public void onSuccess(List<Item> itemList) {
                                                                      mapView.removeAllPOIItems();
                                                                      showResult(itemList); // 검색 결과 보여줌
                                                                  }
                                                                  @Override
                                                                  public void onFail() {
                                                                  }
                                                              });
                                                              XMLParser(query,position);
                                                              arrayList.clear();
                                                              for(int i=0;i<shopInfoArrayList.size();i++)
                                                              {
                                                                  arrayList.add(shopInfoArrayList.get((i)).title);
                                                              }
                                                              arrAdapter.notifyDataSetChanged();
                                                          }
                                                          if(!isPageOpen)
                                                          {
                                                              isPageOpen = true;
                                                              slideMenu.setVisibility(View.VISIBLE);
                                                              slideMenu.startAnimation(translateLeftAnim);
                                                          }

                                                      }
                                                      @Override
                                                      public void onNothingSelected(AdapterView<?> parent) {}
                                                  }
        );


        infoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                              {
                                                  @Override
                                                  public void onItemSelected(AdapterView<?> parent, View view,
                                                                             int position, long id)
                                                  {
                                                      double latitude;
                                                      double longitude;
                                                      try
                                                      {
                                                          latitude = Double.parseDouble(shopInfoArrayList.get(position).latitude);
                                                          longitude = Double.parseDouble(shopInfoArrayList.get(position).longitude);
                                                          mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 2, true);
                                                          mapView.selectPOIItem(mapView.findPOIItemByTag(infoSpinner.getSelectedItemPosition()),true);
                                                      }
                                                      catch(Exception e)
                                                      {

                                                      }


                                                  }
                                                  @Override
                                                  public void onNothingSelected(AdapterView<?> parent) {}
                                              }
        );
        userSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = 0;
                double longitude = 0;
                final Searcher searcher = new Searcher();
                int radius = 2000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;
                String query="";
                try {
                    query = URLEncoder.encode(userSearcher.getText().toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(pinPoint == 0)
                {
                    latitude = inje.latitude;
                    longitude = inje.longitude;
                }
                else if(pinPoint == 1)
                {
                    latitude = sbd.latitude;
                    longitude = sbd.longitude;
                }
                else if(pinPoint == 2)
                {
                    latitude = obs.latitude;
                    longitude = obs.longitude;
                }
                ///////////////////////////////////////////////////////////////////////////////////////////
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                searcher.searchKeyword(getApplicationContext(), userSearcher.getText().toString(), latitude, longitude, radius, page, apikey, new OnFinishSearchListener()
                {
                    @Override
                    public void onSuccess(List<Item> itemList) {
                        mapView.removeAllPOIItems();
                        showResult(itemList); // 검색 결과 보여줌
                    }
                    @Override
                    public void onFail() {
                    }
                });
                XMLParser(query,pinPoint);
                arrayList.clear();
                for(int i=0;i<shopInfoArrayList.size();i++)
                {
                    arrayList.add(shopInfoArrayList.get((i)).title);
                }
                arrAdapter.notifyDataSetChanged();
                if(!isPageOpen)
                {
                    isPageOpen = true;
                    slideMenu.setVisibility(View.VISIBLE);
                    slideMenu.startAnimation(translateLeftAnim);
                }
            }
        });
    }
    private void showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.marker);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.marked);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f,1.0f);


            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
        }

    }
    private void XMLParser(final String query, final int position)
    {new Thread(new Runnable() {
        @Override
        public void run() {
            URL url;
            double Platitude = 0;
            double Plongitude = 0;
            if(position == 0)
            {
                Platitude = inje.latitude;
                Plongitude = inje.longitude;
            }
            else if(position == 1)
            {
                Platitude = sbd.latitude;
                Plongitude = sbd.longitude;
            }
            else if(position == 2)
            {
                Platitude = obs.latitude;
                Plongitude = obs.longitude;
            }
            try {
                url = new URL("https://apis.daum.net/local/v1/search/keyword.XML?apikey=fdc4ea31d5493ea2d1a18dcfc6010379&query="+query+"&location="+ Double.toString(Platitude) + ","+ Double.toString(Plongitude) + "&radius=2000");
                URLConnection connection = url.openConnection();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(url.openStream(), "UTF-8");
                int parserEvent = parser.getEventType();
                String tag;
                shopInfoArrayList.clear();
                boolean placeUrl = false, category = false, newAddress = false, title = false, phone = false, imageUrl = false, address = false, longitude = false,
                        latitude = false, last = false;
                while (parserEvent != XmlPullParser.END_DOCUMENT) {

                    switch (parserEvent) {
                        case XmlPullParser.START_DOCUMENT:
                        case XmlPullParser.END_DOCUMENT:
                        case XmlPullParser.END_TAG:
                            break;
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("placeUrl"))
                                placeUrl = true;
                            else if(parser.getName().equals("category"))
                                category=true;
                            else if(parser.getName().equals("newAddress"))
                                newAddress=true;
                            else if(parser.getName().equals("title"))
                                title=true;
                            else if(parser.getName().equals("phone"))
                                phone=true;
                            else if(parser.getName().equals("imageUrl"))
                                imageUrl=true;
                            else if(parser.getName().equals("address"))
                                address=true;
                            else if(parser.getName().equals("longitude"))
                                longitude=true;
                            else if(parser.getName().equals("latitude"))
                                latitude=true;
                            break;
                        case XmlPullParser.TEXT:
                            if(placeUrl)
                            {
                                si.placeUrl=parser.getText();
                                placeUrl = false;
                            }
                            else if(category)
                            {
                                si.category=parser.getText();
                                category = false;
                            }
                            else if(newAddress)
                            {
                                si.newAddress=parser.getText();
                                newAddress = false;
                            }
                            else if(title)
                            {
                                si.title=parser.getText();
                                title=false;
                            }
                            else if(phone)
                            {
                                si.phone=parser.getText();
                                phone = false;
                            }
                            else if(imageUrl)
                            {
                                si.imageUrl=parser.getText();
                                imageUrl = false;
                            }
                            else if(address)
                            {
                                si.address=parser.getText();
                                address = false;
                            }
                            else if(longitude)
                            {
                                si.longitude=parser.getText();
                                longitude = false;
                                shopInfoArrayList.add(si);
                                si = new ShopInfo();
                            }
                            else if(latitude)
                            {
                                si.latitude=parser.getText();
                                latitude = false;
                            }
                            break;
                    }
                    parserEvent = parser.next();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    ).start();

    }


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        Spinner infoSpinner = (Spinner)findViewById(R.id.spinner_info);
        for(int i=0;i<shopInfoArrayList.size();i++)
        {
            if(mapPOIItem.getItemName().equals(shopInfoArrayList.get(i).title))
            {
                infoSpinner.setSelection(i);
                if(!isPageOpen)
                {
                    isPageOpen = true;
                    setInfoMenu(shopInfoArrayList.get(i));
                    slideMenu.setVisibility(View.VISIBLE);
                    slideMenu.startAnimation(translateLeftAnim);
                }
                else
                {
                        setInfoMenu(shopInfoArrayList.get(i));
                }
            }
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onBackPressed()
    {
        if(isPageOpen)
        {
            slideMenu.startAnimation(translateRightAnim);
            slideMenu.setVisibility(View.INVISIBLE);
            isPageOpen = false;
        }
        else
        {
            finishAffinity();
        }
    }

    public void setInfoMenu(ShopInfo information){

        ImageView img = (ImageView)findViewById(R.id.shop_img);
        TextView title = (TextView)findViewById(R.id.shop_title);
        TextView category = (TextView)findViewById(R.id.shop_category);
        TextView address = (TextView)findViewById(R.id.shop_newAddress);
        TextView phone = (TextView)findViewById(R.id.shop_phone);
        TextView placeurl = (TextView)findViewById(R.id.shop_placeUrl);
        TextView rating = (TextView)findViewById(R.id.rating);

        new DownloadImageTask(img).execute(information.imageUrl);
        title.setText(information.title);
        category.setText(information.category);
        address.setText(information.address);
        phone.setText(information.phone);
        placeurl.setText(information.placeUrl);
        rating.setText(getRating(information.title));
    }

    public void showMenu()
    {
        if(!isPageOpen)
        {
            isPageOpen = true;
            slideMenu.setVisibility(View.VISIBLE);
            slideMenu.startAnimation(translateLeftAnim);
        }
        else
        {
            isPageOpen = false;
            slideMenu.startAnimation(translateRightAnim);
            slideMenu.setVisibility(View.INVISIBLE);
        }
    }

    public String getRating(String shopName)
    {
        try
        {
            myArrayList = je.readExcel("DataBase.xlsx");
            for(String[] tmp : myArrayList)
            {
                for(int i = 0; i < tmp.length; i++)
                {
                    if(shopName == tmp[i])
                    {
                        return tmp[i+2];
                    }
                }
            }
        }
        catch (IOException e)
        {

        }
        return "";
    }


    class InjeUni
    {
        double latitude = 35.248713; // 위도
        double longitude = 128.902721; // 경도
    }
    class SamBangDong{
        double latitude = 35.257637;
        double longitude = 128.906903;
    }
    class Observatory
    {
        double latitude = 35.252767;
        double longitude = 128.887351;
    }

}

