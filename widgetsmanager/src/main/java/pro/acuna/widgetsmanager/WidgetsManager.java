  package pro.acuna.widgetsmanager;
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
  import android.widget.EditText;
  import android.widget.RemoteViews;
  import android.widget.TextView;
  
  import java.util.ArrayList;
  import java.util.HashMap;
  import java.util.LinkedHashMap;
  import java.util.List;
  import java.util.Map;
  
  import pro.acuna.androdesign.UI;
  import pro.acuna.androdesign.adapter.ItemsAdapter;
  import pro.acuna.androdesign.widget.ListItem;
  import pro.acuna.andromeda.AppsManager;
  import pro.acuna.andromeda.Color;
  import pro.acuna.andromeda.OS;
  import pro.acuna.andromeda.Prefs;
  import pro.acuna.andromeda.Strings;
  import pro.acuna.andromeda.System;
  import pro.acuna.jabadaba.Int;
  import pro.acuna.jabadaba.Locales;
  
  public class WidgetsManager {
    
    public Context context;
    public Prefs prefs;
    int widgetId;
    private int dialogStyle = 0;
    private List<Provider> providers = new ArrayList<> ();
    Provider provider;
    private Class<?> widgetProvider;
    private Map<String, Object> providerItems = new HashMap<> ();
    
    public WidgetsManager (Context context) {
      this.context = context;
    }
    
    private Map<String, Object> defPrefs = new HashMap<> (), prefItems = new HashMap<> ();
    
    public WidgetsManager loadPrefs (int id) throws WidgetsManagerException {
      return loadPrefs (id, "");
    }
    
    public WidgetsManager loadPrefs (int id, String name) throws WidgetsManagerException {
      
      widgetId = id;
      
      defPrefs.put (Const.PREF_PLUGIN, "");
      defPrefs.put (Const.PREF_THEME, "Default");
      defPrefs.put (Const.PREF_LIST_THEME, "Default");
      
      prefs = new Prefs (context, "widget-" + widgetId, defPrefs);
      
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
    
    private Map<String, Object> getProviderItems (Provider provider) throws WidgetsManagerException {
      return provider.setProviderItems (providerItems);
    }
    
    public static class Layouts {
      
      public int widgetTitle, listView, emptyView, background;
      public int date, title, author, text, image;
      
    }
    
    private AppWidgetProviderInfo widgetInfo;
    
    private Provider getProvider (String name) throws WidgetsManagerException {
      
      widgetInfo = appWidgetManager ().getAppWidgetInfo (widgetId);
      
      defPrefs.put (Const.PREF_BACKGROUND_COLOR, "#000000");
      defPrefs.put (Const.PREF_TRANSPARENCY, 100);
      defPrefs.put (Const.PREF_ROUND_CORNERS, true);
      defPrefs.put (Const.PREF_CORNER_RADIUS, 10);
      defPrefs.put (Const.PREF_PREV_CORNER_RADIUS, defPrefs.get (Const.PREF_CORNER_RADIUS));
      defPrefs.put (Const.PREF_UPDATE_TIME, 86400); // Секунд
      
      defPrefs.put (Const.PREF_TITLE_TEXT, "");
      
      defPrefs.put (Const.PREF_TITLE_SIZE, 15);
      defPrefs.put (Const.PREF_TITLE_COLOR, "#ffffff");
      defPrefs.put (Const.PREF_TITLE_STYLE, Typeface.NORMAL);
      defPrefs.put (Const.PREF_TITLE_FONT_FAMILY, "");
      
      defPrefs.put (Const.PREF_LIST_HIDE_DATE, false);
      
      defPrefs.put (Const.PREF_LIST_DATE_SIZE, 14);
      defPrefs.put (Const.PREF_LIST_DATE_COLOR, "#ffffff");
      defPrefs.put (Const.PREF_LIST_DATE_STYLE, Typeface.BOLD);
      defPrefs.put (Const.PREF_LIST_DATE_FONT_FAMILY, "");
      
      defPrefs.put (Const.PREF_LIST_TITLE_SIZE, 15);
      defPrefs.put (Const.PREF_LIST_TITLE_COLOR, "#ffffff");
      defPrefs.put (Const.PREF_LIST_TITLE_STYLE, Typeface.NORMAL);
      defPrefs.put (Const.PREF_LIST_TITLE_FONT_FAMILY, "");
      
      defPrefs.put (Const.PREF_LIST_AUTHOR_SIZE, 13);
      defPrefs.put (Const.PREF_LIST_AUTHOR_COLOR, "#ffffff");
      defPrefs.put (Const.PREF_LIST_AUTHOR_STYLE, Typeface.NORMAL);
      defPrefs.put (Const.PREF_LIST_AUTHOR_FONT_FAMILY, "");
      
      defPrefs.put (Const.PREF_LIST_TEXT_SIZE, 14);
      defPrefs.put (Const.PREF_LIST_TEXT_COLOR, "#ffffff");
      defPrefs.put (Const.PREF_LIST_TEXT_STYLE, Typeface.NORMAL);
      defPrefs.put (Const.PREF_LIST_TEXT_FONT_FAMILY, "");
      
      for (Provider provider : providers) {
        
        defPrefs = provider.setDefPrefs (defPrefs);
        
        if (defPrefs.get (Const.PREF_PLUGIN).equals (name)) {
          
          defPrefs.put (Const.PREF_ACTION, "build");
          
          if (widgetInfo != null) {
            
            defPrefs.put (Const.PREF_WIDTH, (widgetInfo.minWidth * getWidthColsNum ()));
            defPrefs.put (Const.PREF_HEIGHT, (widgetInfo.minHeight * getHeightColsNum ()));
            
          }
          
          prefs = prefs.setDefPrefs (provider.setDefPrefs (defPrefs));
          
          return provider;
          
        }
        
      }
      
      return null;
      
    }
    
    public WidgetsManager addProvider (Provider provider) {
      
      providers.add (provider);
      return this;
      
    }
    
    public ListProvider getList () throws WidgetsManagerException {
      return new ListProvider (this);
    }
    
    private Class<?> service;
    
    public WidgetsManager setService (Class<?> service) {
      
      this.service = service;
      return this;
      
    }
    
    private void updateWidget (RemoteViews remoteView, AppWidgetManager manager) {
      
      Layouts item = provider.getLayouts ();
      
      Intent intent = new Intent (context, service);
      
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
      intent.setData (Uri.parse (intent.toUri (Intent.URI_INTENT_SCHEME)));
      
      remoteView.setRemoteAdapter (item.listView, intent);
      
      manager.notifyAppWidgetViewDataChanged (widgetId, item.listView);
      
    }
    
    private RemoteViews buildWidget (RemoteViews remoteView, AppWidgetManager manager) {
      
      Layouts item = provider.getLayouts ();
      
      try {
        
        SpannableString text = new SpannableString (prefs.getString (Const.PREF_TITLE_TEXT).replace ("%d", Locales.date (1)));
        
        int edgeDef = 10, edge = edgeDef;
        //int edge = Int.prop (prefs.getInt (PREF_CORNER_RADIUS), prefs.getInt (PREF_PREV_CORNER_RADIUS), edgeDef);
        //edge = Int.correct (edge, edgeDef, 10);
        
        if (Int.size (text) > 0 && item.widgetTitle != 0) { // Заголовок виджета
          
          remoteView.setViewVisibility (item.widgetTitle, View.VISIBLE);
          
          remoteView.setFloat (item.widgetTitle, "setTextSize", prefs.getInt (Const.PREF_TITLE_SIZE));
          remoteView.setInt (item.widgetTitle, "setTextColor", prefs.getColor (Const.PREF_TITLE_COLOR));
          
          text = Strings.setFontTypeface (text, prefs.getInt (Const.PREF_TITLE_STYLE));
          text = Strings.setFontFamily (text, prefs.getString (Const.PREF_TITLE_FONT_FAMILY));
          
          remoteView.setTextViewText (item.widgetTitle, text);
          
          remoteView.setViewPadding (item.widgetTitle, edge, 0, edge, 0);
          
        }
        
        remoteView = provider.setWidgetRemoteView (remoteView);
        
        PendingIntent pIntent = PendingIntent.getActivity (context, 0, provider.onItemClick (), PendingIntent.FLAG_UPDATE_CURRENT);
        
        remoteView.setPendingIntentTemplate (item.listView, pIntent);
        
        updateWidget (remoteView, manager);
        
        remoteView.setViewPadding (item.listView, edge, 10, edge, 10);
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
      
      return remoteView;
      
    }
    
    public WidgetsManager setWidgetProvider (Class<?> provider) {
      
      this.widgetProvider = provider;
      return this;
      
    }
    
    private void getProvider () throws WidgetsManagerException {
      
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
        public void onClick (final View view, ItemsAdapter.ViewHolder holder, ListItem item) {
          
          try {
            
            loadPrefs (widgetId, item.getArgv (2));
            getProvider ();
            
            startWidgetUpdate ();
            
            buildWidget (activity);
            activity.finish ();
            
            provider.onProviderSelect (item.getArgv (2), widgetId);
            
          } catch (WidgetsManagerException e) {
            OS.alert (context, System.error (context, e));
          }
          
        }
        
        @Override
        public void onLongClick (View view, ItemsAdapter.ViewHolder holder, ListItem item) {}
        
      });
      
      return adapter;
      
    }
    
    private Map<String, Object> getDefPrefs () throws WidgetsManagerException {
      return provider.setDefPrefs (new HashMap<String, Object> ());
    }
    
    public void startWidgetUpdate () throws WidgetsManagerException {
      
      Intent intent = new Intent (context, widgetProvider);
      
      intent.setAction (AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      
      Uri.Builder build = new Uri.Builder ();
      build.appendPath ("" + widgetId);
      
      intent.setData (build.build ());
      
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
      
      if (provider.setService (intent) == null)
        new OS.Service (context, Locales.time () + (prefs.getInt (Const.PREF_UPDATE_TIME) * 1000))
          .setIntent (PendingIntent.getBroadcast (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
          .init ()
          .start ((prefs.getInt (Const.PREF_UPDATE_TIME) * 1000));
      
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
        
        List<ListItem> items = new ArrayList<> ();
        
        //items.add (new ListItem ().setArgv (Graphics.toString (this, R.string.settings_widget_provider), dt.widget.providerItems.getString (ITEM_TITLE), PREF_PLUGIN));
        
        items.add (new ListItem ().setArgv (context.getString (R.string.settings_widget_bg_color), Color.toString (prefs.getColor (Const.PREF_BACKGROUND_COLOR)), Const.PREF_BACKGROUND_COLOR));
        
        items.add (new ListItem ().setArgv (context.getString (R.string.settings_transparency), "", Const.PREF_TRANSPARENCY).setSeekbar (prefs.getInt (Const.PREF_TRANSPARENCY)).setSeekbarMaxValue (255));
        
        //items.add (new ListItem ().setArgv (context.getString (R.string.settings_corners_radius), "", Const.PREF_CORNER_RADIUS).setSeekbar (prefs.getInt (Const.PREF_CORNER_RADIUS)));
        
        items.add (new ListItem ().setChecked (prefs.getBool (Const.PREF_ROUND_CORNERS)).setArgv (context.getString (R.string.settings_corners_radius), "", Const.PREF_ROUND_CORNERS));
        
        //items.add (new ListItem ().setArgv (context.getString (R.string.settings_widget_update), updateDescr (prefs.getInt (Const.PREF_UPDATE_TIME)), Const.PREF_UPDATE_TIME));
        
        items.add (new ListItem ().setArgv (context.getString (R.string.settings_font_size), "", Const.PREF_LIST_TEXT_SIZE).setSeekbar (prefs.getInt (Const.PREF_LIST_TEXT_SIZE)).setSeekbarMinValue (10).setSeekbarMaxValue (25));
        
        items.add (new ListItem ().setChecked (prefs.getString (Const.PREF_LIST_TEXT_FONT_FAMILY).equals ("serif")).setArgv (context.getString (R.string.settings_font_serif), "", Const.PREF_LIST_TEXT_FONT_FAMILY));
        
        items.add (new ListItem ().setArgv (context.getString (R.string.settings_font_color), Color.toString (prefs.getColor (Const.PREF_LIST_TEXT_COLOR)), Const.PREF_LIST_TEXT_COLOR));
        
        items.add (new ListItem ().setChecked (prefs.getBool (Const.PREF_LIST_HIDE_DATE)).setArgv (context.getString (R.string.settings_hide_date), "", Const.PREF_LIST_HIDE_DATE));
        
        ItemsAdapter.Layouts layouts = new ItemsAdapter.Layouts ();
        
        layouts.title = R.id.title;
        layouts.descr = R.id.descr;
        layouts.checkbox = R.id.checkbox;
        layouts.seekbar = R.id.seekbar;
        
        adapter = provider.setPrefsAdapter (layouts, items);
        
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
              
              case Const.PREF_UPDATE_TIME: {
                
                UI.dialog ((Activity) context, dialogStyle, R.string.title_input, R.layout.dialog_input_descr, new UI.DialogViewNegInterface () {
                  
                  @Override
                  public View onView (AlertDialog.Builder builder, View dialogView) {
                    
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
                
              }
              
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
    
    private String updateDescr (int num) {
      
      String output;
      
      if (num == 0)
        output = context.getString (R.string.interval_never);
      else if (num == 1)
        output = context.getString (R.string.interval_minute_0);
      else
        output = pro.acuna.jabadaba.Strings.suffix (num, new String[] {
          
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
    
    private void buildWidget (Activity activity) throws WidgetsManagerException {
      
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
    
    private void buildWidget (Activity activity, int widgetId) {
      
      Intent intent = new Intent (activity, widgetProvider);
      
      intent.setAction (AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {widgetId});
      
      activity.sendBroadcast (intent);
      
      Intent resultValue = new Intent ();
      
      resultValue.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
      
      activity.setResult (Activity.RESULT_OK, resultValue);
      
    }
        
        /*private Object getPref (String key) {
          
          if (prefItems.get (key) == null)
            prefItems.put (key, defPrefs.get (key));
          
          return prefItems.get (key);
          
        }*/
    
    public void editWidget (Activity activity) throws WidgetsManagerException {
      
      Object oldSize = prefItems.get (Const.PREF_LIST_TEXT_SIZE);
      if (oldSize == null) oldSize = prefs.getInt (Const.PREF_LIST_TEXT_SIZE);
      
      String[] names = new String[] {Const.PREF_LIST_DATE_SIZE, Const.PREF_LIST_TITLE_SIZE, Const.PREF_LIST_AUTHOR_SIZE};
      
      for (String name : names) {
        
        if (oldSize.equals (defPrefs.get (Const.PREF_LIST_TEXT_SIZE)))
          prefItems.put (name, defPrefs.get (name));
        else
          prefItems.put (name, Int.floor (Int.prop (((Integer) oldSize).doubleValue (), ((Integer) prefs.getInt (Const.PREF_LIST_TEXT_SIZE)).doubleValue (), ((Integer) prefs.getInt (name)).doubleValue ())));
        
      }
      
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
      
      AppsManager.close (context);
      
    }
    
    private void saveSettings () {
      
      for (String key : prefItems.keySet ())
        prefs.set (key, prefItems.get (key));
      
      prefs.editor.apply ();
      
    }
    
    public void updateWidget (AppWidgetManager appWidgetManager, int appWidgetId) throws WidgetsManagerException {
      
      if (provider != null) {
        
        RemoteViews remoteView = new RemoteViews (context.getPackageName (), getTheme ());
        
        Layouts item = provider.getLayouts ();
        
        try {
          
          //if (prefs.getString (PREF_ACTION).equals ("build"))
          appWidgetManager.updateAppWidget (appWidgetId, buildWidget (remoteView, appWidgetManager));
          //else
          //	appWidgetManager.updateAppWidget (appWidgetId, buildWidget (remoteView, appWidgetManager));
          
        } catch (IllegalArgumentException e) {
          remoteView.setTextViewText (item.widgetTitle, e.getMessage ());
        }
        
      }
      
    }
    
    int getTheme () throws WidgetsManagerException {
      return provider.setThemes (new LinkedHashMap<String, Integer> ()).get (prefs.getString (Const.PREF_THEME));
    }
    
    int getListTheme () throws WidgetsManagerException {
      return provider.setListThemes (new LinkedHashMap<String, Integer> ()).get (prefs.getString (Const.PREF_LIST_THEME));
    }
    
    private int getColsNum (int size) {
      return Int.floor (((size - 30) / 70));
    }
    
    private int getWidthColsNum () {
      return getColsNum (widgetInfo.minWidth);
    }
    
    private int getHeightColsNum () {
      return getColsNum (widgetInfo.minHeight);
    }
    
    public void resizeWidget (AppWidgetManager appWidgetManager, int appWidgetId, Bundle widgetInfo) throws WidgetsManagerException {
          
          /*prefItems.put (Const.PREF_WIDTH, widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH));
          prefItems.put (Const.PREF_HEIGHT, widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT));
          
          prefItems.put (Const.PREF_ACTION, "build");
          
          saveSettings ();
          updateWidget (appWidgetManager, appWidgetId);
          
          prefItems.put (Const.PREF_ACTION, "update");
          
          saveSettings ();*/
      
    }
    
    public void onDeleted (int id) throws WidgetsManagerException {
      
      Intent intent = new Intent (context, widgetProvider);
      
      intent.setAction (AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, id);
      
      new OS.Service (context)
        .setIntent (PendingIntent.getBroadcast (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
        .init ()
        .stop ();
      
    }
    
    public AppWidgetManager appWidgetManager () {
      return AppWidgetManager.getInstance (context);
    }
    
    public int[] getWidgetsIds () {
      
      ComponentName widgetComponent = new ComponentName (context, widgetProvider);
      return appWidgetManager ().getAppWidgetIds (widgetComponent);
      
    }
    
    public void onReceive (AppWidgetProvider provider, Intent intent) {
      
      String action = intent.getAction ();
      
      if (action.equals (AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
        
        int widgetId = intent.getIntExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        
        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
          provider.onUpdate (context, appWidgetManager (), new int[] {widgetId});
        
      }
      
    }
    
  }