	package pro.acuna.widgetsmanager;
	/*
	 Created by Acuna on 11.07.2018
	*/
	
	import android.content.Intent;
	import android.widget.RemoteViews;
	
	import java.util.List;
	import java.util.Map;
	
	import pro.acuna.androdesign.adapter.ItemsAdapter;
	import pro.acuna.androdesign.widget.ListItem;
	import pro.acuna.andromeda.OS;
	
	public abstract class Provider {
		
		public abstract Provider getInstance (WidgetsManager widget) throws WidgetsManagerException;
		public abstract List<ListProvider.ListItem> getItems () throws WidgetsManagerException;
		public abstract WidgetsManager.Layouts getLayouts ();
		public abstract Map<String, Object> setProviderItems (Map<String, Object> data) throws WidgetsManagerException;
		public abstract Map<String, Integer> setThemes (Map<String, Integer> themes) throws WidgetsManagerException;
		public abstract Map<String, Integer> setListThemes (Map<String, Integer> themes) throws WidgetsManagerException;
		public abstract void onProviderSelect (String provider, int widgetId) throws WidgetsManagerException;
		public abstract ItemsAdapter setPrefsAdapter (ItemsAdapter.Layouts layouts, List<ListItem> items) throws WidgetsManagerException;
		public abstract OS.Service setService (Intent intent) throws WidgetsManagerException;
    
    public Map<String, Object> setDefPrefs (Map<String, Object> prefs) throws WidgetsManagerException {
      return prefs;
    }
    
    public RemoteViews setWidgetRemoteView (RemoteViews remoteView) throws WidgetsManagerException {
      return remoteView;
    }
    
    public RemoteViews setListRemoteView (RemoteViews remoteView, Intent intent) throws WidgetsManagerException {
      return remoteView;
    }
    
    public Intent onItemClick () throws WidgetsManagerException {
      return null;
    }
    
  }