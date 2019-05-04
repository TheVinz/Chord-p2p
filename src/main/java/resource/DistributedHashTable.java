package resource;

// TODO concrete implementation
public interface DistributedHashTable {

    void publish(ChordResource resource);

    ChordResource fetch(String name);
}
