	package pro.acuna.widgetsmanager;
	/*
	 Created by Acuna on 11.07.2018
	*/
	
	public class WidgetsManagerException extends Exception {
		
		public WidgetsManagerException (Exception e) {
			super (e.getMessage ());
		}
		
		WidgetsManagerException (String msg) {
			super (msg);
		}
		
		@Override
		public Exception getCause () {
			return (Exception) super.getCause ();
		}
		
	}