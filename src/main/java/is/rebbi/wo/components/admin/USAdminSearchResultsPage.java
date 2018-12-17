package is.rebbi.wo.components.admin;

import java.util.List;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.components.USBaseComponent;

public class USAdminSearchResultsPage extends USBaseComponent {
    
    public DataObject item;
    public List<DataObject> list;

    public USAdminSearchResultsPage( WOContext context ) {
        super( context );
    }
}