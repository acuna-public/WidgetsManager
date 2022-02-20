  package ru.ointeractive.widgetsmanager.providers;
  /*
   Created by Acuna on 11.07.2018
  */
  
  import android.content.Intent;
  import android.view.View;
  import android.widget.RemoteViews;
  
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Map;
  
  import ru.ointeractive.androdesign.adapter.ItemsAdapter;
  import ru.ointeractive.androdesign.widget.ListItem;
  import ru.ointeractive.widgetsmanager.Const;
  import ru.ointeractive.widgetsmanager.ListProvider;
  import ru.ointeractive.widgetsmanager.Adapter;
  import ru.ointeractive.widgetsmanager.R;
  import ru.ointeractive.widgetsmanager.WidgetsManager;
  import ru.ointeractive.widgetsmanager.WidgetsManagerException;
  
  public class Stub extends Adapter {
    
    private Stub (WidgetsManager manager) {
      super (manager.context, null);
    }
    
    @Override
    public Adapter getInstance (WidgetsManager widget) {
      return new Stub (widget);
    }
    
    @Override
    public WidgetsManager.Layouts getLayouts () {
      
      WidgetsManager.Layouts layouts = new WidgetsManager.Layouts ();
      
      layouts.widgetTitle = R.id.widget_title;
      layouts.listView = R.id.list_view;
      layouts.background = R.id.background;
      
      layouts.date = R.id.date;
      layouts.title = R.id.title;
      layouts.author = R.id.author;
      layouts.text = R.id.text;
      layouts.image = R.id.image;
      
      return layouts;
      
    }
    
    @Override
    public Map<String, Object> setProviderItems (Map<String, Object> data) throws WidgetsManagerException {
      
      data.put (Const.ITEM_TITLE, "Stub");
      
      return data;
      
    }
    
    @Override
    public Map<String, Integer> setThemes (Map<String, Integer> themes) throws WidgetsManagerException {
      
      themes.put (Const.THEME_DEFAULT, R.layout.widget);
      
      return themes;
      
    }
    
    @Override
    public Map<String, Integer> setListThemes (Map<String, Integer> themes) throws WidgetsManagerException {
      return themes;
    }
    
    @Override
    public List<ListProvider.ListItem> getItems () throws WidgetsManagerException {
      return new ArrayList<> ();
    }
    
    @Override
    public void onProviderSelect (View view, String provider, int widgetId) throws WidgetsManagerException {}
    
    @Override
    public RemoteViews setWidgetRemoteView (RemoteViews remoteView) {
      return remoteView;
    }
    
    @Override
    public RemoteViews setListRemoteView (RemoteViews remoteView, Intent intent) {
      return remoteView;
    }
    
    @Override
    public ItemsAdapter setPrefsAdapter (ItemsAdapter.Layouts layouts, List<ListItem> items) {
      return new ItemsAdapter (layouts, R.layout.list_settings, items);
    }
    
  }