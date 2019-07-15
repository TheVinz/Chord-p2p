package resource;

public interface DistributedHashTable {

    void publish(ChordResource resource);

    ChordResource fetch(String name);
}
