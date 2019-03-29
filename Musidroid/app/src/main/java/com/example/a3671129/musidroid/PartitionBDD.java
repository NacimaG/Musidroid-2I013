package com.example.a3671129.musidroid;

import l2i013.musidroid.model.Partition;

public class PartitionBDD {


    private int instants;
    private int id;

    public Partition getPartition() {
        return partition;
    }

    private Partition partition;
    private String name="test";

    public PartitionBDD(Partition partition, int id)  {
        this.partition=partition;
        this.id=id;

    }

    public PartitionBDD(Partition partition, int id, String name, int instants) {
        this.partition = partition;
        this.id = id;
        this.name = name;
        this.instants = instants;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString () {
        return name;
    }

    public int getInstants() {
        return instants;
    }
}
