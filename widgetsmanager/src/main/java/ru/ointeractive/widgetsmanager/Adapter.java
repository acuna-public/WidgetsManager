	package ru.ointeractive.widgetsmanager;
	/*
	 Created by Acuna on 11.07.2018
	*/
	
  import android.content.Context;
  import android.content.Intent;
  import android.view.View;
  import android.widget.RemoteViews;
  
  import java.util.List;
	import java.util.Map;
	
	import ru.ointeractive.androdesign.adapter.ItemsAdapter;
	import ru.ointeractive.androdesign.widget.ListItem;
  import ru.ointeractive.andromeda.AlarmManager;
  import ru.ointeractive.andromeda.graphic.Color;
  import upl.core.Time;
	
	public abstract class Adapter {
   
	  protected Context context;
    protected WidgetsManager manager;
	  
	  protected Adapter (Context context, WidgetsManager manager) {
	  	
	    this.context = context;
	    this.manager = manager;
	    
    }
	  
		public abstract Adapter getInstance (WidgetsManager manager) throws WidgetsManagerException;
		public abstract List<ListProvider.ListItem> getItems () throws WidgetsManagerException;
		public abstract WidgetsManager.Layouts getLayouts ();
		public abstract Map<String, Object> setProviderItems (Map<String, Object> data) throws WidgetsManagerException;
		public abstract Map<String, Integer> setThemes (Map<String, Integer> themes) throws WidgetsManagerException;
		public abstract Map<String, Integer> setListThemes (Map<String, Integer> themes) throws WidgetsManagerException;
		public abstract void onProviderSelect (View view, String provider, int widgetId) throws WidgetsManagerException;
		public abstract ItemsAdapter setPrefsAdapter (ItemsAdapter.Layouts layouts, List<ListItem> items) throws WidgetsManagerException;
		
    public Map<String, Object> getDefPrefs (Map<String, Object> prefs) throws WidgetsManagerException {
      return prefs;
    }
    
    public final List<ListItem> getPrefsItems (List<ListItem> items) throws WidgetsManagerException {
      
      //items.add (new ListItem ().setArgv (Graphics.toString (this, R.string.settings_widget_provider), dt.widget.providerItems.getString (ITEM_TITLE), PREF_PLUGIN));
      
      items.add (new ListItem ().setArgv (context.getString (R.string.settings_widget_bg_color), Color.toString (manager.prefs.getColor (Const.PREF_BACKGROUND_COLOR)), Const.PREF_BACKGROUND_COLOR));
      
      items.add (new ListItem ().setArgv (context.getString (R.string.settings_transparency), "", Const.PREF_TRANSPARENCY).setSeekbar (manager.prefs.getInt (Const.PREF_TRANSPARENCY)).setSeekbarMaxValue (255));
      
      //items.add (new ListItem ().setArgv (context.getString (R.string.settings_corners_radius), "", Const.PREF_CORNER_RADIUS).setSeekbar (manager.prefs.getInt (Const.PREF_CORNER_RADIUS)));
      
      items.add (new ListItem ().setCheckbox (true).setChecked (manager.prefs.getBool (Const.PREF_ROUND_CORNERS)).setArgv (context.getString (R.string.settings_corners_radius), "", Const.PREF_ROUND_CORNERS));
      
      //items.add (new ListItem ().setArgv (context.getString (R.string.settings_widget_update), updateDescr (manager.prefs.getInt (Const.PREF_UPDATE_TIME)), Const.PREF_UPDATE_TIME));
			
	    items.add (new ListItem ().setArgv (context.getString (R.string.settings_date_font_size), "", Const.PREF_LIST_DATE_SIZE).setSeekbar (manager.prefs.getInt (Const.PREF_LIST_DATE_SIZE)).setSeekbarMinValue (10).setSeekbarMaxValue (50));
			
	    items.add (new ListItem ().setArgv (context.getString (R.string.settings_title_font_size), "", Const.PREF_LIST_TITLE_SIZE).setSeekbar (manager.prefs.getInt (Const.PREF_LIST_TITLE_SIZE)).setSeekbarMinValue (10).setSeekbarMaxValue (50));
			
	    items.add (new ListItem ().setArgv (context.getString (R.string.settings_font_size), "", Const.PREF_LIST_TEXT_SIZE).setSeekbar (manager.prefs.getInt (Const.PREF_LIST_TEXT_SIZE)).setSeekbarMinValue (10).setSeekbarMaxValue (50));
      
      items.add (new ListItem ().setCheckbox (true).setChecked (manager.prefs.getString (Const.PREF_LIST_TEXT_FONT_FAMILY).equals ("serif")).setArgv (context.getString (R.string.settings_font_serif), "", Const.PREF_LIST_TEXT_FONT_FAMILY));
      
      items.add (new ListItem ().setArgv (context.getString (R.string.settings_font_color), Color.toString (manager.prefs.getColor (Const.PREF_LIST_TEXT_COLOR)), Const.PREF_LIST_TEXT_COLOR));
      
      return setPrefsItems (items);
      
    }
    
    public List<ListItem> setPrefsItems (List<ListItem> items) throws WidgetsManagerException {
    	return items;
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
		
		public void setService (Intent intent) {
			
			new AlarmManager (context)
				.setIntent (intent)
				.start (new Time ().startDayTime (), android.app.AlarmManager.INTERVAL_DAY);
			
		}
		
	}