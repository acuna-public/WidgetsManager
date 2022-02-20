	package ru.ointeractive.widgetsmanager;
	/*
	 Created by Acuna on 10.07.2018
	*/
	
	import android.appwidget.AppWidgetManager;
	import android.content.Intent;
	import android.graphics.Bitmap;
	import android.text.SpannableString;
	import android.view.View;
	import android.widget.RemoteViews;
	import android.widget.RemoteViewsService;
	
	import java.util.List;
	
	import ru.ointeractive.andromeda.Strings;
	import ru.ointeractive.andromeda.System;
	import upl.core.Int;
	
	public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
		
		public static class ListItem {
			
			public String date = "", title = "", author = "", text = "";
      public Bitmap image;
      
		}
		
		private WidgetsManager manager;
		private List<ListProvider.ListItem> items;
    
    ListProvider (WidgetsManager manager) throws WidgetsManagerException {
			
			this.manager = manager;
			items = manager.provider.getItems ();
			
		}
		
		@Override
		public int getCount () {
			return Int.size (items);
		}
		
		@Override
		public long getItemId (int position) {
			return position;
		}
		
		@Override
		public RemoteViews getViewAt (int position) {
			
			Intent intent = new Intent ();
			
			intent.putExtra (AppWidgetManager.EXTRA_APPWIDGET_ID, manager.widgetId);
			intent.putExtra ("position", position);
			
			RemoteViews remoteView = null;
			
			ListItem item = items.get (position);
      WidgetsManager.Layouts layouts = manager.provider.getLayouts ();
      
			try {
				
				remoteView = new RemoteViews (manager.context.getPackageName (), manager.getListTheme ());
				
				remoteView = manager.provider.setListRemoteView (remoteView, intent);
				
				if (item.image != null && layouts.image > 0)
					remoteView.setImageViewBitmap (layouts.image, item.image);
				
				SpannableString text;
				
				//remoteView.setInt (item.layouts[5], "setBackgroundResource", manager.prefs.getColor (WidgetsManager.PREF_LIST_DATE_COLOR));
				
				if (!manager.prefs.getBool (Const.PREF_LIST_HIDE_DATE) && Int.size (item.date) > 0) {
					
					remoteView.setViewVisibility (R.id.date, View.VISIBLE);
					
					remoteView.setFloat (R.id.date, "setTextSize", manager.prefs.getInt (Const.PREF_LIST_DATE_SIZE));
					remoteView.setInt (R.id.date, "setTextColor", manager.prefs.getColor (Const.PREF_LIST_DATE_COLOR));
					
					text = new SpannableString (item.date);
					
					text = Strings.setFontTypeface (text, manager.prefs.getInt (Const.PREF_LIST_DATE_STYLE));
					text = Strings.setFontFamily (text, manager.prefs.getString (Const.PREF_LIST_DATE_FONT_FAMILY));
					
					remoteView.setTextViewText (R.id.date, text);
					
				} else remoteView.setViewVisibility (R.id.date, View.GONE);
				
				if (Int.size (item.title) > 0) {
					
					remoteView.setOnClickFillInIntent (layouts.title, intent);
					
					remoteView.setFloat (layouts.title, "setTextSize", manager.prefs.getInt (Const.PREF_LIST_TITLE_SIZE));
					remoteView.setInt (layouts.title, "setTextColor", manager.prefs.getColor (Const.PREF_LIST_TITLE_COLOR));
					
					text = new SpannableString (item.title);
					
					text = Strings.setFontTypeface (text, manager.prefs.getInt (Const.PREF_LIST_TITLE_STYLE));
					text = Strings.setFontFamily (text, manager.prefs.getString (Const.PREF_LIST_TITLE_FONT_FAMILY));
					
					remoteView.setTextViewText (layouts.title, text);
					
				}
				
				if (Int.size (item.author) > 0) {
					
					remoteView.setFloat (layouts.author, "setTextSize", manager.prefs.getInt (Const.PREF_LIST_AUTHOR_SIZE));
					remoteView.setInt (layouts.author, "setTextColor", manager.prefs.getColor (Const.PREF_LIST_AUTHOR_COLOR));
					
					text = new SpannableString (item.author);
					
					text = Strings.setFontTypeface (text, manager.prefs.getInt (Const.PREF_LIST_AUTHOR_STYLE));
					text = Strings.setFontFamily (text, manager.prefs.getString (Const.PREF_LIST_AUTHOR_FONT_FAMILY));
					
					remoteView.setTextViewText (layouts.author, text);
					
				}
				
				if (!manager.prefs.getBool (Const.PREF_LIST_HIDE_COMMENT) && Int.size (item.text) > 0) {
					
					remoteView.setOnClickFillInIntent (layouts.text, intent);
					
					remoteView.setFloat (layouts.text, "setTextSize", manager.prefs.getInt (Const.PREF_LIST_TEXT_SIZE));
					remoteView.setInt (layouts.text, "setTextColor", manager.prefs.getColor (Const.PREF_LIST_TEXT_COLOR));
					
					text = new SpannableString (item.text);
					
					text = Strings.setFontTypeface (text, manager.prefs.getInt (Const.PREF_LIST_TEXT_STYLE));
					text = Strings.setFontFamily (text, manager.prefs.getString (Const.PREF_LIST_TEXT_FONT_FAMILY));
					
					remoteView.setTextViewText (layouts.text, text);
					
				} else remoteView.setViewVisibility (R.id.text, View.GONE);
				
			} catch (WidgetsManagerException e) {
				remoteView.setTextViewText (layouts.text, System.error (manager.context, e, manager.widgetId));
			}
			
			return remoteView;
			
		}
		
		@Override
		public RemoteViews getLoadingView () {
			return null;
		}
		
		@Override
		public int getViewTypeCount () {
			return 1;
		}
		
		@Override
		public boolean hasStableIds () {
			return true;
		}
		
		@Override
		public void onCreate () {}
		
		@Override
		public void onDataSetChanged () {}
		
		@Override
		public void onDestroy () {}
		
	}