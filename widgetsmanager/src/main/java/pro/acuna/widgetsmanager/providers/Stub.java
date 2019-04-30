  package pro.acuna.widgetsmanager.providers;
  /*
   Created by Acuna on 11.07.2018
  */
  
  import android.content.Intent;
  import android.widget.RemoteViews;
  
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Map;
  
  import pro.acuna.androdesign.adapter.ItemsAdapter;
  import pro.acuna.androdesign.widget.ListItem;
  import pro.acuna.andromeda.OS;
  import pro.acuna.widgetsmanager.Const;
  import pro.acuna.widgetsmanager.ListProvider;
  import pro.acuna.widgetsmanager.Provider;
  import pro.acuna.widgetsmanager.R;
  import pro.acuna.widgetsmanager.WidgetsManager;
  import pro.acuna.widgetsmanager.WidgetsManagerException;
  
  public class Stub extends Provider {
    
    public Stub () {}
    
    private WidgetsManager manager;
    
    private Stub (WidgetsManager manager) {
      this.manager = manager;
    }
    
    @Override
    public Provider getInstance (WidgetsManager widget) {
      return new Stub (widget);
    }
    
    @Override
    public WidgetsManager.Layouts getLayouts () {
      
      WidgetsManager.Layouts layouts = new WidgetsManager.Layouts ();
      
      layouts.widgetTitle = R.id.widget_title;
      layouts.listView = R.id.list_view;
      layouts.emptyView = R.id.empty_view;
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
    public void onProviderSelect (String provider, int widgetId) throws WidgetsManagerException {}
    
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
    
    @Override
    public OS.Service setService (Intent intent) throws WidgetsManagerException {
      return null;
    }
    
  }