package plug.popup.actions;

public class Context {
	String link;
	String context;
	
	String getLink()
	{
		return link;
	}
	String getContext()
	{
		return context;
	}
	
	@Override
	   public String toString() {
	        return ("Link:"+this.getLink()+
	                    " Context: "+ this.getContext());
	   }

}
