import com.sm.localstore.impl.HessianSerializer
import com.sm.storage.Persistence
import com.sm.store.CursorPara
import com.sm.store.client.*
import com.sm.store.client.netty.NTRemoteClientImpl
import com.sm.store.client.netty.ScanClientImpl
import voldemort.store.cachestore.Key
import voldemort.store.cachestore.Value
import voldemort.store.cachestore.voldeimpl.KeyValue
import com.sm.message.Invoker;

class Utils {
    public static final HessianSerializer serializer = new HessianSerializer();

    static RemoteClientImpl open(String url, String store) {
        return new NTRemoteClientImpl( url, null, store, false)
    }

    static ScanClientImpl openScan(String url, String store) {
        return new ScanClientImpl( url, null, store, 10, false)
    }

    static List<KeyValue> scan(ScanClientImpl client, from, to) {
        List<KeyValue> kv = client.scan( Key.createKey(from), Key.createKey(to))
        List<KeyValue> lst = new ArrayList<KeyValue>()
        for (KeyValue each : kv ) {
            lst.add( new KeyValue(each.getKey(), new RemoteValue( serializer.toObject( each.value.data), each.value.version, each.value.node)))
        }
        return lst
    }

    static List scanv(ScanClientImpl client, from, to) {
        List<KeyValue> kv = client.scan( Key.createKey(from), Key.createKey(to))
        List lst = new ArrayList<KeyValue>()
        for (KeyValue each : kv ) {
            lst.add( serializer.toObject( each.value.data))
        }
        return lst
    }

    static CursorPara scanCursor(ScanClientImpl client, from, to)  {
        return client.nextCursor( new CursorPara(client.getStore(),  (short) 20, from, to))
    }

    static CursorPara nextCursor(ScanClientImpl client, CursorPara nextCursor ) {
        return client.nextCursor( nextCursor)
    }

    static ClusterClient openCluster(String url, String store) {
        ClusterClientFactory ccf = ClusterClientFactory.connect(url, store, TCPClientFactory.ClientType.Netty);
        return ccf.getDefaultStore( 60000, false)
    }

    static List<Key> listKey(RemoteClientImpl client) {
        return client.getKeyList()
    }

    static Value get(Persistence persistence, k) {
        return persistence.get(Key.createKey(k))
    }

    static def getv(Persistence persistence, k) {
        return persistence.get(Key.createKey(k)).data
    }

    static void put(Persistence persistence, k, object) {
        persistence.put(Key.createKey(k), object)
    }

    static List<KeyValue> mget(ScanClientImpl client, keyList) {
        List keys = []
        keyList.each{ it ->  keys.add(Key.createKey( it)) }
        List<KeyValue> kv  = client.multiGets( keys)
        List<KeyValue> lst = new ArrayList<KeyValue>()
        for (KeyValue each : kv ) {
            lst.add( new KeyValue(each.getKey(), new RemoteValue( serializer.toObject( each.value.data), each.value.version, each.value.node)))
        }
        return lst
    }

    static List mgetv(ScanClientImpl client, keyList) {
        List keys = []
        keyList.each{ it ->  keys.add(Key.createKey( it)) }
        List<KeyValue> kv  = client.multiGets( keys)
        List lst = []
        for (KeyValue each : kv ) {
            lst.add( serializer.toObject( each.value.data) )
        }
        return lst
    }

    static boolean remove(Persistence persistence, k) {
        return persistence.remove(Key.createKey(k))
    }

    static String query(RemoteClientImpl client, String query) {
        return client.query4Json( query)
    }

    static List<KeyValue> query4List(RemoteClientImpl client, String query) {
        return client.query( query)
    }

    static List query4v(RemoteClientImpl client, String query) {
        List<KeyValue> kv = client.query( query)
        List lst = []
        for (KeyValue each : kv ) {
            lst.add( each.value.data)
        }
        return lst
    }

    static def invoke(RemoteClientImpl client, String targetClass, String targetMethod, Object... invokerParams) {
        Invoker invoker = new Invoker(targetClass, targetMethod, invokerParams);
        return client.invoke(invoker);
    }

}
