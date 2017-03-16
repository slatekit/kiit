# scala-slate
A scala micro-framework

```scala
    import slate.core.datasource._
    
    def test() = 
    {
        //<doc:example>
        // Get instance of sample datasource ( creating new one here for sample purposes )
        print("Ran Example_Datasource from samples");
            
        val dataSource = new RssDataSource();
        
        // Use case 1: reload data
        val feed = dataSource.reload[RssFeed]();
        print("data reloaded : " + feed.title);

        // Use case 2: another call to only get last data ( without reloading )
        // Note this could return null if "reload" was not called.
        val feed2 = dataSource.get[RssFeed]();
        print("data loaded from cache : " + feed2.title);

        // Use case 3: either get already loaded data or reload if not loaded.
        RssFeed feed3 = dataSource.getOrReload[RssFeed]();
        print("data fetched from cache if available or reloaded : " + feed.Title);

        // Use case 4: check if data is available
        print("check if data available : " + dataSource.isAvailable() );
        
        // Use case 5: check if data source is cacheable ( if get will always reload )
        Print("check if data is cacheable : " + dataSource.isCacheble() );

        // Use case 6: check if the call to reload requires an asynchronous call.
        print ( dataSource.isAsync() );
        
        // Use case 7. if reload is asynchronous... subscribe to get the data when available
        // note: you must implemement DataSourceCallback in your class to get notificed.
        // see sample below.
        if(dataSource.isAsync())
        {
            dataSource.subscribe(this);
        }
        
        // Use case 8: you may want to explicityly notify or re-notify a subscriber of the data source.
        dataSource.notifySubscribers();
        print("subscriber of data notified");
    }
    
    
    def onDataSourceAvailable(sender: AnyRef, eventArgs: DataSourceEventArgs) = {
        // handle data result here.
    }

    
    def OnDataSourceError(sender: AnyRef, eventArgs: DataSourceEventArgs) = {
        // handle data loading error here
    }
    
    
    class RssFeed(var title:String)
    {
        def entries:Seq[String] = null
        
        def load() =
        {
            title = "Sample RSS Feed";
            entries = new Seq("article 1", "article 2")
        }
    }
    
    
    
```
