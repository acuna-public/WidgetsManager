  package ru.ointeractive.widgetsmanager;
  /*
   Created by Acuna on 11.07.2018
  */
  
  import android.app.Activity;
  import android.app.AlertDialog;
  import android.app.PendingIntent;
  import android.appwidget.AppWidgetManager;
  import android.appwidget.AppWidgetProvider;
  import android.appwidget.AppWidgetProviderInfo;
  import android.content.ComponentName;
  import android.content.Context;
  import android.content.Intent;
  import android.graphics.Typeface;
  import android.net.Uri;
  import android.os.Bundle;
  import android.text.SpannableString;
  import android.view.View;
  import android.widget.RemoteViews;
  
  import java.util.ArrayList;
  import java.util.HashMap;
  import java.util.LinkedHashMap;
  import java.util.List;
  import java.util.Map;
  import java.util.Random;

  import ru.ointeractive.androdesign.UI;
  import ru.ointeractive.androdesign.adapter.ItemsAdapter;
  import ru.ointeractive.androdesign.widget.ListItem;
  import ru.ointeractive.andromeda.AlarmManager;
  import ru.ointeractive.andromeda.graphic.Color;
  import ru.ointeractive.andromeda.OS;
  import ru.ointeractive.andromeda.Prefs;
  import ru.ointeractive.andromeda.Strings;
  import ru.ointeractive.andromeda.System;
  import upl.core.Date;
  import upl.core.Int;
  
  public class WidgetsManager {
    
    public Context context;
    public Prefs prefs;
    public int widgetId = 0;
	  public int dialogStyle = 0;
	  public List<Adapter> providers = new ArrayList<> ();
	  public Adapter provider;
	  public Class<?> widgetProvider;
	  public AppWidgetManager manager;
	  public Map<String, Object> providerItems = new HashMap<> ();
    
    public WidgetsManager (Context context) {
    	
      this.context = context;
	    manager = AppWidgetManager.getInstance (context);
	    
    }
    
    protected Map<String, Object> prefItems = new HashMap<> ();
    
    public WidgetsManager loadPrefs (int id) throws WidgetsManagerException {
      return loadPrefs (id, "");
    }
    
    public WidgetsManager loadPrefs (int id, String name) throws WidgetsManagerException {
      
      widgetId = id;
	    
	    prefs = new Prefs (context, "widget-" + widgetId);
      
      prefs.setDefPref (Const.PREF_PLUGIN, "Default");
      prefs.setDefPref (Const.PREF_THEME, "Default");
      prefs.setDefPref (Const.PREF_LIST_THEME, "Default");
      
      provider = getProvider (!name.equals ("") ? name : prefs.getString (Const.PREF_PLUGIN));
      
      if (provider != null) {
        
        provider = provider.getInstance (this);
        providerItems = getProviderItems (provider);
        
      }
      
      return this;
      
    }
    
    public WidgetsManager setDialogStyle (int style) {
      
      dialogStyle = style;
      return this;
      
    }
    
    protected Map<String, Object> getProviderItems (Adapter provider) throws WidgetsManagerException {
      return provider.setProviderItems (providerItems);
    }
    
    public static class Layouts {
      
      public int widgetTitle, listView, emptyView, background;
      public int date, title, author, text, image;
      
    }
    
    public AppWidgetProviderInfo widgetInfo;
    
    protected Adapter getProvider (String name) throws WidgetsManagerException {
      
      widgetInfo = manager.getAppWidgetInfo (widgetId);
      
      prefs.setDefPref (Const.PREF_BACKGROUND_COLOR, "#000000");
      prefs.setDefPref (Const.PREF_TRANSPARENCY, 100);
      prefs.setDefPref (Const.PREF_ROUND_CORNERS, true);
      prefs.setDefPref (Const.PREF_CORNER_RADIUS, 10);
      prefs.setDefPref (Const.PREF_PREV_CORNER_RADIUS, prefs.defPrefs.get (Const.PREF_CORNER_RADIUS));
      //prefs.setDefPref (Const.PREF_UPDATE_TIME, 86400); // Секунд
      
      prefs.setDefPref (Const.PREF_TITLE_TEXT, "");
      
      prefs.setDefPref (Const.PREF_TITLE_SIZE, 16);
      prefs.setDefPref (Const.PREF_TITLE_COLOR, "#ffffff");
      prefs.setDefPref (Const.PREF_TITLE_STYLE, Typeface.NORMAL);
      prefs.setDefPref (Const.PREF_TITLE_FONT_FAMILY, "");
      
      prefs.setDefPref (Const.PREF_LIST_HIDE_DATE, false);
      
      prefs.setDefPref (Const.PREF_LIST_DATE_SIZE, 15);
      prefs.setDefPref (Const.PREF_LIST_DATE_COLOR, "#ffffff");
      prefs.setDefPref (Const.PREF_LIST_DATE_STYLE, Typeface.BOLD);
      prefs.setDefPref (Const.PREF_LIST_DATE_FONT_FAMILY, "");
      
      prefs.setDefPref (Const.PREF_LIST_TITLE_SIZE, 18);
      prefs.setDefPref (Const.PREF_LIST_TITLE_COLOR, "#ffffff");
      prefs.setDefPref (Const.PREF_LIST_TITLE_STYLE, Typeface.NORMAL);
      prefs.setDefPref (Const.PREF_LIST_TITLE_FONT_FAMILY, "");
      
      prefs.setDefPref (Const.PREF_LIST_AUTHOR_SIZE, 14);
      prefs.setDefPref (Const.PREF_LIST_AUTHOR_COLOR, "#ffffff");
      prefs.setDefPref (Const.PREF_LIST_AUTHOR_STYLE, Typeface.NORMAL);
      prefs.setDefPref (Const.PREF_LIST_AUTHOR_FONT_FAMILY, "");
      
      prefs.setDefPref (Const.PREF_LIST_TEXT_SIZE, 17);
      prefs.setDefPref (Const.PREF_LIST_TEXT_COLOR, "#ffffff");
      prefs.setDefPref (Const.PREF_LIST_TEXT_STYLE, Typeface.NORMAL);
      prefs.setDefPref (Const.PREF_LIST_TEXT_FONT_FAMILY, "");
      
      for (Adapter provider : providers) {
        
        provider = provider.getInstance (this);
        
        prefs.defPrefs = provider.getDefPrefs (prefs.defPrefs);
	      
        if (prefs.defPrefs.get (Const.PREF_PLUGIN).equals (name)) {
          
          prefs.setDefPref (Const.PREF_ACTION, "build");
          
          if (widgetInfo != null) {
            
            prefs.setDefPref (Const.PREF_WIDTH, (widgetInfo.minWidth * getWidthColsNum ()));
            prefs.setDefPref (Const.PREF_HEIGHT, (widgetInfo.minHeight * getHeightColsNum ()));
            
          }
          
          return provider;
          
        }
        
      }
      
      return providers.get (0);
      
    }
    
    public WidgetsManager addProvider (Adapter provider) {
      
      providers.add (provider);
      return this;
      
    }
    
    public ListProvider getList () throws WidgetsManagerException {
      return new ListProvider (this);
    }
    
    protected Class<?> service;
    
    public WidgetsManager setService (Class<?> service) {
      
      this.service = service;
      return this;
      
    }
    
    protected void updateWidget (RemoteViews remoteView) {
      
      Layouts item = provider.getLayouts ();
      
      Intent intent = new Intent (context, service);
      
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
      //intent.setData (Uri.parse (intent.toUri (Intent.URI_INTENT_SCHEME)));
	    intent.setType (String.valueOf (new Random ().nextInt (1000)));
	    
      remoteView.setRemoteAdapter (item.listView, intent);
      
      manager.notifyAppWidgetViewDataChanged (widgetId, item.listView);
      
    }
    
    public RemoteViews buildWidget (RemoteViews remoteView) {
      
      Layouts item = provider.getLayouts ();
      
      try {
        
        SpannableString text = new SpannableString (prefs.getString (Const.PREF_TITLE_TEXT).replace ("%d", new Date ().toString (1)));
        
        int edgeDef = 10;
        //int edge = Int.prop (prefs.getInt (PREF_CORNER_RADIUS), prefs.getInt (PREF_PREV_CORNER_RADIUS), edgeDef);
        //edge = Int.correct (edge, edgeDef, 10);
        
        if (Int.size (text) > 0 && item.widgetTitle != 0) { // Заголовок виджета
          
          remoteView.setViewVisibility (item.widgetTitle, View.VISIBLE);
          
          remoteView.setFloat (item.widgetTitle, "setTextSize", prefs.getInt (Const.PREF_TITLE_SIZE));
          remoteView.setInt (item.widgetTitle, "setTextColor", prefs.getColor (Const.PREF_TITLE_COLOR));
          
          Strings.setFontTypeface (text, prefs.getInt (Const.PREF_TITLE_STYLE));
          Strings.setFontFamily (text, prefs.getString (Const.PREF_TITLE_FONT_FAMILY));
          
          remoteView.setTextViewText (item.widgetTitle, text);
          
          remoteView.setViewPadding (item.widgetTitle, edgeDef, 0, edgeDef, 0);
          
        }
        
        remoteView = provider.setWidgetRemoteView (remoteView);
        
        PendingIntent pIntent = PendingIntent.getActivity (context, 0, provider.onItemClick (), PendingIntent.FLAG_UPDATE_CURRENT);
	      
        remoteView.setPendingIntentTemplate (item.listView, pIntent);
        
        remoteView.setViewPadding (item.listView, edgeDef, 10, edgeDef, 10);
        remoteView.setEmptyView (item.listView, item.emptyView);
        
        remoteView.setInt (item.background, "setColorFilter", prefs.getColor (Const.PREF_BACKGROUND_COLOR));
        remoteView.setInt (item.background, "setImageAlpha", prefs.getInt (Const.PREF_TRANSPARENCY));
        remoteView.setInt (item.background, "setColorFilter", prefs.getColor (Const.PREF_BACKGROUND_COLOR));
        
        //float radius = prefs.getInt (Const.PREF_CORNER_RADIUS);
        
        //Shape shape = new RoundRectShape (new float[] {radius, radius, radius, radius, radius, radius, radius, radius}, null, null);
        
        if (prefs.getBool (Const.PREF_ROUND_CORNERS))
          remoteView.setImageViewResource (item.background, R.drawable.rounded);
        else
          remoteView.setImageViewResource (item.background, R.drawable.normal);
        
      } catch (WidgetsManagerException e) {
        
        remoteView.setViewVisibility (item.widgetTitle, View.VISIBLE);
        remoteView.setTextViewText (item.widgetTitle, e.getMessage ());
        
      }
      
	    updateWidget (remoteView);
     
	    return remoteView;
     
    }
    
    public WidgetsManager setWidgetProvider (Class<?> provider) {
      
      widgetProvider = provider;
      return this;
      
    }
    
    protected void getProvider () throws WidgetsManagerException {
      
      if (provider == null) provider = providers.get (0);
      provider = provider.getInstance (this);
      
      Map<String, Object> items = getDefPrefs ();
      
      for (String key : items.keySet ())
        prefItems.put (key, items.get (key));
      
    }
    
    public ItemsAdapter providersAdapter (final Activity activity, final int widgetId) throws WidgetsManagerException {
      
      final List<ListItem> items = new ArrayList<> ();
      
      for (int i = 0; i < Int.size (providers); ++i) {
        
        provider = providers.get (i);
        provider = provider.getInstance (this);
        
        try {
          
          Map<String, Object> pItems = getProviderItems (provider), pPrefs = getDefPrefs ();
          
          items.add (new ListItem ().setArgv (pItems.get (Const.ITEM_TITLE).toString (), "", pPrefs.get (Const.PREF_PLUGIN).toString ()));
          
        } catch (WidgetsManagerException e) {
          items.add (new ListItem ().setArgv (e.getMessage ()));
        }
        
      }
      
      ItemsAdapter.Layouts layouts = new ItemsAdapter.Layouts ();
      
      layouts.title = R.id.title;
      layouts.descr = R.id.descr;
      
      ItemsAdapter adapter = new ItemsAdapter (layouts, R.layout.andro_list_main, items);
      
      adapter.addListener (new ItemsAdapter.TextListener () {
        
        @Override
        public void onClick (View view, ItemsAdapter.ViewHolder holder, ListItem item) { // Выбор провайдера виджета
          
          try {
            
            loadPrefs (widgetId, item.getArgv (2));
            getProvider ();
            
            startWidgetUpdate ();
            
            buildWidget (activity);
            activity.finish ();
            
            provider.onProviderSelect (view, item.getArgv (2), widgetId);
            
          } catch (WidgetsManagerException e) {
            OS.alert (context, System.error (context, e));
          }
          
        }
        
        @Override
        public void onLongClick (View view, ItemsAdapter.ViewHolder holder, ListItem item) {}
        
      });
      
      return adapter;
      
    }
    
    protected Map<String, Object> getDefPrefs () throws WidgetsManagerException {
      return provider.getDefPrefs (new HashMap<> ());
    }
    
    public void startWidgetUpdate () {
	    
      if (provider != null) { // Виджет может быть не установлен, но служба запущена
	      
        Intent intent = new Intent (context, widgetProvider);
        
        intent.setAction (AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        
        Uri.Builder build = new Uri.Builder ();
        build.appendPath ("" + widgetId);
        
        intent.setData (build.build ());
        
        intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId); // TODO widgetId is null
        
        provider.setService (intent);
        
      }
      
    }
    
    public ItemsAdapter getPrefsAdapter () throws WidgetsManagerException {
      
      ItemsAdapter adapter = null;
      
      if (prefs != null) {
        
        if (provider == null) {
          
          getProvider ();
          saveSettings ();
          
          startWidgetUpdate ();
          
          loadPrefs (widgetId);
          
        }
        
        ItemsAdapter.Layouts layouts = new ItemsAdapter.Layouts ();
        
        layouts.title = R.id.title;
        layouts.descr = R.id.descr;
        layouts.checkbox = R.id.checkbox;
        layouts.seekbar = R.id.seekbar;
        
        adapter = provider.setPrefsAdapter (layouts, provider.getPrefsItems (new ArrayList<> ()));
        
        if (adapter != null)
        adapter.addListener (new ItemsAdapter.SeekbarItemListener () {
          
          @Override
          public void onShow (ItemsAdapter.ViewHolder holder, ListItem item) {
            if (item.checked) prefItems.put (item.getArgv (2), item.checked);
          }
          
          @Override
          public void onItemCheck (ListItem item) {
            prefItems.put (item.getArgv (2), true);
          }
          
          @Override
          public void onItemUncheck (ListItem item) {
            prefItems.put (item.getArgv (2), false);
          }
          
          @Override
          public void onClick (View view, final ItemsAdapter.ViewHolder holder, final ListItem item) {
            
            switch (item.getArgv (ItemsAdapter.NAME)) {
              
              case Const.PREF_BACKGROUND_COLOR:
              case Const.PREF_LIST_TEXT_COLOR: {
                
                UI.colorPickerDialog ((Activity) context, dialogStyle, R.string.title_color_picker, prefs.getColor (item.getArgv (ItemsAdapter.NAME)), new UI.ColorPickerDialogInterface () {
                  
                  @Override
                  public void onSubmit (AlertDialog dialog, int color) {
                    
                    prefItems.put (item.getArgv (ItemsAdapter.NAME), Color.toString (color));
                    holder.descr.setText (Color.toString (color));
                    
                  }
                  
                  @Override
                  public void onDismiss (AlertDialog dialog, int color) {}
                  
                });
                
                break;
                
              }
              
              /*case Const.PREF_UPDATE_TIME: {
                
                UI.dialog ((Activity) context, dialogStyle, R.string.title_input, R.layout.dialog_input_descr, new UI.DialogViewNegInterface () {
                  
                  @Override
                  public View onView (AlertDialog.NumberPicker builder, View dialogView) {
                    
                    TextView text = dialogView.findViewById (R.id.descr);
                    text.setText (R.string.settings_widget_update_descr);
                    
                    EditText text2 = dialogView.findViewById (R.id.text1);
                    text2.setText (String.valueOf ((prefItems.get (item.getArgv (2)) != null ? prefItems.get (item.getArgv (2)) : prefs.getInt (Const.PREF_UPDATE_TIME))));
                    
                    return dialogView;
                    
                  }
                  
                  @Override
                  public void onPositiveClick (AlertDialog dialog, View dialogView) {
                    
                    dialog.dismiss ();
                    
                    EditText text = dialogView.findViewById (R.id.text1);
                    String prevText = text.getText ().toString ();
                    
                    int num = Integer.parseInt (prevText);
                    
                    prefItems.put (item.getArgv (2), num);
                    holder.descr.setText (updateDescr (num));
                    
                  }
                  
                  @Override
                  public void onNegativeClick (AlertDialog dialog, View view) {
                    dialog.dismiss ();
                  }
                  
                }, android.R.string.ok, android.R.string.cancel);
                
                break;
                
              }*/
              
            }
            
          }
          
          @Override
          public void onLongClick (View view, ItemsAdapter.ViewHolder holder, final ListItem item) {}
          
          @Override
          public void onStartTrackingTouch (ItemsAdapter.ViewHolder holder, ListItem item) {}
          
          @Override
          public void onProgressChanged (ItemsAdapter.ViewHolder holder, ListItem item, int progress) {
            
            holder.seekbarValue.setText (String.valueOf (progress));
            prefItems.put (item.getArgv (2), progress);
	          
          }
          
          @Override
          public void onStopTrackingTouch (ItemsAdapter.ViewHolder holder, ListItem item) {}
          
        });
        
      }
      
      return adapter;
      
    }
    
    protected String updateDescr (int num) {
      
      String output;
      
      if (num == 0)
        output = context.getString (R.string.interval_never);
      else if (num == 1)
        output = context.getString (R.string.interval_minute_0);
      else
        output = new upl.type.String (num).suffix (new String[] {
          
          context.getString (R.string.interval_minute_1),
          context.getString (R.string.interval_minute_2),
          context.getString (R.string.interval_minute_3),
          
          }).replace ("%num%", String.valueOf (num));
      
      return output;
      
    }
    
    public Layouts layouts;
    
    public WidgetsManager setWidgetLayouts (Layouts layouts) {
      
      this.layouts = layouts;
      return this;
      
    }
    
    protected void buildWidget (Activity activity) throws WidgetsManagerException {
      
      //prefItems.put (Const.PREF_PREV_CORNER_RADIUS, prefs.getInt (Const.PREF_CORNER_RADIUS));
      
      String[] names;
      
      if (prefItems.get (Const.PREF_LIST_TEXT_FONT_FAMILY) != null) {
        
        names = new String[] {Const.PREF_LIST_DATE_FONT_FAMILY, Const.PREF_LIST_TITLE_FONT_FAMILY, Const.PREF_LIST_AUTHOR_FONT_FAMILY};
        
        for (String name : names)
          prefItems.put (name, prefItems.get (Const.PREF_LIST_TEXT_FONT_FAMILY));
        
      }
      
      if (prefItems.get (Const.PREF_LIST_TEXT_COLOR) != null) {
        
        names = new String[] {Const.PREF_LIST_DATE_COLOR, Const.PREF_LIST_TITLE_COLOR, Const.PREF_LIST_AUTHOR_COLOR};
        
        for (String name : names)
          prefItems.put (name, prefItems.get (Const.PREF_LIST_TEXT_COLOR));
        
      }
      
      saveSettings ();
      buildWidget (activity, widgetId);
      
    }
    
    protected void buildWidget (Activity activity, int widgetId) {
      
      Intent intent = new Intent (activity, widgetProvider);
      
      intent.setAction (AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {widgetId});
      
      activity.sendBroadcast (intent);
      
      Intent resultValue = new Intent ();
      
      resultValue.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
      
      activity.setResult (Activity.RESULT_OK, resultValue);
      
    }
        
        /*protected Object getPref (String key) {
          
          if (prefItems.get (key) == null)
            prefItems.put (key, defPrefs.get (key));
          
          return prefItems.get (key);
          
        }*/
    
    public WidgetsManager editWidget (Activity activity) throws WidgetsManagerException {
      
      /*Object oldSize = prefItems.get (Const.PREF_LIST_TEXT_SIZE);
      if (oldSize == null) oldSize = prefs.getInt (Const.PREF_LIST_TEXT_SIZE);
      
      String[] names = new String[] {Const.PREF_LIST_DATE_SIZE, Const.PREF_LIST_TITLE_SIZE, Const.PREF_LIST_AUTHOR_SIZE};
      
      for (String name : names) {
        
        if (oldSize.equals (prefs.defPrefs.get (Const.PREF_LIST_TEXT_SIZE)))
          prefItems.put (name, prefs.defPrefs.get (name));
        else
          prefItems.put (name, Int.floor (Int.prop (((Integer) oldSize).doubleValue (), ((Integer) prefs.getInt (Const.PREF_LIST_TEXT_SIZE)).doubleValue (), ((Integer) prefs.getInt (name)).doubleValue ())));
        
      }*/
      
      Object isSerif = prefItems.get (Const.PREF_LIST_TEXT_FONT_FAMILY);
      
      if (isSerif != null && !(isSerif instanceof String)) {
        
        if (isSerif instanceof Boolean && (boolean) isSerif)
          prefItems.put (Const.PREF_LIST_TEXT_FONT_FAMILY, "serif");
        else
          prefItems.put (Const.PREF_LIST_TEXT_FONT_FAMILY, "");
        
      }
      
      buildWidget (activity);
      activity.finish ();
      
      prefItems.put (Const.PREF_ACTION, "update");
      
      saveSettings ();
      
      return this;
      
    }
    
    protected void saveSettings () {
      
      for (String key : prefItems.keySet ())
        prefs.set (key, prefItems.get (key));
      
      prefs.apply ();
      
    }
    
    public void updateWidget () throws WidgetsManagerException {
	    
      if (provider != null) {
	      
        RemoteViews remoteView = new RemoteViews (context.getPackageName (), getTheme ());
        
        Layouts item = provider.getLayouts ();
        
        try {
	        manager.updateAppWidget (widgetId, buildWidget (remoteView));
        } catch (IllegalArgumentException e) {
          remoteView.setTextViewText (item.widgetTitle, e.getMessage ());
        }
        
      }
      
    }
    
    public int getTheme () throws WidgetsManagerException {
      return provider.setThemes (new LinkedHashMap<> ()).get (prefs.getString (Const.PREF_THEME));
    }
    
    public int getListTheme () throws WidgetsManagerException {
      return provider.setListThemes (new LinkedHashMap<> ()).get (prefs.getString (Const.PREF_LIST_THEME));
    }
    
    protected int getColsNum (int size) {
      return Int.floor ((size - 30) / 70);
    }
    
    public int getWidthColsNum () {
      return getColsNum (widgetInfo.minWidth);
    }
    
    public int getHeightColsNum () {
      return getColsNum (widgetInfo.minHeight);
    }
    
    public void resizeWidget (int appWidgetId, Bundle widgetInfo) throws WidgetsManagerException {
          
          /*prefItems.put (Const.PREF_WIDTH, widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH));
          prefItems.put (Const.PREF_HEIGHT, widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT));
          
          prefItems.put (Const.PREF_ACTION, "build");
          
          saveSettings ();
          updateWidget (appWidgetId);
          
          prefItems.put (Const.PREF_ACTION, "update");
          
          saveSettings ();*/
      
    }
    
    public void onDeleted (int id) throws WidgetsManagerException {
      
      Intent intent = new Intent (context, widgetProvider);
      
      intent.setAction (AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, id);
      
      new AlarmManager (context)
        .setIntent (intent, id)
        .stop ();
      
    }
    
    public int[] getWidgetsIds () {
      return manager.getAppWidgetIds (new ComponentName (context, widgetProvider));
    }
    
    public boolean onReceive (Intent intent, AppWidgetProvider provider) throws WidgetsManagerException {
    	
	    if (intent.getAction ().equals (AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
	    	
		    int widgetId = intent.getIntExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		    
		    if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
					
			    //loadPrefs (widgetId).updateWidget ();
					
			    provider.onUpdate (context, manager, new int[] {widgetId});
			    
			    return true;
			    
		    }
		    
	    }
	    
	    return false;
	    
    }
    
    public void onUpdate (int[] appWidgetIds) throws WidgetsManagerException {
    	
	    for (int id : appWidgetIds)
	    	loadPrefs (id).updateWidget ();
	    
    }
    
	  public void onEnabled () throws WidgetsManagerException {
		  onUpdate (getWidgetsIds ());
	  }
	  
	  public void onAppWidgetOptionsChanged (int appWidgetId, Bundle bundle) throws WidgetsManagerException {
     
    	loadPrefs (appWidgetId);
		  
      /*RemoteViews remoteView = new RemoteViews (context.getPackageName (), manager.getListTheme ());
      
      if (bundle.getInt (AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110)
        remoteView.setViewVisibility (R.id.text, View.GONE);
      else
        remoteView.setViewVisibility (R.id.text, View.VISIBLE);
      
      appWidgetManager.updateAppWidget (appWidgetId, manager.buildWidget (remoteView, appWidgetManager));*/
		  
		  resizeWidget (appWidgetId, bundle);
		  
	  }
	  
	  public void onDeleted (int[] appWidgetIds) throws WidgetsManagerException {
    
		  for (int id : appWidgetIds)
			  onDeleted (id);
		  
	  }
	  
	  public void startWidgetUpdate (Intent intent) throws WidgetsManagerException {
    
		  for (int id : getWidgetsIds ())
		  	loadPrefs (id).startWidgetUpdate ();
		  
	  }
	  
  }